package com.m3u8.download.video.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class LoadingUtils {
    public static void showLoadingPage(JFrame parentFrame, float durationInSeconds) {
        JDialog loadingDialog = new JDialog(parentFrame, "Loading", Dialog.ModalityType.APPLICATION_MODAL);
        loadingDialog.setSize(400, 100);
        loadingDialog.setLocationRelativeTo(parentFrame);

        JPanel loadingPanel = new JPanel() {
            private long startTime = System.currentTimeMillis();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // 计算矩形的位置和大小
                int padding = 50;
                int rectX = 0;
                int rectY = 30;
                int rectWidth = 400;
                int rectHeight = 10;

                // 计算颜色渐变值
                long elapsedTime = System.currentTimeMillis() - startTime;
                float hue = (elapsedTime % (durationInSeconds * 1000)) /  (durationInSeconds * 1000);

                // 创建渐变色
                Color startColor = Color.getHSBColor(hue, 1, 1);
                Color endColor = Color.getHSBColor((hue + 0.5f) % 1, 1, 1);
                GradientPaint gradientPaint = new GradientPaint(rectX, rectY, startColor, rectX + rectWidth, rectY + rectHeight, endColor);

                // 绘制矩形
                g2d.setPaint(gradientPaint);
                g2d.fill(new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight));

                g2d.dispose();

                // 检查持续时间，超过指定时间后关闭加载页面
                if (elapsedTime >= durationInSeconds * 1000) {
                    loadingDialog.dispose();
                } else {
                    repaint();
                }
            }
        };

        loadingDialog.add(loadingPanel);
        loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loadingDialog.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 调用显示加载页面的方法，并指定持续时间为3秒
        showLoadingPage(frame, 3);
    }
}
