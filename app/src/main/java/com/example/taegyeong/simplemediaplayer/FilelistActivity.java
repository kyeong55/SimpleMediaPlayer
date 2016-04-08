package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        location = (TextView) findViewById(R.id.location);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

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

//        fileListAdapter = new FileListAdapter(getApplicationContext(),location);

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

    public class ScanTask extends AsyncTask<Context, Void, Void> {
        @Override
        public Void doInBackground(Context... params) {
            fileListAdapter = new FileListAdapter(params[0],location);
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
