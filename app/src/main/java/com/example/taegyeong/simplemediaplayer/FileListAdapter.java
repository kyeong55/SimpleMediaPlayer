package com.example.taegyeong.simplemediaplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by taegyeong on 16. 4. 8..
 */

class LoadAlbumClass{
    int albumID;
    ImageView icon;
    ImageView album;
    LoadAlbumClass (int albumID, ImageView icon, ImageView album){
        this.albumID = albumID;
        this.icon = icon;
        this.album = album;
    }
}

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
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.album.setVisibility(View.GONE);
                    holder.icon.setImageDrawable(context.getDrawable(R.drawable.icon_image));
                    break;
                case SMPCustom.TYPE_MUSIC:
                    Uri fileUri = Uri.parse(fileTracker.getFilePath(position));
                    String filePath = fileUri.getPath();
                    Cursor c;
                    try {
                        c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "_data ='" + filePath + "'", null, null);
                    }catch (Exception e){
                        c = null;
                    }
                    if (c!= null) {
                        c.moveToNext();
                        if (c.getCount() > 0) {
                            int id = c.getInt(0);
                            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                            CursorLoader cursorLoader = new CursorLoader(context, uri, null, null, null, null);
                            Cursor cur = cursorLoader.loadInBackground();
                            if (cur.moveToFirst()) {
                                holder.subtitle.setText(cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)));
                                int albumID = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                                Bitmap albumArt = getAlbumArt(context, albumID);
                                if (albumArt == null) {
                                    holder.icon.setImageDrawable(context.getDrawable(R.drawable.icon_music));
                                    holder.icon.setVisibility(View.VISIBLE);
                                    holder.album.setVisibility(View.GONE);
                                } else {
                                    holder.album.setImageBitmap(albumArt);
                                    holder.icon.setVisibility(View.GONE);
                                    holder.album.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        c.close();
                    }
                    else{
                        holder.subtitle.setText("unknown");
                        holder.icon.setImageDrawable(context.getDrawable(R.drawable.icon_music));
                        holder.icon.setVisibility(View.VISIBLE);
                        holder.album.setVisibility(View.GONE);
                    }
                    holder.folder.setVisibility(View.GONE);
                    break;
                case SMPCustom.TYPE_VIDEO:
                    holder.subtitle.setText("");
                    holder.folder.setVisibility(View.GONE);
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.album.setVisibility(View.GONE);
                    holder.icon.setImageDrawable(context.getDrawable(R.drawable.icon_video));
                    break;
                case SMPCustom.TYPE_DIR:
                    holder.folder.setVisibility(View.VISIBLE);
                    holder.icon.setVisibility(View.GONE);
                    holder.album.setVisibility(View.GONE);
                    holder.subtitle.setText(fileTracker.getSubDirFileNum(position)+" files");
                    break;
                default:
                    holder.subtitle.setText("");
                    holder.folder.setVisibility(View.GONE);
                    holder.icon.setVisibility(View.GONE);
                    holder.album.setVisibility(View.GONE);
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

    private Bitmap getAlbumArt(Context mContext, long albumId)
    {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(artworkUri, albumId);
        ContentResolver cr = mContext.getContentResolver();
        InputStream in = null;
        try {
            in = cr.openInputStream(uri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap artwork = BitmapFactory.decodeStream(in);
        if (artwork == null)
            return null;
        int scale = 200;
        if (artwork.getWidth() > artwork.getHeight()) {
            if (artwork.getHeight() > scale)
                return Bitmap.createScaledBitmap(artwork, scale*artwork.getWidth()/artwork.getHeight(), scale, false);
            else
                return artwork;
        } else {
            if (artwork.getWidth() > scale)
                return Bitmap.createScaledBitmap(artwork, scale, scale*artwork.getHeight()/artwork.getWidth(), false);
            else
                return artwork;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        ImageView album;
        ImageView icon;
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
                icon = (ImageView) itemView.findViewById(R.id.filelist_elem_thumb_icon);
                folder = (ImageView) itemView.findViewById(R.id.filelist_elem_thumb_folder);
                layout = itemView;
            }
        }
    }
}