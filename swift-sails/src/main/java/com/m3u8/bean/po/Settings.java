package com.m3u8.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


@TableName("settings")
public class Settings {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private Integer downloadCores;
    private String defaultSaveFolder;
    private String defaultTempFolder;
    private Boolean deleteAfterDownload;
    private Boolean autoExitAfterDownload;
    private Boolean scheduleDownload;
    private Boolean notifyAfterDownload;
    private String background;
    private String hotkeys;


    private Date createTime;


    @Override
    public String toString() {
        return "SettingsPOJO{" +
                "id=" + id +
                ", downloadCores=" + downloadCores +
                ", defaultSaveFolder='" + defaultSaveFolder + '\'' +
                ", defaultTempFolder='" + defaultTempFolder + '\'' +
                ", deleteAfterDownload=" + deleteAfterDownload +
                ", autoExitAfterDownload=" + autoExitAfterDownload +
                ", scheduleDownload=" + scheduleDownload +
                ", notifyAfterDownload=" + notifyAfterDownload +
                ", background='" + background + '\'' +
                ", hotkeys='" + hotkeys + '\'' +
                '}';
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDownloadCores() {
        return downloadCores;
    }

    public void setDownloadCores(Integer downloadCores) {
        this.downloadCores = downloadCores;
    }

    public String getDefaultSaveFolder() {
        return defaultSaveFolder;
    }

    public void setDefaultSaveFolder(String defaultSaveFolder) {
        this.defaultSaveFolder = defaultSaveFolder;
    }

    public String getDefaultTempFolder() {
        return defaultTempFolder;
    }

    public void setDefaultTempFolder(String defaultTempFolder) {
        this.defaultTempFolder = defaultTempFolder;
    }

    public Boolean getDeleteAfterDownload() {
        return deleteAfterDownload;
    }

    public void setDeleteAfterDownload(Boolean deleteAfterDownload) {
        this.deleteAfterDownload = deleteAfterDownload;
    }

    public Boolean getAutoExitAfterDownload() {
        return autoExitAfterDownload;
    }

    public void setAutoExitAfterDownload(Boolean autoExitAfterDownload) {
        this.autoExitAfterDownload = autoExitAfterDownload;
    }

    public Boolean getScheduleDownload() {
        return scheduleDownload;
    }

    public void setScheduleDownload(Boolean scheduleDownload) {
        this.scheduleDownload = scheduleDownload;
    }

    public Boolean getNotifyAfterDownload() {
        return notifyAfterDownload;
    }

    public void setNotifyAfterDownload(Boolean notifyAfterDownload) {
        this.notifyAfterDownload = notifyAfterDownload;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getHotkeys() {
        return hotkeys;
    }

    public void setHotkeys(String hotkeys) {
        this.hotkeys = hotkeys;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
