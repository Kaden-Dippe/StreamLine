package org.mobiledevsberkeley.streamline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;

public class SliderActivity extends AppCompatActivity implements PanelSlideListener, View.OnClickListener {

    ArrayList<Post> posts = new ArrayList<>();
    final PostAdapter postAdapter = new PostAdapter(SliderActivity.this, posts);

    private Post playingSong;

    private SlidingUpPanelLayout sLayout;
    private ImageView playPauseButton;
    private ImageView skipNextButton;
    private ImageView skipPrevButton;
    private ImageView collapsedPlayPauseButton;
    private ImageView collapsedAlbumArt;
    private TextView collapsedSongTitle;
    private TextView collapsedArtist;
    private TextView usernamePost, timeStampPost, songTitle, songArtist;

    //Sliding layout post information
    private ImageView albumArtCurrent, postUserProfileImage;

    private AudioManager audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //INIT UI elements
        playPauseButton = findViewById(R.id.playPauseButton);
        skipNextButton = findViewById(R.id.skipNextButton);
        skipPrevButton = findViewById(R.id.skipPrevButton);
        collapsedPlayPauseButton = findViewById(R.id.collapsedPlayPauseButton);
        collapsedArtist = findViewById(R.id.collapsedArtist);
        collapsedSongTitle = findViewById(R.id.collapsedSongTitle);
        usernamePost = findViewById(R.id.usernamePost);
        albumArtCurrent = findViewById(R.id.albumArtCurrent);
        postUserProfileImage = findViewById(R.id.postUserProfileImage);
        timeStampPost = findViewById(R.id.timeStampPost);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        collapsedAlbumArt = findViewById(R.id.collapsedAlbumArt);

        //Add UI listeners
        playPauseButton.setOnClickListener(this);
        skipNextButton.setOnClickListener(this);
        skipPrevButton.setOnClickListener(this);
        collapsedPlayPauseButton.setOnClickListener(this);

        //INIT scrollability
        songTitle.setSelected(true);


        //INIT recycler view for the posts
        RecyclerView feed = (RecyclerView) findViewById(R.id.postFeed);
        feed.setLayoutManager(new LinearLayoutManager(this));


        feed.setAdapter(postAdapter);

        //INIT slider panel methods.

        sLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        sLayout.addPanelSlideListener(this);

        //INIT Firebase
        FirebaseUtils.initFeedValueEventListener(SliderActivity.this);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    public void setPlayingSong(Post pS) {
        this.playingSong = pS;
        SpotifyUtils.play(playingSong.getTrackId());
        albumArtCurrent.setImageBitmap(playingSong.getBitmap());
        usernamePost.setText(playingSong.getUsername());
        timeStampPost.setText(Utils.formatTime(playingSong.getTimePosted()));
        songTitle.setText(playingSong.getSongTitle());
        songArtist.setText(playingSong.getArtist());
        playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    //On Click Listener Method
    //TODO implement different functionalities
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sliding_layout:
                break;
            case R.id.playPauseButton:
                if (SpotifyUtils.isPlaying()) {
                    SpotifyUtils.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    SpotifyUtils.resume();
                    playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                }
                break;
            case R.id.collapsedPlayPauseButton:
                break;
            case R.id.skipNextButton:
                SpotifyUtils.skipToNext();
                break;
            case R.id.skipPrevButton:
                SpotifyUtils.skipToPrev();
                break;
            case R.id.favoriteButton:
                Toast.makeText(this, "You've discovered a premium feature!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //Panel Listener Methods.
    //TODO implement code for panel state change w/ bottom buttons.
    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        if (newState == PanelState.COLLAPSED || newState == PanelState.DRAGGING){
            collapsedPlayPauseButton.setVisibility(View.VISIBLE);
            collapsedArtist.setVisibility(View.VISIBLE);
            collapsedSongTitle.setVisibility(View.VISIBLE);
            collapsedAlbumArt.setVisibility(View.VISIBLE);
        }
        if (newState == PanelState.EXPANDED){
            collapsedPlayPauseButton.setVisibility(View.GONE);
            collapsedArtist.setVisibility(View.GONE);
            collapsedSongTitle.setVisibility(View.GONE);
            collapsedAlbumArt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        collapsedPlayPauseButton.setAlpha(1-slideOffset);
        collapsedArtist.setAlpha(1-slideOffset);
        collapsedSongTitle.setAlpha(1-slideOffset);
        collapsedAlbumArt.setAlpha(1-slideOffset);

    }

    @Override
    public void onBackPressed() {
        if (sLayout != null &&
                (sLayout.getPanelState() == PanelState.EXPANDED || sLayout.getPanelState() == PanelState.ANCHORED)) {
            sLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public void addPostToAdapter(Post post) {
        postAdapter.addItem(post);
    }

    public void removePostFromAdapter(Post post) {
        postAdapter.removeItem(post);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        if (sLayout != null) {
            if (sLayout.getPanelState() == PanelState.HIDDEN) {
                //some action
            } else {
                //other action
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.newPost:
                startActivity(new Intent(this.getApplicationContext(), SearchActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}