package com.example.taegyeong.simplemediaplayer;

/**
 * Created by taegyeong on 16. 4. 21..
 */
public class FileType {
    public static final int IMAGE = 0;
    public static final int MUSIC = 1;
    public static final int VIDEO = 2;
    public static final int ALL = 3;
    public static final int DIR = 4;

    private static final String[] imageFormat = {".jpg"};
    private static final String[] musicFormat = {".mp3"};
    private static final String[] videoFormat = {".wmv"};

    public static boolean isRightType(String fileName, int fileType){
        if ((fileType == ALL)||(fileType == IMAGE)) {
            for (String format : imageFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        if ((fileType == ALL)||(fileType == MUSIC)) {
            for (String format : musicFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        if ((fileType == ALL)||(fileType == VIDEO)) {
            for (String format : videoFormat){
                if (fileName.endsWith(format))
                    return true;
            }
        }
        return false;
    }

    public static int checkType(String fileName, int fileType){
        if ((fileType == ALL)||(fileType == IMAGE)) {
            for (String format : imageFormat){
                if (fileName.endsWith(format))
                    return IMAGE;
            }
        }
        if ((fileType == ALL)||(fileType == MUSIC)) {
            for (String format : musicFormat){
                if (fileName.endsWith(format))
                    return MUSIC;
            }
        }
        if ((fileType == ALL)||(fileType == VIDEO)) {
            for (String format : videoFormat){
                if (fileName.endsWith(format))
                    return VIDEO;
            }
        }
        return -1;
    }
}
