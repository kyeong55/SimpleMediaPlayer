package com.example.taegyeong.simplemediaplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private FileListAdapter fileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        TextView location = (TextView) findViewById(R.id.location);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        fileListAdapter = new FileListAdapter(getApplicationContext(),location);

        RecyclerView fileListView = (RecyclerView) findViewById(R.id.filelist);
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
}
