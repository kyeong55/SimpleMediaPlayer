package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by taegyeong on 16. 4. 8..
 */

public class FileListAdapter extends  RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private Context context;
    private TextView location;
    private FileTracker fileTracker;

    public FileListAdapter(Context context, TextView location, FileTracker fileTracker){
        this.context = context;
        this.location = location;
        this.fileTracker = fileTracker;
    }

    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, final int position) {
        location.setText(fileTracker.getDirName());
        holder.name.setText(fileTracker.getFileName(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int filePosition = fileTracker.openFile(position);
                if(filePosition < 0){
                    location.setText(fileTracker.getDirName());
                    notifyDataSetChanged();
                }
                else{
                    if (fileTracker.getSubFileType(position) == FileType.IMAGE){
                        Intent imagePlayIntent = new Intent(context, ImagePlayActivity.class);
                        imagePlayIntent.putExtra("position", filePosition);
                        imagePlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                        imagePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(imagePlayIntent);
                    }
                    else if (fileTracker.getSubFileType(position) == FileType.MUSIC){
                        Intent musicPlayIntent = new Intent(context, MusicPlayActivity.class);
                        musicPlayIntent.putExtra("position", filePosition);
                        musicPlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                        musicPlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(musicPlayIntent);
                    }
                    else if (fileTracker.getSubFileType(position) == FileType.VIDEO){
                        Intent videoPlayIntent = new Intent(context, VideoPlayActivity.class);
                        videoPlayIntent.putExtra("position", filePosition);
                        videoPlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                        videoPlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(videoPlayIntent);
                    }
                    else{
                        Toast.makeText(context, "열수 없는 파일입니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileTracker.getCurrentFileNum();
    }

    public boolean returnBack(){
        if(fileTracker.returnBack()) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.filelist_elem_name);
            layout = itemView;
        }
    }
}