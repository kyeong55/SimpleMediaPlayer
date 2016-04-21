package com.example.taegyeong.simplemediaplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FileTracker fileTracker;

    private boolean fileScanFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startFileListActivity();
                }
            });
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        fileScanFinished = false;
//        ScanTask task = new ScanTask();
//        task.execute(getApplicationContext());
    }

    public void startFileListActivity(){
        final Intent fileListIntent = new Intent(this, FileListActivity.class);
        fileListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(fileListIntent);
    }

    public class ScanTask extends AsyncTask<Context, Void, Void> {
        @Override
        public Void doInBackground(Context... params) {
            fileTracker = new FileTracker();
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("debugging", "execute finished: "+fileTracker.getDirName());
            fileScanFinished = true;
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
