package com.example.taegyeong.simplemediaplayer;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImagePlayActivity extends AppCompatActivity {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_play);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        title = (TextView) findViewById(R.id.image_title);

        assert title != null;

        title.setTypeface(SMPCustom.branBold);

        final ArrayList<String> fileList = getIntent().getStringArrayListExtra("fileList");
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fileList);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.image_pager);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra("position", -1));
        title.setText(new File(fileList.get(getIntent().getIntExtra("position", -1))).getName());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                title.setText(new File(fileList.get(position)).getName());
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> fileList;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public Fragment getItem(int position) {
            return new ImageViewFragment(fileList.get(position));
        }

        @Override
        public int getCount() {
            return fileList.size();
        }
    }

    public class ImageViewFragment extends Fragment {

        private String filePath;
        ImageView imageView;
        ProgressBar progressBar;

        public ImageViewFragment(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_play, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.image_view);
            progressBar = (ProgressBar) rootView.findViewById(R.id.image_progressbar);

            ImageLoadTask task = new ImageLoadTask();
            task.execute();

            return rootView;
        }

        public Bitmap setImageView() throws IOException {
            Bitmap stampPhoto, rotatedStampPhoto, scaledRotatedStampPhoto;
            stampPhoto = BitmapFactory.decodeFile(filePath);
//            ExifInterface exif = new ExifInterface(filePath);
//            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
//            int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

//            int rotationAngle = 0;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotationAngle);
//            rotatedStampPhoto = Bitmap.createBitmap(stampPhoto, 0,
//                    0, stampPhoto.getWidth(), stampPhoto.getHeight(),
//                    matrix, true);
            rotatedStampPhoto = stampPhoto;

            if (rotatedStampPhoto.getWidth() > rotatedStampPhoto.getHeight()) {
                if (rotatedStampPhoto.getHeight() > 500) {
                    scaledRotatedStampPhoto = Bitmap.createScaledBitmap(rotatedStampPhoto,
                            500 * rotatedStampPhoto.getWidth() / rotatedStampPhoto.getHeight(), 500, false);
                } else {
                    scaledRotatedStampPhoto = rotatedStampPhoto;
                }
            } else {
                if (rotatedStampPhoto.getWidth() > 500) {
                    scaledRotatedStampPhoto = Bitmap.createScaledBitmap(rotatedStampPhoto, 500, 500
                            * rotatedStampPhoto.getHeight() / rotatedStampPhoto.getWidth(), false);
                } else {
                    scaledRotatedStampPhoto = rotatedStampPhoto;
                }
            }

            return scaledRotatedStampPhoto;
        }

        public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
            @Override
            public Bitmap doInBackground(Void... params) {
                Bitmap result = null;
                try {
                    result = setImageView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(result);
            }
        }
    }
}
