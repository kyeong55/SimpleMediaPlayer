package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileListActivity extends AppCompatActivity {

    private View pageLoading;
    private FileListAdapter fileListAdapter;
    private TextView fileListLocation;

    private FileTracker fileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        pageLoading = findViewById(R.id.main_loading_page);
        TextView loadingTitle1 = (TextView) findViewById(R.id.loading_title1);
        TextView loadingTitle2 = (TextView) findViewById(R.id.loading_title2);
        TextView loadingDetail = (TextView) findViewById(R.id.loading_detail);

        TextView title = (TextView) findViewById(R.id.filelist_title);
        fileListLocation = (TextView) findViewById(R.id.filelist_location);

        assert loadingTitle1 != null;
        assert loadingTitle2 != null;
        assert loadingDetail != null;
        assert title != null;
        assert fileListLocation != null;

        SMPCustom.branBlack = Typeface.createFromAsset(getAssets(), "brandon_blk.otf");
        SMPCustom.branBold = Typeface.createFromAsset(getAssets(), "brandon_bld.otf");
        SMPCustom.branRegular = Typeface.createFromAsset(getAssets(), "brandon_med.otf");
        SMPCustom.branLight = Typeface.createFromAsset(getAssets(), "brandon_reg.otf");
        title.setTypeface(SMPCustom.branBold);
        fileListLocation.setTypeface(SMPCustom.branRegular);

        loadingTitle1.setTypeface(SMPCustom.branLight);
        loadingTitle2.setTypeface(SMPCustom.branBold);
        loadingDetail.setTypeface(SMPCustom.branRegular);

        ScanTask task = new ScanTask();
        task.execute();
    }

    @Override
    public void onBackPressed(){
        if (!fileListAdapter.returnBack())
            super.onBackPressed();
    }

    public void selectFileType(int fileType){
        fileTracker.setFileType(fileType);
        fileListAdapter = new FileListAdapter(getApplicationContext(), fileListLocation, fileTracker);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView fileListView = (RecyclerView) findViewById(R.id.filelist_list);
        assert fileListView != null;
        fileListView.setHasFixedSize(true);
        fileListView.setLayoutManager(layoutManager);
        fileListView.setAdapter(fileListAdapter);
        fileListView.setVerticalScrollBarEnabled(true);
    }

    public class ScanTask extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... params) {
            fileTracker = new FileTracker();
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            pageLoading.setVisibility(View.GONE);
        }
    }
}
