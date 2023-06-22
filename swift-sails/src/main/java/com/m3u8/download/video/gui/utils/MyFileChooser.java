package com.m3u8.download.video.gui.utils;

/**
 * @author Small_Tsk
 * @create 2023-06-12
 **/

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MyFileChooser {

    public static final String SAVE = "save";
    public static final String IMG = "img";

    private static final Dimension dimension = new Dimension(800, 600);


    /**
     * 保存文件选择器
     *
     * @param openPosition 是保存还是图像
     */
    public static String fileChooser(String openPosition) {
        JFrame frame = new JFrame("文件选择器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser fileChooser = null;
        switch (openPosition) {
            case SAVE:
                // todo h2
                fileChooser = new JFileChooser();
            case IMG:
                fileChooser = new JFileChooser();
            default:
                fileChooser = new JFileChooser(System.getProperty("user.dir"));

        }
        fileChooser.setEnabled(true);

        fileChooser.setPreferredSize(dimension);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setApproveButtonText("确定");

//        FileNameExtensionFilter filter = new FileNameExtensionFilter(type);
//        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                return file.getAbsolutePath();
            }
        }

        frame.pack();
        frame.setVisible(true);
        return null;
    }

    /**
     * 保存文件
     *
     * @return
     */
    public static String fileChooserBySaveFile(String openPosition) {
        JFrame frame = new JFrame("文件选择器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser fileChooser = null;
        switch (openPosition) {
            case SAVE:
                // todo h2
                fileChooser = new JFileChooser();
            case IMG:
                fileChooser = new JFileChooser();
            default:
                fileChooser = new JFileChooser(System.getProperty("user.dir"));

        }
        fileChooser.setEnabled(true);
        fileChooser.setPreferredSize(dimension);
        fileChooser.setDialogTitle("保存文件");
        fileChooser.setApproveButtonText("保存");

//        FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文件 (*.txt)", "txt");
//        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        frame.pack();
        frame.setVisible(true);
        return null;
    }


    /**
     * 目录选择器
     *
     * @return
     */
    public static String fileChooserByDir(String openPosition) {
        JFrame frame = new JFrame("文件选择器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser fileChooser = null;
        switch (openPosition) {
            case SAVE:
                // todo h2
                fileChooser = new JFileChooser();
            case IMG:
                fileChooser = new JFileChooser();
            default:
                fileChooser = new JFileChooser(System.getProperty("user.dir"));

        }
        fileChooser.setEnabled(true);
        fileChooser.setPreferredSize(dimension);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("选择");

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        frame.pack();
        frame.setVisible(true);
        return null;
    }
}


