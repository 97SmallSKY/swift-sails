package com.m3u8.download.video.gui.utils.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogUtil {

    public static void main(String[] args) {
        System.out.println(showCustomDialog(
                "Confirmation",
                "<html>Are you sure you want to delete this file?<br>This action cannot be undone.</html>"));
    }

    public static int showCustomDialog(String title, String message, String... options) {

        return showCustomDialog(title, message, JOptionPane.INFORMATION_MESSAGE, options);
    }

    public static int showCustomDialog(String title, String message, int type, String... options) {
        Font fontMsg = new Font("宋体", Font.PLAIN, 18);
        Font fontBtn = new Font("正楷", Font.BOLD, 18);
        if (options.length == 0) {
            options = new String[]{"确认", "取消"}; // 如果 options 为空，则设置默认的确认和取消按钮
        }

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 设置关闭操作为不做任何操作

        if (type == JOptionPane.ERROR_MESSAGE || type == JOptionPane.WARNING_MESSAGE) {
            dialog.setUndecorated(true); // 如果标题为错误或警告，则去掉窗体的装饰，包括右上角的关闭按钮
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置边框间距为10

        JLabel label = new JLabel(message);
        label.setFont(fontMsg);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置文本与边框的间距为10
        panel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置按钮与边框的间距为10

        final int[] index = {-1}; // 使用数组保存索引值

        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            button.setFont(fontBtn);
            final int buttonIndex = i; // 使用final变量保存索引值
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    index[0] = buttonIndex; // 更新index变量的值
                    dialog.dispose();
                }
            });
            buttonPanel.add(button);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        // 如果对话框没有关闭，则返回-1
        if (dialog.isShowing()) {
            return -1;
        }

        // 返回用户点击的按钮的索引
        return index[0];
    }
}
