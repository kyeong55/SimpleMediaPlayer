package com.example.taegyeong.simplemediaplayer;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import java.util.ArrayList;

public class MusicPlayService extends Service {

    private final IBinder mBinder = new MusicPlayBinder();
    public MediaPlayer mediaPlayer;
    private String filePath;

    private ArrayList<String> fileList;
    private int position;
    private boolean fileChanged = false;
    public boolean skipping = false;

    public class MusicPlayBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("debugging", "onStartCommand");
        fileList = intent.getStringArrayListExtra("fileList");
        position = intent.getIntExtra("position", -1);
        musicInit(fileList.get(position));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("debugging", "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy(){
        mediaPlayer.pause();
        super.onDestroy();
    }

    private boolean musicLoad(String path){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            filePath = path;
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    skipping = false;
                }
            });
            fileChanged = true;
            return true;
        }catch(Exception e){
            return false;
        }
    }
    private boolean musicInit(String path){
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if(filePath != null){
            if(filePath.compareTo(path) == 0)
                return true;
        }

        if (!musicLoad(path))
            return false;

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                skipNext();
//            }
//        });
        return true;
    }

    public boolean musicPlay(){
        Log.d("debugging","play called");
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            return false;
        }
        else {
            mediaPlayer.start();
            return true;
        }
    }
    public int nextPosition(){
        if (position < fileList.size() - 1)
            return position+1;
        else
            return 0;
    }
    public int prevPosition(){
        if (position == 0)
            return fileList.size() - 1;
        else
            return position-1;
    }
//    public void skipNext(){
//        Log.d("debugging", "position before: "+position);
//        if (position < fileList.size() - 1)
//            position++;
//        else
//            position = 0;
//        musicLoad(fileList.get(position));
//        Log.d("debugging", "position after: "+position);
//    }
//    public void skipPrevious(){
//        Log.d("debugging", "position before: "+position);
//        if (position == 0)
//            position = fileList.size() - 1;
//        else
//            position--;
//        musicLoad(fileList.get(position));
//        Log.d("debugging", "position after: "+position);
//    }
    public void skipTo(int pos){
        Log.d("debugging", "position before: "+position);
        skipping = true;
        if (pos != position){
            position = pos;
            musicLoad(fileList.get(position));
        }
        Log.d("debugging", "position after: "+position);
    }
    public boolean isChanged(){
        return fileChanged;
    }
    public void notifiedChange(){
        fileChanged = false;
    }

    public void seekTo(int time){
        Log.d("debugging", "seekto: "+time+"/ total:"+mediaPlayer.getDuration());
        mediaPlayer.pause();
        mediaPlayer.seekTo(time);
        mediaPlayer.start();
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    public String getFilePath(){
        return filePath;
    }

    public void independent(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debugging", "inde-onCompletion");
                skipTo(nextPosition());
            }
        });
    }

    public int getPosition(){
        return position;
    }
}
