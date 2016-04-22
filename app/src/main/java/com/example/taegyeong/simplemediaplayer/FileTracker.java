package com.example.taegyeong.simplemediaplayer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by taegyeong on 16. 4. 9..
 */
class FileInfo{

    private File file;
    private FileInfo parent;

    private int[] subFileNum;
    private ArrayList<FileInfo> subFileInfo;
    private ArrayList<String> imagePathList;
    private ArrayList<String> musicPathList;
    private ArrayList<String> videoPathList;

    public FileInfo(FileInfo parent, File file){
        this.file = file;
        this.parent = parent;
        subFileNum = new int[3];
        subFileInfo = new ArrayList<>();
        imagePathList = new ArrayList<>();
        musicPathList = new ArrayList<>();
        videoPathList = new ArrayList<>();
    }
    public String getName() {return file.getName();}
    public String getPath() {return file.getPath();}
    public FileInfo getParent() {return parent;}
    public boolean isDirectory() {return file.isDirectory();}
    public int getSubFileNum(int fileType) {
        if (fileType == SMPCustom.TYPE_ALL)
            return subFileNum[0] + subFileNum[1] + subFileNum[2];
        else
            return subFileNum[fileType];
    }
    public ArrayList<FileInfo> getSubFileInfo(int fileType) {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        for (FileInfo fileInfo : subFileInfo){
            if (fileInfo.subFileNum[fileType] > 0)
                fileInfos.add(fileInfo);
        }
        return fileInfos;
    }
    public String getSubFileName(int position, int fileType) {
        return new File(getSubFilePathList(fileType).get(position)).getName();
    }
    public ArrayList<String> getSubFilePathList(int fileType) {
        if(fileType == SMPCustom.TYPE_IMAGE)
            return imagePathList;
        else if(fileType == SMPCustom.TYPE_MUSIC)
            return musicPathList;
        else if(fileType == SMPCustom.TYPE_VIDEO)
            return videoPathList;
        return null;
    }

    public void scan(){
        if(isDirectory()){
            if(file == null)
                return;
            if(!file.canRead())
                return;

            subFileNum[0] = 0;
            subFileNum[1] = 0;
            subFileNum[2] = 0;

            File[] subDirs = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            File[] subFiles = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    int type = SMPCustom.checkType(file.getName(), SMPCustom.TYPE_ALL);
                    if(type < 0)
                        return false;
                    subFileNum[type]++;
                    getSubFilePathList(type).add(file.getPath());
                    return true;
                }
            });
            if(subDirs != null){
                for (File subDir : subDirs) {
                    FileInfo newDir = new FileInfo(this,subDir);
                    newDir.scan();
                    if (newDir.getSubFileNum(SMPCustom.TYPE_ALL) > 0) {
                        subFileInfo.add(newDir);
                        subFileNum[0] += newDir.subFileNum[0];
                        subFileNum[1] += newDir.subFileNum[1];
                        subFileNum[2] += newDir.subFileNum[2];
                    }
                }
            }
        }
    }
}
public class FileTracker{
    private FileInfo rootDir;
    private FileInfo currentDir;
    private int fileType;

    public FileTracker(){
        rootDir = new FileInfo(null,new File(SMPCustom.root));
        rootDir.scan();
        currentDir = rootDir;
    }

    public void setFileType(int fileType) {this.fileType = fileType;}

    public String getDirName() {return currentDir.getPath();}
    public int getCurrentFileNum() {
        return currentDir.getSubFileInfo(fileType).size() + currentDir.getSubFilePathList(fileType).size();
    }
    public int getSubFileNum(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if(position < subFileInfo.size())
            return subFileInfo.get(position).getSubFileNum(fileType);
        return -1;
    }
    public int getSubFileType(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if (position < subFileInfo.size())
            return SMPCustom.TYPE_DIR;
        return fileType;
    }
    public String getFileName(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if (position < subFileInfo.size())
            return subFileInfo.get(position).getName();
        return currentDir.getSubFileName(position - subFileInfo.size(), fileType);
    }
    public int openFile(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if (position < subFileInfo.size()) {
            currentDir = subFileInfo.get(position);
            return -1;
        }
        return position - subFileInfo.size();
    }
    public ArrayList<String> getFilePathList() {
        return currentDir.getSubFilePathList(fileType);
    }
    public boolean returnBack() {
        if(currentDir.getParent() == null)
            return false;
        currentDir = currentDir.getParent();
        return true;
    }
    public void gotoRoot() {
        currentDir = rootDir;
    }
}
