package com.example.taegyeong.simplemediaplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taegyeong on 16. 4. 8..
 */
class FileInfo{
    public String name;
    public String path;
    public FileInfo(String name, String path){
        this.name = name;
        this.path = path;
    }
}
public class FileListAdapter extends  RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final String root = "/";

    private List<FileInfo> items;
    private TextView location;

    public FileListAdapter(TextView location){
        this.location = location;
        getDir(root);
    }

    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.filelist_elem,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, int position) {
        final FileInfo item = items.get(position);
        holder.name.setText(item.name);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(item.path);
                if(file.isDirectory())
                    getDir(item.path);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void getDir(String dirPath) {
        items = new ArrayList<>();
        location.setText("Location: " + dirPath);
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            items.add(new FileInfo(root,root));
            items.add(new FileInfo("../",f.getParent()));
        }

        for (File file : files) {
            if (file.isDirectory())
                items.add(new FileInfo(file.getName() + "/",file.getPath()));
            else
                items.add(new FileInfo(file.getName(),file.getPath()));
        }

        this.notifyDataSetChanged();
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