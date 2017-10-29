package org.mobiledevsberkeley.streamline;

import android.app.Activity;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;

import static android.R.attr.duration;

/**
 * Created by nzp & Kaden on 10/14/17.
 *
 * All code related to the Spotify API.
 */

public class SpotifyUtils {

    //Constants
    public static final String CLIENT_ID = "0b5db977f8424bb6a7d234a3150317d9";
    public static final int AUTH_REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://mobiledevsberkeley.org/streamline-android/callback/";
    public static final String TAG_AUTH = "AUTH";

    //Spotify static variables we care about
    private static boolean isLoggedIn;
    private static Player player;
    private static String accessToken;
    private static ConnectionStateCallback connectionStateCallback;
    private static Player.NotificationCallback notificationCallback;

    //Methods

    public static void askToAuthenticate(Activity activity) {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setShowDialog(true).setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(activity, AUTH_REQUEST_CODE, request);
    }

    public static void authenticate(final Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode == SpotifyUtils.AUTH_REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(activity, response.getAccessToken(), SpotifyUtils.CLIENT_ID);
                Spotify.getPlayer(playerConfig, activity, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        login(activity, response, spotifyPlayer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public static void login(Activity activity, AuthenticationResponse response, SpotifyPlayer spotifyPlayer) {
        accessToken = response.getAccessToken();
        player = spotifyPlayer;
        isLoggedIn = true;
        connectionStateCallback = new SpotifyConnectionCallback();
        notificationCallback = new SpotifyNotificationCallback();
        player.addConnectionStateCallback(connectionStateCallback);
        player.addNotificationCallback(notificationCallback);
        new UserDataFetcher(activity).execute();
    }

    public static void logout() {
        accessToken = null;
        if (player != null) {
            player.logout();
        }
        isLoggedIn = false;
        player = null;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static Player getPlayer() {
        return player;
    }

    public static String getToken() {
        return accessToken;
    }

    //PLAYER CONTROLS

    public static void play(String uri) {
        getPlayer().playUri(null, "spotify:track:" + uri, 0, 0);
    }

    public static void pause() {
        getPlayer().pause(null);
    }

    public static void resume() {
        getPlayer().resume(null);
    }

    public static boolean isPlaying() {
        return getPlayer().getPlaybackState().isPlaying;
    }

    public static void skipToNext() {
        getPlayer().skipToNext(null);
    }

    public static void skipToPrev() {
        getPlayer().skipToPrevious(null);
    }

    public static void addToQueue(String uri) {
        getPlayer().queue(null, "spotify:track" + uri);
    }

    public static class UserDataFetcher extends AsyncTask<Void, Object, UserPrivate> {

        Activity activity;

        public UserDataFetcher(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected UserPrivate doInBackground(Void... voids) {
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(accessToken);
            return api.getService().getMe();
        }

        @Override
        protected void onPostExecute(UserPrivate user) {
            FirebaseUtils.addUserToDatabase(user.id, user.display_name);
            activity.startActivity(new Intent(activity.getApplicationContext(), SliderActivity.class));
        }
    }

    public static class SpotifyNotificationCallback implements Player.NotificationCallback {

        @Override
        public void onPlaybackEvent(PlayerEvent playerEvent) {
            Log.d(TAG_AUTH, "Received playback event: " + playerEvent.name());
        }

        @Override
        public void onPlaybackError(Error error) {
            Log.d(TAG_AUTH, "Received playback error:" + error.name());
        }

    }

    public static class SpotifyConnectionCallback implements ConnectionStateCallback {

        @Override
        public void onLoggedIn() {
            Log.d(TAG_AUTH, "Logged in");
        }

        @Override
        public void onLoggedOut() {
            Log.d(TAG_AUTH, "Logged out");
        }

        @Override
        public void onLoginFailed(Error error) {
            Log.d(TAG_AUTH, "Could not log in");
        }

        @Override
        public void onTemporaryError() {
            Log.d(TAG_AUTH, "Temporary error occurred");
        }

        @Override
        public void onConnectionMessage(String s) {
            Log.d(TAG_AUTH, "Received connection message: " + s);
        }
    }

    public static class searchFetcher extends AsyncTask<Void, Object, TracksPager> {
        String query;
        SearchAdapter adapter;
        RecyclerView rv;

        public searchFetcher(String query, SearchAdapter adapter, RecyclerView rv) {
            this.query = query;
            this.rv = rv;
            this.adapter = adapter;
        }

        @Override
        protected TracksPager doInBackground(Void... voids) {
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(accessToken);
            return api.getService().searchTracks(query);
        }

        @Override
        protected void onPostExecute(TracksPager tracks) {
            if(SearchActivity.results != null) {
                SearchActivity.results.clear();
            }
            SearchActivity.results.addAll(tracks.tracks.items);
            adapter.notifyDataSetChanged();
        }
    }





}

