package com.m3u8.download.video.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author Small_Tsk
 * @create 2023-06-11
 **/
public class PositionAndStyleSetting {


    public static void windowCentered(Frame frame) {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        System.out.println(frame.getWidth()+","+ frame.getHeight());
        frame.setLocation((int) ((defaultToolkit.getScreenSize().getWidth() - frame.getWidth()) / 2),
                (int) ((defaultToolkit.getScreenSize().getHeight() - frame.getHeight()) / 2));

    }
}
