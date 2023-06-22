package com.m3u8.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("download_segment")
public class DownloadSegment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long downloadTaskId;
    private Integer segmentNumber;
    private String segmentName;
    private String segmentPath;

    private String status;

    private String size;

    private String saveDirectory;
    private String isValid;


    @Override
    public String toString() {
        return "DownloadSegment{" +
                "id=" + id +
                ", downloadTaskId=" + downloadTaskId +
                ", segmentNumber=" + segmentNumber +
                ", segmentName='" + segmentName + '\'' +
                ", segmentPath='" + segmentPath + '\'' +
                ", status='" + status + '\'' +
                ", size='" + size + '\'' +
                ", saveDirectory='" + saveDirectory + '\'' +
                ", isValid='" + isValid + '\'' +
                '}';
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDownloadTaskId() {
        return downloadTaskId;
    }

    public void setDownloadTaskId(Long downloadTaskId) {
        this.downloadTaskId = downloadTaskId;
    }

    public Integer getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(Integer segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getSegmentPath() {
        return segmentPath;
    }

    public void setSegmentPath(String segmentPath) {
        this.segmentPath = segmentPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
