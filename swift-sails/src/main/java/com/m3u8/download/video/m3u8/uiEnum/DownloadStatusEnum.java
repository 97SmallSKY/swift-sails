package com.m3u8.download.video.m3u8.uiEnum;

/**
 * @author Small_Tsk
 * @create 2023-06-16
 **/
public enum DownloadStatusEnum {
    NOT_STARTED("未开始"),
    DOWNLOADING("下载中"),
    PAUSED("已暂停"),
    COMPLETED("已完成"),
    CONNECTING("正在连接资源"),

    CONNECTING_FAIL("连接失败");

    private final String chineseName;

    DownloadStatusEnum(String chineseName) {
        this.chineseName = chineseName;
    }

    public String get() {
        return chineseName;
    }
}

