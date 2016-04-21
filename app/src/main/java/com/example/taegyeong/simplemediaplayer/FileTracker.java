package com.example.taegyeong.simplemediaplayer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

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
        if (fileType == FileType.ALL)
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
        if(fileType == FileType.IMAGE)
            return imagePathList;
        else if(fileType == FileType.MUSIC)
            return musicPathList;
        else if(fileType == FileType.VIDEO)
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
                    int type = FileType.checkType(file.getName(), FileType.ALL);
                    if(type < 0)
                        return false;
                    subFileNum[type]++;
                    getSubFilePathList(type).add(file.getPath());
                    return true;
                }
            });
//            File[] subFiles = file.listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String filename) {
////                    for(int i = 0; i < filter.size(); i++){
////                        if(filename.endsWith("."+filter.get(i)))
////                            return true;
////                    }
////                    return false;
//                    int type = FileType.checkType(filename, FileType.ALL);
//                    if(type < 0)
//                        return false;
//                    subFileNum[type]++;
//                    getSubFilePathList(type).add(filename);
//                    return true;
//                }
//            });
            if(subDirs != null){
                for (File subDir : subDirs) {
                    FileInfo newDir = new FileInfo(this,subDir);
                    newDir.scan();
                    if (newDir.getSubFileNum(FileType.ALL) > 0) {
                        subFileInfo.add(newDir);
                        subFileNum[0] += newDir.subFileNum[0];
                        subFileNum[1] += newDir.subFileNum[1];
                        subFileNum[2] += newDir.subFileNum[2];
                    }
                }
            }
//            if(subFiles != null){
//                for (File subFile : subFiles){
////                    subFileInfo.add(new FileInfo(this,subFile));
////                    subFileNum++;
//                    subFileNameList.add(subFile.getName());
//                    subFilePathList.add(subFile.getPath());
//                }
//            }
        }
    }
}
public class FileTracker {
    private final String root = "/storage/external_SD/";
    private FileInfo rootDir;
    private FileInfo currentDir;
    private int fileType;

    public FileTracker(){
        rootDir = new FileInfo(null,new File(root));
        rootDir.scan();
        fileType = FileType.MUSIC;
        currentDir = rootDir;
    }
    public String getDirName() {return currentDir.getPath();}
    public int getCurrentFileNum() {
        return currentDir.getSubFileInfo(fileType).size() + currentDir.getSubFilePathList(fileType).size();
    }
    public int getSubFileType(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if (position < subFileInfo.size())
            return FileType.DIR;
        return fileType;
    }
    public String getFileName(int position) {
        ArrayList<FileInfo> subFileInfo = currentDir.getSubFileInfo(fileType);
        if (position < subFileInfo.size())
            return subFileInfo.get(position).getName();
        return currentDir.getSubFileName(position - subFileInfo.size(), fileType);
    }
//    public String getFilePath(int position) {return currentDir.getSubFileInfo().get(position).getPath();}
//    public ArrayList<String> getFileList() {
//        ArrayList<String> fileList = new ArrayList<>();
//        List<FileInfo> subFileInfo = currentDir.getSubFileInfo();
//        for (int i=0;i<currentDir.getSubFileInfo().size();i++){
//            fileList.add(currentDir.getSubFileInfo().get(i).getPath());
//        }
//        return fileList;
//    }
//    public boolean openDir(int position) {
//        if(currentDir.getSubFileInfo().get(position).isDirectory()) {
//            if (position < currentDir.getSubFileInfo().size()) {
//                currentDir = currentDir.getSubFileInfo().get(position);
//                return true;
//            }
//        }
//        return false;
//    }
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
