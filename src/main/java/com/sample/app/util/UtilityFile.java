package com.sample.app.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;

public class UtilityFile {

    private String urlString;

    private String saveDir;

    public UtilityFile(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getCompleteFileSavePath(String urlString) {
        this.urlString = urlString;
        return this.saveDir + File.separator + this.getDirectoryPath() + File.separator + this.getFileName();
    }

    public String getDirectoryPath() {
        try {
            String dirPath = !this.getFileExtension().isEmpty() ? this.urlString.substring(0, this.urlString.lastIndexOf("/")) : this.urlString;
            return dirPath.startsWith("/") ? dirPath.replaceFirst("/","") : dirPath;
        }catch (Exception e){
            return "/";
        }
    }

    private String getFileName() {
        return !this.getFileExtension().isEmpty() ? FilenameUtils.getName(this.urlString) : "index.html";
    }

    private String getFileName(URL url, String extension) {
        return !extension.isEmpty() ? FilenameUtils.getName(url.getPath()) : "index.html";
    }


    private String getFileExtension() {
        return FilenameUtils.getExtension(this.urlString.replace(":", ""));
    }

    public File getFileName(URL url) {
        String extension = FilenameUtils.getExtension(url.getPath().replace(":",""));

        String directoryPath = getDirectoryPath(url, extension);
        String fileName = getFileName(url, extension);

        createDirectory(directoryPath);

        File file = getNewFileToSave(directoryPath, fileName);

        return file;
    }

    private void createDirectory(String directoryPath) {
        File directory = new File(this.saveDir + File.separator + directoryPath + File.separator);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private File getNewFileToSave(String directoryPath, String fileName) {
        return new File(this.saveDir + File.separator + directoryPath + File.separator + fileName);
    }


    private String getDirectoryPath(URL url, String extension) {
        return !extension.isEmpty() ? url.getPath().substring(0, url.getPath().lastIndexOf("/")) : url.getPath();
    }
}
