package com.example.taegyeong.simplemediaplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taegyeong on 16. 4. 8..
 */
public class FileListAdapter extends  RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void update(List<String>){
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.filelist_elem_name);
        }
    }
}