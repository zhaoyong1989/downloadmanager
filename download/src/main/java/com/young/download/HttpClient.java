package com.young.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    private InputStream input;
    private long contentLength;

    public InputStream getInput() {
        return input;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void request(String url) throws IOException, DownloadException{
        HttpURLConnection urlConnection = getUrlConnection(url);
        input = urlConnection.getInputStream();
        contentLength = urlConnection.getContentLength();
    }

    private HttpURLConnection getUrlConnection(String fileUrl) throws IOException, DownloadException{
        int redirectCount = 0;
        HttpURLConnection connection;
        String urlTemp = fileUrl;
        URL url;

        while(redirectCount < DownloadConstants.REDIRECT_COUNT_MAX) {
            url = new URL(urlTemp);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DownloadConstants.CONNECT_TIME_OUT);
            connection.setReadTimeout(DownloadConstants.READ_TIME_OUT);
            //断点续传
            //connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");

            int requestCode = connection.getResponseCode();
            if(requestCode >= 200 && requestCode < 300) {
                return connection;
            } else if(requestCode >= 300 && requestCode < 400) {
                urlTemp = connection.getHeaderField("Location");
                redirectCount++;
            } else if(requestCode >= 400) {
                throw new DownloadException(DownloadStatus.HTTP_RESPONSE_ERROR);
            }
        }

        throw new DownloadException(DownloadStatus.BEYOND_REDIRECT_COUNT_ERROR);
    }
}
