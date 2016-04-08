package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by taegyeong on 16. 4. 8..
 */

public class FileListAdapter extends  RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final String root = "/storage";

    private Context context;
    private TextView location;
    private FileTracker fileTracker;

    public FileListAdapter(Context context, TextView location){
        fileTracker = new FileTracker();
        this.context = context;
        this.location = location;
        this.location.setText(fileTracker.getDirName());
    }

    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, final int position) {
        holder.name.setText(fileTracker.getFileName(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileTracker.openDir(position)){
                    location.setText(fileTracker.getDirName());
                    notifyDataSetChanged();
                }
                else
                    Toast.makeText(context, "여는파일이 아님", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileTracker.getFileNum();
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