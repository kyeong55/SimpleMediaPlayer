package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileListActivity extends AppCompatActivity {

    private FileListAdapter fileListAdapter;
    private TextView location;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = (TextView) findViewById(R.id.filelist_title);
        location = (TextView) findViewById(R.id.filelist_location);
        progressBar = (ProgressBar) findViewById(R.id.filelist_progressbar);
        TextView name1 = (TextView) findViewById(R.id.main_name1);
        TextView name2 = (TextView) findViewById(R.id.main_name2);
//        TextView name3 = (TextView) findViewById(R.id.main_name3);
        TextView loading = (TextView) findViewById(R.id.main_loading);


        assert title != null;
        assert location != null;
        assert progressBar != null;

        SMPCustom.branBlack = Typeface.createFromAsset(getAssets(), "brandon_blk.otf");
        SMPCustom.branBold = Typeface.createFromAsset(getAssets(), "brandon_bld.otf");
        SMPCustom.branRegular = Typeface.createFromAsset(getAssets(), "brandon_med.otf");
        SMPCustom.branLight = Typeface.createFromAsset(getAssets(), "brandon_reg.otf");
        title.setTypeface(SMPCustom.branBold);
        location.setTypeface(SMPCustom.branRegular);

        name1.setTypeface(SMPCustom.branLight);
        name2.setTypeface(SMPCustom.branBold);
        loading.setTypeface(SMPCustom.branRegular);
//        name3.setTypeface(SMPCustom.branBold);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        fileListAdapter = new FileListAdapter(getApplicationContext(),location);
//
//        RecyclerView fileListView = (RecyclerView) findViewById(R.id.filelist);
//        fileListView.setHasFixedSize(true);
//        fileListView.setLayoutManager(layoutManager);
//        fileListView.setAdapter(fileListAdapter);

        ScanTask task = new ScanTask();
        task.execute(getApplicationContext());
    }

    public void setList(){
        progressBar.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView fileListView = (RecyclerView) findViewById(R.id.filelist_list);
        assert fileListView != null;
        fileListView.setHasFixedSize(true);
        fileListView.setLayoutManager(layoutManager);
        fileListView.setAdapter(fileListAdapter);
        fileListView.setVerticalScrollBarEnabled(true);

    }

    @Override
    public void onBackPressed(){
        if (!fileListAdapter.returnBack())
            super.onBackPressed();
    }

    public class ScanTask extends AsyncTask<Context, Void, Void> {
        @Override
        public Void doInBackground(Context... params) {
            FileTracker fileTracker = new FileTracker();
            fileTracker.setFileType(SMPCustom.TYPE_MUSIC);
            fileListAdapter = new FileListAdapter(params[0],location, fileTracker);
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
//            super.onPostExecute(result);
            Log.d("debugging", "execute finished");
            setList();
//            location.setText("asd");
        }
    }
}
