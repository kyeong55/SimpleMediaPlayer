package com.example.taegyeong.simplemediaplayer;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import java.util.ArrayList;

public class VideoPlayActivity extends AppCompatActivity {

    private int position;
    private ArrayList<String> fileList;

    private View decorView;
    private boolean statusbarHidden;

    private VideoView videoView;
    private View controller;
    private ImageView playButton;
    private ImageView pauseButton;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView durationTime;

    private boolean isPlaying = false;
    private boolean playedBeforeSeek;

    private ShowControllerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        position = getIntent().getIntExtra("position", -1);
        fileList = getIntent().getStringArrayListExtra("fileList");

        videoView = (VideoView) findViewById(R.id.video_view);
        View focusView = findViewById(R.id.video_focus);
        controller = findViewById(R.id.video_controller);
        playButton = (ImageView) findViewById(R.id.video_play);
        pauseButton = (ImageView) findViewById(R.id.video_pause);
        ImageView nextButton = (ImageView) findViewById(R.id.video_next);
        ImageView previousButton = (ImageView) findViewById(R.id.video_previous);
        seekBar = (SeekBar) findViewById(R.id.video_seekbar);
        currentTime = (TextView) findViewById(R.id.video_current);
        durationTime = (TextView) findViewById(R.id.video_duration);

        assert videoView != null;
        assert focusView != null;
        assert playButton != null;
        assert pauseButton != null;
        assert nextButton != null;
        assert previousButton != null;
        assert seekBar != null;

        currentTime.setTypeface(SMPCustom.branRegular);
        durationTime.setTypeface(SMPCustom.branRegular);

        videoView.setVideoPath(fileList.get(position));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                isPlaying = true;
                seekBar.setMax(videoView.getDuration());
                durationTime.setText(SMPCustom.getTimeString(videoView.getDuration()));
                new SeekBarThread().start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                task.cancel(false);
                videoSkipNext();
                task = new ShowControllerTask();
                task.execute();
            }
        });
        focusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusbarHidden){
                    showSystemUI();
                    task = new ShowControllerTask();
                    task.execute();
                }
                else{
                    task.cancel(false);
                    hideSystemUI();
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(false);
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.GONE);
                videoView.start();
                isPlaying = true;
                new SeekBarThread().start();
                task = new ShowControllerTask();
                task.execute();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(false);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                videoView.pause();
                isPlaying = false;
                task = new ShowControllerTask();
                task.execute();
            }
        });
        playButton.setVisibility(View.GONE);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(false);
                videoSkipPrevious();
                task = new ShowControllerTask();
                task.execute();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(false);
                videoSkipNext();
                task = new ShowControllerTask();
                task.execute();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                if (playedBeforeSeek) {
                    videoView.start();
                    isPlaying = true;
                    new SeekBarThread().start();
                    task = new ShowControllerTask();
                    task.execute();
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                task.cancel(false);
                videoView.pause();
                playedBeforeSeek = isPlaying;
                isPlaying = false;
            }
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                currentTime.setText(SMPCustom.getTimeString(progress));
            }
        });

        decorView = getWindow().getDecorView();
        showSystemUI();
        task = new ShowControllerTask();
        task.execute();
    }

    public void videoSkipNext(){
        Log.d("debugging","From: "+position);
        if (position < fileList.size() - 1)
            position++;
        else
            position = 0;
        videoView.setVideoPath(fileList.get(position));
        Log.d("debugging","Next: "+position);
    }
    public void videoSkipPrevious(){
        if (position == 0)
            position = fileList.size() - 1;
        else
            position--;
        videoView.setVideoPath(fileList.get(position));
    }

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
        controller.setVisibility(View.GONE);
        statusbarHidden = true;
    }
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        controller.setVisibility(View.VISIBLE);
        statusbarHidden = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class SeekBarThread extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                seekBar.setProgress(videoView.getCurrentPosition());
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ShowControllerTask extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... params) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            hideSystemUI();
        }
    }
}
