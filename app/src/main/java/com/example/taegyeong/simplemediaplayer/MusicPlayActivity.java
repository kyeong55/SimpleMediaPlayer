package com.example.taegyeong.simplemediaplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MusicPlayActivity extends AppCompatActivity {

    private String musicPath;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);

        musicPath = getIntent().getStringExtra("filePath");
        loadAudio(musicPath);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mp.isPlaying())
                    mp.start();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying())
                    mp.pause();
            }
        });
    }

    private boolean loadAudio(String path){
        mp = new MediaPlayer();
        try{
            mp.setDataSource(path);
            mp.prepare();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
