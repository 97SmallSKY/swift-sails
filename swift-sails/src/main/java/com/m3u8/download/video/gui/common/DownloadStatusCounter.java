package com.m3u8.download.video.gui.common;

import com.m3u8.download.video.gui.utils.LoadingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author Small_Tsk
 * @create 2023-06-20
 **/
public class DownloadStatusCounter {
    private static int allCount = 0;
    private static int downloadingCount = 0;
    private static int finishedCount = 0;
    private static int stoppedCount = 0;
    private static int errorCount = 0;

    private static JCheckBox allJC;
    private static JCheckBox downloadingJC;
    private static JCheckBox finishedJC;
    private static JCheckBox stoppedJC;
    private static JCheckBox errorJC;
    private static DefaultTableModel model;
    private static JFrame jFrame;

    public static void initJCText(){
        allJC.setText("全部（" + allCount + ")");
        downloadingJC.setText("下载中（" + downloadingCount + ")");
        finishedJC.setText("已完成（" + finishedCount + ")");
        stoppedJC.setText("已停止（" + stoppedCount + ")");
        errorJC.setText("错误（" + errorCount + ")");
        model.fireTableDataChanged();
        LoadingUtils.showLoadingPage(jFrame, 0.1F);

    }


    public static void addDownloadingCount() {
        downloadingCount++;
        downloadingJC.setText("下载中（" + downloadingCount + "）");
        flush();
    }

    public static void addFinishedCount() {
        finishedCount++;
        finishedJC.setText("已完成（" + finishedCount + "）");
        flush();
    }

    public static void addStoppedCount() {
        stoppedCount++;
        stoppedJC.setText("已停止（" + stoppedCount + "）");
        flush();
    }

    public static void addErrorCount() {
        errorCount++;
        errorJC.setText("错误（" + errorCount + "）");
        flush();
    }

    private static void addAllCount(){
        allCount++;
        allJC.setText("全部（" + allCount + "）");
    }

    private static void flush() {
        addAllCount();
        model.fireTableDataChanged();
        LoadingUtils.showLoadingPage(jFrame, 0.01F);
    }


    public static JCheckBox getAllJC() {
        return allJC;
    }

    public static void setAllJC(JCheckBox allJC) {
        DownloadStatusCounter.allJC = allJC;
    }

    public static JCheckBox getDownloadingJC() {
        return downloadingJC;
    }

    public static void setDownloadingJC(JCheckBox downloadingJC) {
        DownloadStatusCounter.downloadingJC = downloadingJC;
    }

    public static JCheckBox getFinishedJC() {
        return finishedJC;
    }

    public static void setFinishedJC(JCheckBox finishedJC) {
        DownloadStatusCounter.finishedJC = finishedJC;
    }

    public static JCheckBox getStoppedJC() {
        return stoppedJC;
    }

    public static void setStoppedJC(JCheckBox stoppedJC) {
        DownloadStatusCounter.stoppedJC = stoppedJC;
    }

    public static JCheckBox getErrorJC() {
        return errorJC;
    }

    public static void setErrorJC(JCheckBox errorJC) {
        DownloadStatusCounter.errorJC = errorJC;
    }

    public static DefaultTableModel getModel() {
        return model;
    }

    public static void setModel(DefaultTableModel model) {
        DownloadStatusCounter.model = model;
    }

    public static JFrame getjFrame() {
        return jFrame;
    }

    public static void setjFrame(JFrame jFrame) {
        DownloadStatusCounter.jFrame = jFrame;
    }
}

