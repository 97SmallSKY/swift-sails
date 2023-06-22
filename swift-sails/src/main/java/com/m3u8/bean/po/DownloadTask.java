package com.m3u8.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.annotations.Options;
import org.springframework.data.annotation.Id;


import java.math.BigDecimal;
import java.util.Date;

@TableName("download_task")
public class DownloadTask {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String downloadUrl;
    private String name;

    private String progress;

    private String timeConsuming;
    private String size;
    private String saveDirectory;
    private String tempDirectory;
    private Integer threadCount;
    private String m3u8Key;

    private String keyBytes;

    private BigDecimal downloadBytes;
    private BigDecimal totalFileSize;
    private boolean isBytes;
    private String status;
    private String suffix;
    private Date createTime;
    private String isValid;



    public DownloadTask() {

    }


    public DownloadTask(Long id, String downloadUrl, String name,
                        String progress, String timeConsuming, String size,
                        String saveDirectory, String tempDirectory, Integer threadCount,
                        String m3u8Key, String keyBytes, BigDecimal downloadBytes,
                        BigDecimal totalFileSize, boolean isBytes, String status, String suffix,
                        Date createTime, String isValid) {
        this.id = id;
        this.downloadUrl = downloadUrl;
        this.name = name;
        this.progress = progress;
        this.timeConsuming = timeConsuming;
        this.size = size;
        this.saveDirectory = saveDirectory;
        this.tempDirectory = tempDirectory;
        this.threadCount = threadCount;
        this.m3u8Key = m3u8Key;
        this.keyBytes = keyBytes;
        this.downloadBytes = downloadBytes;
        this.totalFileSize = totalFileSize;
        this.isBytes = isBytes;
        this.status = status;
        this.suffix = suffix;
        this.createTime = createTime;
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "DownloadTask{" +
                "id=" + id +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", name='" + name + '\'' +
                ", progress='" + progress + '\'' +
                ", timeConsuming='" + timeConsuming + '\'' +
                ", size='" + size + '\'' +
                ", saveDirectory='" + saveDirectory + '\'' +
                ", tempDirectory='" + tempDirectory + '\'' +
                ", threadCount=" + threadCount +
                ", m3u8Key='" + m3u8Key + '\'' +
                ", keyBytes='" + keyBytes + '\'' +
                ", downloadBytes=" + downloadBytes +
                ", totalFileSize=" + totalFileSize +
                ", isByte=" + isBytes +
                ", status='" + status + '\'' +
                ", suffix='" + suffix + '\'' +
                ", createTime=" + createTime +
                ", isValid='" + isValid + '\'' +
                '}';
    }

    public boolean isByte() {
        return isBytes;
    }

    public void setByte(boolean aByte) {
        isBytes = aByte;
    }

    public String getM3u8Key() {
        return m3u8Key;
    }

    public void setM3u8Key(String m3u8Key) {
        this.m3u8Key = m3u8Key;
    }

    public String getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(String keyBytes) {
        this.keyBytes = keyBytes;
    }

    public BigDecimal getDownloadBytes() {
        return downloadBytes;
    }

    public void setDownloadBytes(BigDecimal downloadBytes) {
        this.downloadBytes = downloadBytes;
    }

    public BigDecimal getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(BigDecimal totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(String timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
