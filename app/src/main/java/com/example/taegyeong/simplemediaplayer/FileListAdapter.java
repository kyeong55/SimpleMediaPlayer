package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by taegyeong on 16. 4. 8..
 */

public class FileListAdapter extends  RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_ELEM = 1;

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
        View v = null;
        if (viewType == VIEW_TYPE_EMPTY)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem_empty,parent,false);
        else if (viewType == VIEW_TYPE_ELEM)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem,parent,false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        location.setText(fileTracker.getDirName());
        holder.title.setTypeface(SMPCustom.branLight);
        if(viewType == VIEW_TYPE_ELEM) {
            holder.title.setText(fileTracker.getFileName(position));
            switch (fileTracker.getSubFileType(position)){
                case SMPCustom.TYPE_IMAGE:
                    holder.subtitle.setText("");
                    holder.folder.setVisibility(View.GONE);
                    holder.album.setImageDrawable(context.getDrawable(R.drawable.icon_image));
                    break;
                case SMPCustom.TYPE_MUSIC:
                    holder.subtitle.setText("");
                    holder.folder.setVisibility(View.GONE);
                    holder.album.setImageDrawable(context.getDrawable(R.drawable.icon_music));
                    break;
                case SMPCustom.TYPE_VIDEO:
                    holder.subtitle.setText("");
                    holder.folder.setVisibility(View.GONE);
                    holder.album.setImageDrawable(context.getDrawable(R.drawable.icon_video));
                    break;
                case SMPCustom.TYPE_DIR:
                    holder.folder.setVisibility(View.VISIBLE);
                    holder.subtitle.setText(fileTracker.getSubFileNum(position)+" files");
                    break;
                default:
                    holder.subtitle.setText("");
                    break;
            }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int filePosition = fileTracker.openFile(position);
                    if (filePosition < 0) {
                        location.setText(fileTracker.getDirName());
                        notifyDataSetChanged();
                    } else {
                        if (fileTracker.getSubFileType(position) == SMPCustom.TYPE_IMAGE) {
                            Intent imagePlayIntent = new Intent(context, ImagePlayActivity.class);
                            imagePlayIntent.putExtra("position", filePosition);
                            imagePlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                            imagePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(imagePlayIntent);
                        } else if (fileTracker.getSubFileType(position) == SMPCustom.TYPE_MUSIC) {
                            Intent musicPlayIntent = new Intent(context, MusicPlayActivity.class);
                            musicPlayIntent.putExtra("position", filePosition);
                            musicPlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                            musicPlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(musicPlayIntent);
                        } else if (fileTracker.getSubFileType(position) == SMPCustom.TYPE_VIDEO) {
                            Intent videoPlayIntent = new Intent(context, VideoPlayActivity.class);
                            videoPlayIntent.putExtra("position", filePosition);
                            videoPlayIntent.putExtra("fileList", fileTracker.getFilePathList());
                            videoPlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(videoPlayIntent);
                        } else {
                            Toast.makeText(context, "열수 없는 파일입니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (fileTracker.getCurrentFileNum() == 0)
            return 1;
        return fileTracker.getCurrentFileNum();
    }

    @Override
    public int getItemViewType(int position){
        if (fileTracker.getCurrentFileNum() == 0)
            return VIEW_TYPE_EMPTY;
        return VIEW_TYPE_ELEM;
    }

    public boolean returnBack(){
        if(fileTracker.returnBack()) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        ImageView album;
        ImageView folder;
        View layout;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_EMPTY)
                title = (TextView)itemView.findViewById(R.id.filelist_elem_nofile);
            else if (viewType == VIEW_TYPE_ELEM){
                title = (TextView) itemView.findViewById(R.id.filelist_elem_title);
                subtitle = (TextView) itemView.findViewById(R.id.filelist_elem_artist);
                album = (ImageView) itemView.findViewById(R.id.filelist_elem_thumb_album);
                folder = (ImageView) itemView.findViewById(R.id.filelist_elem_thumb_folder);
                layout = itemView;
            }
        }
    }
}