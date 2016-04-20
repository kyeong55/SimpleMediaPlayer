package com.example.taegyeong.simplemediaplayer;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MusicPlayActivity extends AppCompatActivity {
    private MusicPlayService musicPlayService;
    private Intent musicPlayIntent;

    private boolean isPlaying;

    private SeekBar seekBar;
    private ImageView albumArt;
    private TextView title;
    private TextView artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        final ImageView playButton = (ImageView) findViewById(R.id.music_play);
        final ImageView pauseButton = (ImageView) findViewById(R.id.music_pause);
        ImageView nextButton = (ImageView) findViewById(R.id.music_next);
        ImageView previousButton = (ImageView) findViewById(R.id.music_previous);
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);
        albumArt = (ImageView) findViewById(R.id.music_image);
        title = (TextView) findViewById(R.id.music_title);
        artist = (TextView) findViewById(R.id.music_artist);

        assert playButton != null;
        assert pauseButton != null;
        assert nextButton != null;
        assert previousButton != null;
        assert seekBar != null;
        assert albumArt != null;
        assert title != null;
        assert artist != null;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.GONE);
                isPlaying = musicPlayService.musicPlay();
                new SeekBarThread().start();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                isPlaying = musicPlayService.musicPlay();
            }
        });
        playButton.setVisibility(View.GONE);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayService.skipPrevious();
                setUp(musicPlayService.getFilePath());
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayService.skipNext();
                setUp(musicPlayService.getFilePath());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicPlayIntent = new Intent(this, MusicPlayService.class);
        musicPlayIntent.putExtra("fileList", getIntent().getStringArrayListExtra("fileList"));
        musicPlayIntent.putExtra("position", getIntent().getIntExtra("position", -1));
        startService(musicPlayIntent);
        bindService(musicPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
        isPlaying = true;
    }

    @Override
    protected void onStop() {
        musicPlayService.independent();
        if(!isPlaying) {
            stopService(musicPlayIntent);
        }
        unbindService(musicConnection);
        isPlaying = false;
        super.onStop();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            musicPlayService = ((MusicPlayService.MusicPlayBinder) service).getService();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d("debugging","onStopTrackingTouch");
//                            musicPlayService.musicPlay();
                    musicPlayService.seekTo(seekBar.getProgress());
                    isPlaying = true;
                    Thread seekBarThread = new SeekBarThread();
                    seekBarThread.start();
                }
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("debugging","onStartTrackingTouch");
//                            musicPlayService.musicPlay();
                    isPlaying = false;
                    musicPlayService.musicPlay();
                }
                public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
//                    if (fromUser) {
//                        musicPlayService.seekTo(progress);
//                    }
                }
            });
            musicPlayService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    musicPlayService.skipNext();
                    setUp(musicPlayService.getFilePath());
                }
            });
            Thread seekBarThread = new SeekBarThread();
            seekBarThread.start();
            setUp(musicPlayService.getFilePath());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    class SeekBarThread extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                seekBar.setProgress(musicPlayService.getCurrentPosition());
                if(musicPlayService.isChanged()){
//                    musicPlayService.seekTo(0);
                    seekBar.setMax(musicPlayService.getDuration());
                    seekBar.setProgress(0);
                    musicPlayService.notifiedChange();
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setUp(String path){
        Uri fileUri = Uri.parse(path);
        String filePath = fileUri.getPath();
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,"_data ='" + filePath + "'",null,null);
        c.moveToNext();
        if(c.getCount()>0) {
            int id = c.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
            Cursor cur = managedQuery(uri, null, null, null, null);
            if(cur.moveToFirst()) {
                title.setText(cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                artist.setText(cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)));
            }
        }
        c.close();
    }
}
