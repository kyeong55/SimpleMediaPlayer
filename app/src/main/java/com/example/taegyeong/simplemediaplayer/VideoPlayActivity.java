package com.example.taegyeong.simplemediaplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoPlayActivity extends AppCompatActivity {

    private int position;
    private ArrayList<String> fileList;

    private VideoView videoView;
    private View focusView;
    private View decorView;
    private boolean statusbarHidden;
    private int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        videoView = (VideoView) findViewById(R.id.video_view);
        focusView = findViewById(R.id.video_focus);

        assert videoView != null;
        assert focusView != null;

        position = getIntent().getIntExtra("position", -1);
        fileList = getIntent().getStringArrayListExtra("fileList");

        videoView.setVideoPath(fileList.get(position));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoSkipNext();
            }
        });
//        videoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (statusbarHidden)
//                    decorView.setSystemUiVisibility(0);
//                else
//                    decorView.setSystemUiVisibility(uiOptions);
//            }
//        });
        statusbarHidden = true;
        hideSystemUI();
        focusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusbarHidden){
                    showSystemUI();
                    Log.d("debugging", "show");
                }
                else{
                    hideSystemUI();
                    Log.d("debugging", "hide");
                }
                statusbarHidden = !statusbarHidden;
            }
        });
    }

    public void videoSkipNext(){
        if (position < fileList.size() - 1)
            position++;
        else
            position = 0;
        videoView.setVideoPath(fileList.get(position));
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
