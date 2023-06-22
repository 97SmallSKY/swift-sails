package com.m3u8.download.video.m3u8.listener;

import com.m3u8.bean.po.DownloadTask;

public interface DownloadListener {

    void start(DownloadTask downloadTask);

    void percent(double percent);

    void downloadedFile(String downloadedFile);

    void fileSize(String fileSize);

    void process(String downloadUrl, int finished, int sum, double percent);

    void speed(String speedPerSecond);

    void timeConsuming(String timeConsuming);

    void expectedCompletionTime(String completionTime);

    void mergeAndDelete();
    void end();

    void connectionFailed();

    void pause();

}
