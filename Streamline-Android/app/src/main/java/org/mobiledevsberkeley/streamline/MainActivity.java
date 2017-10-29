package org.mobiledevsberkeley.streamline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button connectButton;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = (Button) findViewById(R.id.connectButton);


        //restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean autologgin = settings.getBoolean("autologgin", false);

        if(autologgin){
            connectButton.setVisibility(View.GONE);
            SpotifyUtils.askToAuthenticate(MainActivity.this);
        }
        else {
            connectButton.setOnClickListener(this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        SpotifyUtils.authenticate(MainActivity.this, requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connectButton:
                SpotifyUtils.askToAuthenticate(MainActivity.this);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("autologgin", true);
                editor.apply();
                break;
        }
    }

}