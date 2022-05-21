package com.sample.app;

import com.sample.app.util.Constants;
import com.sample.app.util.UtilityFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class WebsiteDownloader {

    private String hostName;
    private String saveDir;
    private static Set<String> globalUrlSet = new HashSet<>();
    private UtilityFile fileUtil;

    public WebsiteDownloader(String hostName, String saveDir, UtilityFile fileUtil) {
        this.hostName = hostName;
        this.saveDir = saveDir;
        this.fileUtil = fileUtil;
    }

    public Set<String> startDownload(String urlString) {
        try {
            URL url = new URL(urlString);
            return downloadResource(url);
        } catch (IOException ie) {
            System.out.println("IO Exception occur");
        }
        return new HashSet<>();
    }
    /* This method will download the resource and return all the links within the resource*/
    private Set<String> downloadResource(URL url) {
        Set<String> set = new HashSet<>();

        File file = fileUtil.getFileName(url);

        try {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                if (httpConn.getContentType().contains(Constants.HTML_TEXT_CONTENT_TYPE)) {
                    set = extractAllLinksFromPage(url, file, httpConn);
                } else {
                    fileContentDownloader(file, httpConn);
                }

            }
            httpConn.disconnect();
        }
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        } catch (IOException ie) {
            System.out.println("IOException raised");
        }

        return set;
    }

    private void fileContentDownloader(File file, HttpURLConnection httpConn) {
        try {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
            System.out.println("File name : " + file.getAbsoluteFile());
            long completeFileSize = httpConn.getContentLength();

            // read each line from stream till end
            long downloadedFileSize = 0;

            int bytesRead = 0;
            byte[] buffer = new byte[Constants.BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                downloadedFileSize += bytesRead;
                outputStream.write(buffer, 0, bytesRead);
                // calculate progress
                final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);
                System.out.print("Downloading: " + file.getName()  + " :" + currentProgress + "% " + "\r");
            }
            outputStream.close();
            inputStream.close();
            System.out.print("Downloading Completed" + "\r");
        } catch (IOException e) {
            System.out.println("Exception in file : " +  file.getName() + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private Set<String> extractAllLinksFromPage(URL url, File file, HttpURLConnection httpConn) throws IOException {
        Document document = Jsoup.connect(url.toString()).get();
        Set<String> extractedUrlSet = this.parseFileToExtractTags(document);
        fileContentDownloader(file, httpConn);
        return extractedUrlSet;
    }

    private Set<String> parseFileToExtractTags(Document document) {
        Set<String> urlSet = new HashSet<>();
        Elements scriptTags = document.select("script");
        Elements imageTags = document.select("img");
        Elements anchorTags = document.select("a");
        Elements linkTags = document.select("link");

        this.extractHtmlTagLinks(urlSet,scriptTags,"src");
        this.extractHtmlTagLinks(urlSet,imageTags,"src");
        this.extractHtmlTagLinks(urlSet,anchorTags,"href");
        this.extractHtmlTagLinks(urlSet,linkTags,"href");

        return urlSet;
    }

    private boolean isAbsoluteUrl(String url) {
        Pattern pattern = Pattern.compile("^(?:[a-z]+:)?//", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(url).find();
    }

    private String getAbsoluteUrl(String url) {
        return this.hostName + url.replaceFirst("^/", "");
    }

    private void extractHtmlTagLinks(Set<String> urlSet, Elements elements, String elementLinkAttribute) {
        for (Element link : elements) {
            String this_url = link.attr(elementLinkAttribute);

            if (!this_url.isEmpty() && this_url.indexOf("mailto") == -1 && this_url.indexOf("tel:") == -1
                    && !this_url.startsWith("#") && !this_url.contains("javascript:")) {
                if (isAbsoluteUrl(this_url)) {
                    if (this_url.indexOf(this.hostName) != -1) {
                        if (!this.globalUrlSet.contains(this_url)) {
                            urlSet.add(this_url);
                            this.globalUrlSet.add(this_url);
                        }
                        String modifiedUrl = this.fileUtil.getCompleteFileSavePath(this_url);
                        link.attr(elementLinkAttribute, modifiedUrl);
                    }
                } else {
                    if (!this.globalUrlSet.contains(this.getAbsoluteUrl(this_url))) {
                        urlSet.add(this.getAbsoluteUrl(this_url));
                        this.globalUrlSet.add(this.getAbsoluteUrl(this_url));
                    }
                    String modifiedUrl = this.fileUtil.getCompleteFileSavePath(this_url);
                    link.attr(elementLinkAttribute, modifiedUrl);
                }
            }
        }
    }
}
