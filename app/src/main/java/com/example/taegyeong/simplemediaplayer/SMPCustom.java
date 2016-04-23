package com.example.taegyeong.simplemediaplayer;

import android.graphics.Typeface;

/**
 * Created by taegyeong on 16. 4. 21..
 */
public class SMPCustom {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_ALL = 3;
    public static final int TYPE_DIR = 4;

    // "/storage/external_SD/";
    // "/storage/emulated/0/";
    public static final String root = "/storage/emulated/0/";

    private static final String[] imageFormat = {".jpg"};
    private static final String[] musicFormat = {".mp3"};
    private static final String[] videoFormat = {".mp4"};

    public static boolean isRightType(String fileName, int fileType){
        if ((fileType == TYPE_ALL)||(fileType == TYPE_IMAGE)) {
            for (String format : imageFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        if ((fileType == TYPE_ALL)||(fileType == TYPE_MUSIC)) {
            for (String format : musicFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        if ((fileType == TYPE_ALL)||(fileType == TYPE_VIDEO)) {
            for (String format : videoFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        return false;
    }

    public static int checkType(String fileName, int fileType){
        if ((fileType == TYPE_ALL)||(fileType == TYPE_IMAGE)) {
            for (String format : imageFormat){
                if (fileName.endsWith(format))
                    return TYPE_IMAGE;
            }
        }
        if ((fileType == TYPE_ALL)||(fileType == TYPE_MUSIC)) {
            for (String format : musicFormat){
                if (fileName.endsWith(format))
                    return TYPE_MUSIC;
            }
        }
        if ((fileType == TYPE_ALL)||(fileType == TYPE_VIDEO)) {
            for (String format : videoFormat){
                if (fileName.endsWith(format))
                    return TYPE_VIDEO;
            }
        }
        return -1;
    }

    public static Typeface branBlack;
    public static Typeface branBold;
    public static Typeface branRegular;
    public static Typeface branLight;
}
