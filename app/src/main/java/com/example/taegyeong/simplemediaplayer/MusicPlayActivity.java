package com.example.taegyeong.simplemediaplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MusicPlayActivity extends AppCompatActivity {
    private MusicPlayService musicPlayService;
    private Intent musicPlayIntent;

    private boolean isPlaying;
    private boolean playedBeforeSeek;

    private SeekBar seekBar;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getIntent().getStringArrayListExtra("fileList"));
        mViewPager = (ViewPager) findViewById(R.id.music_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(loaded)
                    musicPlayService.skipTo(position);
//                ((MusicInfoFragment)mSectionsPagerAdapter.getItem(position)).title.setSelected(true);
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(getIntent().getIntExtra("position", -1));

        final ImageView playButton = (ImageView) findViewById(R.id.music_play);
        final ImageView pauseButton = (ImageView) findViewById(R.id.music_pause);
        ImageView nextButton = (ImageView) findViewById(R.id.music_next);
        ImageView previousButton = (ImageView) findViewById(R.id.music_previous);
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);

        assert playButton != null;
        assert pauseButton != null;
        assert nextButton != null;
        assert previousButton != null;
        assert seekBar != null;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.GONE);
                musicPlayService.musicPlay();
                isPlaying = true;
                new SeekBarThread().start();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                musicPlayService.musicPause();
                isPlaying = false;
            }
        });
        playButton.setVisibility(View.GONE);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUp(musicPlayService.prevPosition());
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUp(musicPlayService.nextPosition());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicPlayIntent = new Intent(this, MusicPlayService.class);
        musicPlayIntent.putExtra("fileList", getIntent().getStringArrayListExtra("fileList"));
        musicPlayIntent.putExtra("position", getIntent().getIntExtra("position", -1));
        startService(musicPlayIntent);
        if (musicConnection == null)
            Log.d("debugging", "musicConnection null");
        else
            Log.d("debugging", "musicConnection not null");
        bindService(musicPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
        isPlaying = true;
    }

    @Override
    protected void onStop() {
        musicPlayService.independent();
        if(!isPlaying) {
            stopService(musicPlayIntent);
        }
        unbindService(musicConnection);
        isPlaying = false;
        super.onStop();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            musicPlayService = ((MusicPlayService.MusicPlayBinder) service).getService();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d("debugging","onStopTrackingTouch");
                    musicPlayService.seekTo(seekBar.getProgress());
                    if (playedBeforeSeek) {
                        musicPlayService.musicPlay();
                        isPlaying = true;
                        new SeekBarThread().start();
                    }
                }
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("debugging","onStartTrackingTouch");
                    musicPlayService.musicPause();
                    playedBeforeSeek = isPlaying;
                    isPlaying = false;
                }
                public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                }
            });
            musicPlayService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(!musicPlayService.skipping){
                        setUp(musicPlayService.nextPosition());
                    }
                }
            });
            loaded = true;
            new SeekBarThread().start();
            setUp(musicPlayService.getPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            loaded = false;
        }
    };

    class SeekBarThread extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                seekBar.setProgress(musicPlayService.getCurrentPosition());
                if(musicPlayService.isChanged()){
                    seekBar.setMax(musicPlayService.getDuration());
                    seekBar.setProgress(0);
                    musicPlayService.notifiedChange();
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setUp(int pos){
        mViewPager.setCurrentItem(pos);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> fileList;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public Fragment getItem(int position) {
            return new MusicInfoFragment(fileList.get(position));
        }

        @Override
        public int getCount() {
            return fileList.size();
        }
    }

    public class MusicInfoFragment extends Fragment {

        private String filePath;
        ImageView albumArt;
        TextView title;
        TextView artist;

        public MusicInfoFragment(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_music_play, container, false);
            albumArt = (ImageView) rootView.findViewById(R.id.music_image);
            title = (TextView) rootView.findViewById(R.id.music_title);
            artist = (TextView) rootView.findViewById(R.id.music_artist);

            title.setSelected(true);

            Uri fileUri = Uri.parse(filePath);
            String filePath = fileUri.getPath();
            Cursor c = container.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,"_data ='" + filePath + "'",null,null);
            c.moveToNext();
            if(c.getCount()>0) {
                int id = c.getInt(0);
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
                Cursor cur = managedQuery(uri, null, null, null, null);
                if(cur.moveToFirst()) {
                    title.setText(cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                    artist.setText(cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)));
                    int albumID = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                    albumArt.setImageBitmap(getArtworkQuick(container.getContext(),albumID, 50, 50));
                    albumArt.setImageBitmap(getAlbumArt(container.getContext(),albumID));
                }
            }
            c.close();
            return rootView;
        }


        public Bitmap getAlbumArt(Context mContext, long albumId)
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
            return artwork;
        }

//        private Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
//            // NOTE: There is in fact a 1 pixel frame in the ImageView used to
//            // display this drawable. Take it into account now, so we don't have to
//            // scale later.
//            w -= 2;
//            h -= 2;
//            Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
//            BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
//            ContentResolver res = context.getContentResolver();
//            Uri uri = ContentUris.withAppendedId(artworkUri, album_id);
//            if (uri != null) {
//                Log.d("debugging", "uri exist");
//                ParcelFileDescriptor fd = null;
//                try {
//                    fd = res.openFileDescriptor(uri, "r");
//                    int sampleSize = 1;
//
//                    // Compute the closest power-of-two scale factor
//                    // and pass that to sBitmapOptionsCache.inSampleSize, which will
//                    // result in faster decoding and better quality
//                    sBitmapOptionsCache.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFileDescriptor(
//                            fd.getFileDescriptor(), null, sBitmapOptionsCache);
//                    int nextWidth = sBitmapOptionsCache.outWidth >> 1;
//                    int nextHeight = sBitmapOptionsCache.outHeight >> 1;
//                    while (nextWidth>w && nextHeight>h) {
//                        sampleSize <<= 1;
//                        nextWidth >>= 1;
//                        nextHeight >>= 1;
//                    }
//
//                    sBitmapOptionsCache.inSampleSize = sampleSize;
//                    sBitmapOptionsCache.inJustDecodeBounds = false;
//                    Bitmap b = BitmapFactory.decodeFileDescriptor(
//                            fd.getFileDescriptor(), null, sBitmapOptionsCache);
//
//                    if (b != null) {
//                        // finally rescale to exactly the size we need
//                        if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
//                            Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
//                            b.recycle();
//                            b = tmp;
//                        }
//                    }
//
//                    return b;
//                } catch (FileNotFoundException e) {
//                } finally {
//                    try {
//                        if (fd != null)
//                            fd.close();
//                    } catch (IOException e) {
//                    }
//                }
//            }
//            return null;
//        }
    }
}
