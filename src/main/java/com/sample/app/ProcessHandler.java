package com.sample.app;

import com.sample.app.util.Constants;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ProcessHandler {

    public void downloadExecutor(WebsiteDownloader downloader, Set<String> paths) {
        ExecutorService executor = Executors.newFixedThreadPool(Constants.MAX_THREADS_FOR_DOWNLOADS);
        try {

            List<CompletableFuture<Set<String>>> completableFutureTasks = paths
                    .stream()
                    .map(urlPath -> CompletableFuture.supplyAsync(() ->
                            downloader.startDownload(urlPath), executor)
                    )
                    .collect(Collectors.toList());
            executor.shutdown();
            completableFutureTasks.forEach((cmp) -> cmp.thenAccept(set -> downloadExecutor(downloader, set)));
            CompletableFuture.allOf(completableFutureTasks.toArray(new CompletableFuture[completableFutureTasks.size()])).join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

    public String getWebsiteURL(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter website URL. Press Enter to use default URL [https://tretton37.com/] : ");
        String websiteURL = userInput.nextLine();

        return websiteURL.trim().isEmpty() ? Constants.DEFAULT_URL : websiteURL.trim();
    }

    public String getWebsiteDownloadLocation(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter location to download website. Default location will be current working directory : ");
        String directoryToDownload = userInput.nextLine();

        String default_Directory = File.separator + Constants.DEFAULT_DOWNLOAD_DIR;
        String currentDirectory = System.getProperty("user.dir");
        return directoryToDownload.trim().isEmpty()? currentDirectory
                .substring(0, currentDirectory.lastIndexOf("/")) + default_Directory
                : directoryToDownload.trim() + default_Directory;
    }
}
