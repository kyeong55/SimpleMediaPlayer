package com.example.taegyeong.simplemediaplayer;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileListActivity extends AppCompatActivity {

    private View pageLoading;
    private View pageSelect;
    private TextView fileListTitle;
    private TextView fileListLocation;
    private FileListAdapter fileListAdapter;

    private FileTracker fileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        pageLoading = findViewById(R.id.main_loading_page);
        pageSelect = findViewById(R.id.main_select_page);

        TextView loadingTitle1 = (TextView) findViewById(R.id.loading_title1);
        TextView loadingTitle2 = (TextView) findViewById(R.id.loading_title2);
        TextView loadingDetail = (TextView) findViewById(R.id.loading_detail);

        TextView selectTitle1 = (TextView) findViewById(R.id.select_title);
        View selectImageButton = findViewById(R.id.select_image_button);
        View selectMusicButton = findViewById(R.id.select_image_button);
        View selectVideoButton = findViewById(R.id.select_image_button);
        TextView selectImageTitle = (TextView) findViewById(R.id.select_image_title);
        TextView selectMusicTitle = (TextView) findViewById(R.id.select_music_title);
        TextView selectVideoTitle = (TextView) findViewById(R.id.select_video_title);

        fileListTitle = (TextView) findViewById(R.id.filelist_title);
        fileListLocation = (TextView) findViewById(R.id.filelist_location);

        assert pageLoading != null;
        assert pageSelect != null;
        assert loadingTitle1 != null;
        assert loadingTitle2 != null;
        assert loadingDetail != null;
        assert selectTitle1 != null;
        assert selectImageButton != null;
        assert selectMusicButton != null;
        assert selectVideoButton != null;
        assert selectImageTitle != null;
        assert selectMusicTitle != null;
        assert selectVideoTitle != null;
        assert fileListTitle != null;
        assert fileListLocation != null;

        SMPCustom.branBlack = Typeface.createFromAsset(getAssets(), "brandon_blk.otf");
        SMPCustom.branBold = Typeface.createFromAsset(getAssets(), "brandon_bld.otf");
        SMPCustom.branRegular = Typeface.createFromAsset(getAssets(), "brandon_med.otf");
        SMPCustom.branLight = Typeface.createFromAsset(getAssets(), "brandon_reg.otf");
        fileListTitle.setTypeface(SMPCustom.branBold);
        fileListLocation.setTypeface(SMPCustom.branRegular);

        loadingTitle1.setTypeface(SMPCustom.branLight);
        loadingTitle2.setTypeface(SMPCustom.branBold);
        loadingDetail.setTypeface(SMPCustom.branRegular);

        selectTitle1.setTypeface(SMPCustom.branBold);
        selectImageTitle.setTypeface(SMPCustom.branBold);
        selectMusicTitle.setTypeface(SMPCustom.branBold);
        selectVideoTitle.setTypeface(SMPCustom.branBold);

        ScanTask task = new ScanTask();
        task.execute();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileType(SMPCustom.TYPE_IMAGE);
            }
        });
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileType(SMPCustom.TYPE_MUSIC);
            }
        });
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileType(SMPCustom.TYPE_VIDEO);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (pageSelect.getVisibility()==View.VISIBLE)
            super.onBackPressed();
        else if (!fileListAdapter.returnBack())
            backToSelect();
    }

    public void backToSelect(){
        pageSelect.setVisibility(View.VISIBLE);
        fileListAdapter = null;
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

        switch (fileType){
            case SMPCustom.TYPE_IMAGE:
                fileListTitle.setText("IMAGE FILES");
                break;
            case SMPCustom.TYPE_MUSIC:
                fileListTitle.setText("MUSIC FILES");
                break;
            case SMPCustom.TYPE_VIDEO:
                fileListTitle.setText("VIDEO FILES");
                break;
        }

        pageSelect.setVisibility(View.GONE);
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
