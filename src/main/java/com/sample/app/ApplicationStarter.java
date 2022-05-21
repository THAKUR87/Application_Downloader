package com.sample.app;

import com.sample.app.util.UtilityFile;

import java.util.Set;

public class ApplicationStarter {

    public static void main(String[] args) {

        ProcessHandler processHandler = new ProcessHandler();
        String websiteLocation = processHandler.getWebsiteDownloadLocation();
        UtilityFile fileUtil = new UtilityFile(websiteLocation);
        String website_URL = processHandler.getWebsiteURL();
        WebsiteDownloader downloader = new WebsiteDownloader(website_URL, websiteLocation, fileUtil);
        Set<String> paths = downloader.startDownload(website_URL);

        processHandler.downloadExecutor(downloader, paths);
    }

}
