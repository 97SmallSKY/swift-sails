package com.m3u8.download.video;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;


/**
 * @author Small_Tsk
 * @create 2023-06-03
 **/
public class mainTest {

    private static JLabel jLabel = new JLabel();
    private static JPanel jpanel = null;

    private final static Font font = new Font("宋体", Font.BOLD, 22);

    private final static int jfWidth = 800;
    private final static int jfHeight = 450;


    public static void main(String[] args) {
        swing();
//        showMouseLocation(jLabel, jpanel);
//        jLabel.setText("完成");
//        MyCallable(jLabel, jpanel);
    }

    public static void swing() {
        // 1. 创建一个顶层容器（窗口）

        JFrame jf = new JFrame("测试下载m3u8");          // 创建窗口
        // 设置窗口大小
        jf.setSize(jfWidth, jfHeight);
        // 把窗口位置设置到屏幕中心
        jf.setLocationRelativeTo(null);
        // 当点击窗口的关闭按钮时退出程序（没有这一句，程序不会退出）
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


//        设置布局方式
        SpringLayout layout = new SpringLayout();


        // 2. 创建中间容器（面板容器）
        JPanel panel = new JPanel(layout);// 创建面板容器，使用默认的布局管理器


        jpanel = panel;


        //  下载链接
        JLabel label = new JLabel("请输入需要下载的m3u8链接：");


        JTextField m3u8URL = new JTextField(".m3u8", 20);

        // 3. 创建一个按钮，并添加到 面板容器 中
        JButton downloadBtn = new JButton("下载");
        JButton batchDownloadBtn = new JButton("批量下载");
        JButton onlineWatchBtn = new JButton("在线观看");
        JButton resourceSniffingBtn = new JButton("资源嗅探");

        downloadBtn.setName("download_btn");
        batchDownloadBtn.setName("_batch_download_btn");
        onlineWatchBtn.setName("online_watch_btn");
        resourceSniffingBtn.setName("resource_sniffing_btn");

        downloadBtn.setBackground(new Color(165, 214, 63));
        batchDownloadBtn.setBackground(new Color(255, 195, 0));
        onlineWatchBtn.setBackground(new Color(67, 207, 124));
        resourceSniffingBtn.setBackground(new Color(204, 165, 126));

//        创建进度条
        JProgressBar progressBar = new JProgressBar();
//        进度条说明文字
        JLabel fileName = new JLabel("文件名");
        JLabel completedProgress = new JLabel("正在连接...");
        JLabel internetSpeed = new JLabel("2M/s");
//        下载完成列表
        JLabel completeFileList = new JLabel("已完成： 《海上钢琴师》（F:\\m3u8Video） 640.3M");
//        打开下载列表
        JButton openDownloadList = new JButton("打开下载列表");
        openDownloadList.setName("open_download_list");

        btnClickEvent(downloadBtn, batchDownloadBtn, onlineWatchBtn, resourceSniffingBtn, openDownloadList);

        m3u8URL.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    检查m3u8是否正确
                    JOptionPane.showMessageDialog(null, "链接输入有误！");
                    downloadBtn.doClick();

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        jLabel.setText("鼠标位置");


        setLocation(m3u8URL);
        panel.add(label);
        panel.add(m3u8URL);
        panel.add(downloadBtn);
        panel.add(batchDownloadBtn);
        panel.add(onlineWatchBtn);
        panel.add(resourceSniffingBtn);
        panel.add(progressBar);
//        panel.add(setProcessBar());
//        panel.add(jLabel);
        panel.add(completedProgress);
        panel.add(fileName);
        panel.add(internetSpeed);
        panel.add(completeFileList);
        panel.add(openDownloadList);

//        showMouseLocation(jLabel,panel);
        // 4. 把 面板容器 作为窗口的内容面板 设置到 窗口
        jf.setContentPane(panel);

        // 5. 显示窗口，前面创建的信息都在内存中，通过 jf.setVisible(true) 把内存中的窗口显示在屏幕上。
        jf.setVisible(true);

        label.setFont(font);


        /*
         * 组件的约束设置（弹性布局设置的关键）
         */
        // 标签组件约束: 设置标签的左上角坐标为 (5, 5)

        setSpringLayoutStyle(layout, label, 0, 0, 40, 20);

        setSpringLayoutStyle(layout, m3u8URL, 450, 40, getComponentWidth(layout, label), 20);

        setSpringLayoutStyle(layout, downloadBtn, 100, 30, 50, 100);
        setSpringLayoutStyle(layout, batchDownloadBtn, 100, 30,
                getComponentWidth(layout, downloadBtn) + 100, 100);
        setSpringLayoutStyle(layout, onlineWatchBtn, 100, 30,
                getComponentWidth(layout, batchDownloadBtn) + 100, 100);
        setSpringLayoutStyle(layout, resourceSniffingBtn, 100, 30,
                getComponentWidth(layout, onlineWatchBtn) + 100, 100);

        setSpringLayoutStyle(layout, progressBar, 600, 20, 10, 300);

        setSpringLayoutStyle(layout, fileName, 0, 0, 20, 280);

        setSpringLayoutStyle(layout, completedProgress, 600, 20,
                getComponentWidth(layout, progressBar) - completedProgress.getWidth() - 5, 280);

        setSpringLayoutStyle(layout, internetSpeed, 600, 20,
                getComponentWidth(layout, progressBar) + 1, 300);

        setSpringLayoutStyle(layout, completeFileList, 600, 20,
                20, 350);


        setSpringLayoutStyle(layout, openDownloadList, 100, 20,
                getComponentWidth(layout, completeFileList) + 5, 350);

        progressBar.setBackground(new Color(255, 255, 255));
        progressBar.setValue(20);

        completedProgress.setForeground(new Color(246, 2, 2));
        fileName.setForeground(Color.RED);
        internetSpeed.setForeground(Color.RED);


        setSpringLayoutStyle(layout, jLabel, 0, 0, 100, 300);



    }


    public static void btnClickEvent(JButton... btns) {
        for (JButton btn : btns) {
            btn.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "开始下载");
//   todo         跳转到下载选择页面
//                不可点击
                btn.setEnabled(false);
                btn.setText("正在下载...");
            });
        }

    }

    public static int getComponentWidth(SpringLayout layout, JTextComponent jTextComponent) {
        return layout.getConstraints(jTextComponent).getWidth().getValue() + layout.getConstraints(jTextComponent).getX().getValue();
    }

    public static int getComponentWidth(SpringLayout layout, JComponent jTextComponent) {
        return layout.getConstraints(jTextComponent).getWidth().getValue() + layout.getConstraints(jTextComponent).getX().getValue();
    }

    /**
     * 弹性布局统一设置相对距离
     *
     * @param layout
     * @param jTextComponent
     * @param width
     * @param height
     * @param x
     * @param y
     */
    public static void setSpringLayoutStyle(SpringLayout layout, JTextComponent jTextComponent, int width, int height,
                                            int x, int y) {

        if (layout != null) {
            SpringLayout.Constraints constraints = layout.getConstraints(jTextComponent);
            if (width > 0) {
                constraints.setWidth(Spring.constant(width));
            }
            if (height > 0) {
                constraints.setHeight((Spring.constant(height)));
            }
            if (x > 0) {
                constraints.setX(Spring.constant(x));
            }
            if (y > 0) {
                constraints.setY(Spring.constant(y));
            }
        }
    }

    public static void setSpringLayoutStyle(SpringLayout layout, Component jTextComponent, int width, int height,
                                            int x, int y) {
        if (layout != null) {
            SpringLayout.Constraints constraints = layout.getConstraints(jTextComponent);
            if (width > 0) {
                constraints.setWidth(Spring.constant(width));
            }
            if (height > 0) {
                constraints.setHeight((Spring.constant(height)));
            }
            if (x > 0) {
                constraints.setX(Spring.constant(x));
            }
            if (y > 0) {
                constraints.setY(Spring.constant(y));
            }
        }
    }

    public static void setStyleText(SpringLayout layout, JTextField textField) {

    }


    public static JProgressBar setProcessBar() {
        JProgressBar bar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);

        return bar;
    }

    public static void setLocation(JTextField textField) {
        textField.setBounds(613, 274, textField.getWidth(), textField.getHeight());
    }


    public static void MyCallable(JLabel label, JPanel panel) {
        ExecutorService executor = Executors.newCachedThreadPool();

        // FutureTask包装callbale任务，再交给线程池执行
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            while (true) {
//                System.out.println("子线程开始：");
                String s = MouseInfo.getPointerInfo().getLocation().toString();
                label.setText(s);
                panel.updateUI();
            }
        });


// 线程池执行任务， 运行结果在 futureTask 对象里面
        executor.submit(futureTask);
        try {
            System.out.println("task运行结果计算的总和为：" + futureTask.get());
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

}

