package com.example.taegyeong.simplemediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MusicPlayActivity extends AppCompatActivity {

    private String filePath;
    private MusicPlayService musicPlayService;
    Intent musicPlayIntent;

    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        Button b1 = (Button) findViewById(R.id.button1);

        assert b1 != null;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = musicPlayService.musicPlay();
            }
        });

        filePath = getIntent().getStringExtra("filePath");
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicPlayIntent = new Intent(this, MusicPlayService.class);
        musicPlayIntent.putExtra("filePath", filePath);
        startService(musicPlayIntent);
        bindService(musicPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
        isPlaying = true;
    }

    @Override
    protected void onStop() {
        if(!isPlaying) {
            stopService(musicPlayIntent);
        }
        super.onStop();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            musicPlayService = ((MusicPlayService.MusicPlayBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
