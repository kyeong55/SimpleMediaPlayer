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

    private int subFileNum;
    private List<FileInfo> subFileInfo;

    public FileInfo(FileInfo parent, File file){
        this.file = file;
        this.parent = parent;
    }
    public String getName() {return file.getName();}
    public String getPath() {return file.getPath();}
    public FileInfo getParent() {return parent;}
    public boolean isDirectory() {return file.isDirectory();}
    public int getSubFileNum() {return subFileNum;}
    public List<FileInfo> getSubFileInfo() {return subFileInfo;}

    public void scan(final List<String> filter){
        if(isDirectory()){
            if(file == null)
                return;
            if(!file.canRead())
                return;

            subFileNum = 0;
            subFileInfo = new ArrayList<>();

            File[] subDir = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            File[] subFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    for(int i = 0; i < filter.size(); i++){
                        if(filename.endsWith("."+filter.get(i)))
                            return true;
                    }
                    return false;
                }
            });
            if(subDir != null){
                for (File dir : subDir) {
                    FileInfo newDir = new FileInfo(this,dir);
                    newDir.scan(filter);
                    if (newDir.subFileNum > 0) {
                        subFileInfo.add(newDir);
                        subFileNum += newDir.subFileNum;
                    }
                }
            }
            if(subFiles != null){
                for (File subFile : subFiles){
                    subFileInfo.add(new FileInfo(this,subFile));
                    subFileNum++;
                }
            }
        }
    }
}
public class FileTracker {
    private final String root = "/storage/external_SD/";
    private List<String> filter;
    private FileInfo rootDir;
    private FileInfo currentDir;

    public FileTracker(){
        rootDir = new FileInfo(null,new File(root));
        init();
    }
    public void init(){
        filter = new ArrayList<>();
//        filter.add("jpg");
//        filter.add("png");
        filter.add("mp3");
//        filter.add("wmv");
//        filter.add("avi");
        rootDir.scan(filter);
        currentDir = rootDir;
    }
    public String getDirName() {return currentDir.getPath();}
    public int getFileNum() {return currentDir.getSubFileInfo().size();}
    public String getFileName(int position) {return currentDir.getSubFileInfo().get(position).getName();}
    public String getFilePath(int position) {return currentDir.getSubFileInfo().get(position).getPath();}
    public ArrayList<String> getFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        List<FileInfo> subFileInfo = currentDir.getSubFileInfo();
        for (int i=0;i<currentDir.getSubFileInfo().size();i++){
            fileList.add(currentDir.getSubFileInfo().get(i).getPath());
        }
        return fileList;
    }
    public boolean openDir(int position) {
        if(currentDir.getSubFileInfo().get(position).isDirectory()) {
            if (position < currentDir.getSubFileInfo().size()) {
                currentDir = currentDir.getSubFileInfo().get(position);
                return true;
            }
        }
        return false;
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
