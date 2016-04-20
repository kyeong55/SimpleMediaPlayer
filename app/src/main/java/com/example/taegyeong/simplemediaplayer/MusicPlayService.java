package com.example.taegyeong.simplemediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayService extends Service {

    private final IBinder mBinder = new MusicPlayBinder();
    public MediaPlayer mediaPlayer;
    private String filePath;
    public class MusicPlayBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String newPath = intent.getStringExtra("filePath");
        if (filePath == null){
            musicLoad(newPath);
        }
        else if (newPath.compareTo(filePath) != 0){
            musicLoad(newPath);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy(){
        mediaPlayer.pause();
        super.onDestroy();
    }

    private boolean musicLoad(String path){
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            filePath = path;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSelf();
                }
            });
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean musicPlay(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            return false;
        }
        else {
            mediaPlayer.start();
            return true;
        }
    }
}
