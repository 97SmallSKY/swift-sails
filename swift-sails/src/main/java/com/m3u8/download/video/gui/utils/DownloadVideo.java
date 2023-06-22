package com.m3u8.download.video.gui.utils;

import com.m3u8.download.video.m3u8.download.M3u8DownloadFactory;
import com.m3u8.download.video.m3u8.listener.DownloadListener;
import com.m3u8.download.video.m3u8.utils.Constant;
import com.m3u8.download.video.m3u8.utils.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Small_Tsk
 * @create 2023-06-11
 **/
public class DownloadVideo {

    // 下载核心数
    private int core;

    // 文件名
    private String name;

    // 文件保存位置
    private String position;

    // 下载链接
    private String url;

    private M3u8DownloadFactory.M3u8Download m3u8Download;

    public DownloadVideo(int core, String name, String position, String url) {
        this.core = core;
        this.name = name;
        this.position = position;
        this.url = url;

    }

    public DownloadVideo() {

    }


    public M3u8DownloadFactory.M3u8Download initialization() {
        m3u8Download = new M3u8DownloadFactory().getInstance(url);

        if (StringUtils.isBlank(name)) {
            name = LocalTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss"));
        }
        name = checkFileName(name);

        //设置生成目录
        m3u8Download.setDir(position);
        //设置视频名称
        m3u8Download.setFileName(name);
        //设置线程数
        m3u8Download.setThreadCount(core);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(10000L);
        /*
        设置日志级别
        可选值：NONE INFO DEBUG ERROR
        */
        m3u8Download.setLogLevel(Constant.INFO);
        //设置监听器间隔（单位：毫秒）
        m3u8Download.setInterval(500L);

        m3u8Download.start();

        return m3u8Download;

    }

    private static String checkFileName(String name) {
        return name.replaceAll("\\\\", "")
                .replaceAll("/", "")
                .replaceAll(":", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\\|", "")
                .replaceAll("。", "");


    }

    @Override
    public String toString() {
        return "downloadVideo{" +
                "core=" + core +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", url='" + url + '\'' +
                ", m3u8Download=" + m3u8Download +
                '}';
    }

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public M3u8DownloadFactory.M3u8Download getM3u8Download() {
        return m3u8Download;
    }

    public void setM3u8Download(M3u8DownloadFactory.M3u8Download m3u8Download) {
        this.m3u8Download = m3u8Download;
    }
}

