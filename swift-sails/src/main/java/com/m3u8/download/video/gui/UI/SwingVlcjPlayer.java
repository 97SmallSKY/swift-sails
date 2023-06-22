package com.m3u8.download.video.gui.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.m3u8.download.video.m3u8.utils.StringUtils;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class SwingVlcjPlayer {
    private JFrame frame;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JPanel controlPanel;
    private JSlider progressSlider;
    private JButton volumeButton;

    private JSlider volumeSlider;
    private JButton playButton;
    private JButton refreshButton;
    private JButton backwardButton;
    private JButton forwardButton;
    private JLabel timeLabel;
    private JButton fullScreenButton;
    private boolean isMuted;
    private JLabel loadingLabel;

    private long videoDuration;

    public SwingVlcjPlayer(String url) {
        frame = new JFrame("Swing vlcj Player");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.m3u8.download.video.gui.utils.PositionAndStyleSetting.windowCentered(frame);


        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        frame.setContentPane(mediaPlayerComponent);

        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.BLACK);
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(controlPanel, BorderLayout.SOUTH);
        JPanel controlPanelTop = new JPanel(new BorderLayout());
        controlPanelTop.setBorder(new EmptyBorder(0, 0, 10, 0));
        controlPanelTop.setOpaque(false);

        progressSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        progressSlider.setUI(new CustomSliderUI());
        progressSlider.setPreferredSize(new Dimension(frame.getWidth() * 8 / 10, 30));
        progressSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                notScrollProgress();
            }
        });

        controlPanelTop.add(progressSlider, BorderLayout.CENTER);


        volumeButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/声音-小_volume-small.png")));
        volumeButton.setPreferredSize(new Dimension(frame.getWidth() * 3 / 100, 30));
        volumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isMuted = !isMuted;
                mediaPlayerComponent.mediaPlayer().audio().setMute(isMuted);
            }
        });
//        controlPanelTop.add(volumeButton, BorderLayout.EAST);

        volumeSlider = new JSlider();
        volumeSlider.setMaximum(100);
        volumeSlider.setMinimum(0);
        volumeSlider.setPreferredSize(new Dimension(frame.getWidth() * 11 / 100, 30));
        volumeSlider.addChangeListener(e -> {
            mediaPlayerComponent.mediaPlayer().audio().setVolume(volumeSlider.getValue());
        });

        JLabel blankJLabel = new JLabel();
        blankJLabel.setPreferredSize(new Dimension(frame.getWidth() * 3 / 100, 30));

        JPanel volumePanel = new JPanel(new BorderLayout());
        volumePanel.setOpaque(false);
        volumePanel.add(blankJLabel, BorderLayout.WEST);
        volumePanel.add(volumeButton, BorderLayout.CENTER);
        volumePanel.add(volumeSlider, BorderLayout.EAST);


        controlPanelTop.add(progressSlider, BorderLayout.CENTER);
        controlPanelTop.add(volumePanel, BorderLayout.EAST);


        JPanel controlPanelBottom = new JPanel(new GridLayout(1, 2, 10, 0));
        controlPanelBottom.setOpaque(false);

        JPanel controlPanelBottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanelBottomLeft.setOpaque(false);

        playButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/播放_play-one.png")));
        playButton.setPreferredSize(new Dimension(frame.getWidth() * 5 / 100, 30));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlayPauseVideoState();
            }
        });
        controlPanelBottomLeft.add(playButton);

        refreshButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/刷新_refresh.png")));
        refreshButton.setPreferredSize(new Dimension(frame.getWidth() * 5 / 100, 30));
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().stop();
                mediaPlayerComponent.mediaPlayer().media().play(url);
            }
        });
        controlPanelBottomLeft.add(refreshButton);
        backwardButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/双左_double-left.png")));
        backwardButton.setPreferredSize(new Dimension(frame.getWidth() * 5 / 100, 30));
        backwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quickForwardSetting(-10);
            }
        });
        controlPanelBottomLeft.add(backwardButton);

        forwardButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/双右_double-right.png")));
        forwardButton.setPreferredSize(new Dimension(frame.getWidth() * 5 / 100, 30));
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quickForwardSetting(10);
            }
        });
        controlPanelBottomLeft.add(forwardButton);

        timeLabel = new JLabel("00:00:00/00:00:00");
        timeLabel.setForeground(Color.WHITE);
        controlPanelBottomLeft.add(timeLabel);

        controlPanelBottom.add(controlPanelBottomLeft);

        JPanel controlPanelBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanelBottomRight.setOpaque(false);

        fullScreenButton = new JButton(new ImageIcon(getClass().getResource("/static/icon/全屏播放_full-screen-play.png")));
        fullScreenButton.setPreferredSize(new Dimension(frame.getWidth() * 5 / 100, 30));
        fullScreenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enterFullScreen();
            }
        });
        controlPanelBottomRight.add(fullScreenButton);

        controlPanelBottom.add(controlPanelBottomRight);

        controlPanel.add(controlPanelTop, BorderLayout.NORTH);
        controlPanel.add(controlPanelBottom, BorderLayout.SOUTH);

        loadingLabel = new JLabel();
        loadingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        loadingLabel.setVerticalAlignment(SwingConstants.TOP);
        loadingLabel.setForeground(new Color(84, 100, 172));
        loadingLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
        loadingLabel.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newText = (String) evt.getNewValue();
                if (StringUtils.isNotBlank(newText)) {
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        loadingLabel.setText("");
                        mediaPlayerComponent.repaint(); // 强制重绘mediaPlayerComponent
                    }, 1, TimeUnit.SECONDS); // 设置延迟时间，单位为秒
                }
            }
        });
        mediaPlayerComponent.add(loadingLabel, BorderLayout.NORTH);

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                videoDuration = newLength;
                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText("00:00:00/" + formatTime(newLength));
                });
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                String timeString = formatTime(newTime) + " / " + formatTime(videoDuration);
                timeLabel.setText(timeString);
            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
                SwingUtilities.invokeLater(() -> {
                    loadingLabel.setText("加载中... " + (int) (newCache) + "%");
                    mediaPlayerComponent.repaint(); // 强制重绘mediaPlayerComponent
                });
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(() -> {
                    loadingLabel.setText("");
                    mediaPlayerComponent.repaint(); // 强制重绘mediaPlayerComponent
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }
        });

        Component component = mediaPlayerComponent.videoSurfaceComponent();
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    exitFullScreen();
                } else if (keyCode == KeyEvent.VK_ENTER) {
                    enterFullScreen();
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    quickForwardSetting(-10);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    quickForwardSetting(10);
                } else if (keyCode == KeyEvent.VK_SPACE) {
                    PlayPauseVideoState();
                }
            }
        });

        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                mediaPlayerComponent.mediaPlayer().audio().setVolume(volumeSlider.getValue() + rotation);
            }
        });

        controlPanel.add(controlPanelTop, BorderLayout.NORTH);
        controlPanel.add(controlPanelBottom, BorderLayout.SOUTH);

        frame.setVisible(true);
        mediaPlayerComponent.mediaPlayer().media().play(url);
    }

    public void addSubtitle(String subtitlePath) {
        mediaPlayerComponent.mediaPlayer().subpictures().setSubTitleFile(subtitlePath);
    }

    /**
     * 滑动模块
     */
    private void notScrollProgress() {


        if (!progressSlider.getValueIsAdjusting()) {
            float position1 = BigDecimal.valueOf(progressSlider.getValue()).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP).floatValue();
            System.out.println("跳动到：" + position1);
            mediaPlayerComponent.mediaPlayer().controls().setPosition(position1);
            if (null == mediaPlayerComponent) {
                progressSlider.setValue((int) (position1*100));
                mediaPlayerComponent.mediaPlayer().controls().start();
            }
        }
        setStartTime();
    }

    /**
     * 根据状态暂停或者播放
     */
    private void PlayPauseVideoState() {
        if (mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
            mediaPlayerComponent.mediaPlayer().controls().pause();
        } else {
            long currentTime = mediaPlayerComponent.mediaPlayer().status().time();
            if (videoDuration - currentTime < 5) {
                mediaPlayerComponent.mediaPlayer().controls().stop();
                mediaPlayerComponent.mediaPlayer().controls().start();
                return;
            }
            mediaPlayerComponent.mediaPlayer().controls().play();
        }
    }

    /**
     * 快进事件
     *
     * @param time 正数代表快进，负数代表后退，代表秒数
     */
    private void quickForwardSetting(long time) {
        if (mediaPlayerComponent == null) {
            mediaPlayerComponent.mediaPlayer().controls().start();
        }
        long currentTime = mediaPlayerComponent.mediaPlayer().status().time();
        mediaPlayerComponent.mediaPlayer().controls().setTime(currentTime + time * 1000);
        loadingLabel.setText((time > 0 ? "快进" : "后退") + time + "秒");
        mediaPlayerComponent.repaint(); // 强制重绘mediaPlayerComponent
        long startTime = currentTime + time;
        // 更新进度和时间
        SwingUtilities.invokeLater(() -> {
            if (startTime < 0) {
                progressSlider.setValue(0);
                timeLabel.setText("00:00:00/" + formatTime(videoDuration));
                return;
            }
            if (startTime > videoDuration) {
                progressSlider.setValue(100);
                timeLabel.setText(formatTime(videoDuration) + "/" + formatTime(videoDuration));
                return;
            }
            progressSlider.setValue((int) (startTime / videoDuration));
            timeLabel.setText(formatTime(startTime) + "/" + videoDuration);

        });


    }

    /**
     * 全屏
     */
    private void enterFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (device.isFullScreenSupported()) {
            frame.dispose();
            frame.setUndecorated(true);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
            device.setFullScreenWindow(frame);
        }
    }

    /**
     * 退出全屏
     */
    private void exitFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (device != null && device.getFullScreenWindow() == frame) {
            device.setFullScreenWindow(null);
            frame.dispose();

            JFrame originalFrame = new JFrame("Swing vlcj Player");
            originalFrame.setSize(800, 600);
            originalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            originalFrame.setContentPane(mediaPlayerComponent);
            originalFrame.add(controlPanel, BorderLayout.SOUTH);
            originalFrame.setVisible(true);
            com.m3u8.download.video.gui.utils.PositionAndStyleSetting.windowCentered(originalFrame);
            frame = originalFrame;
            originalFrame = null;
        }
    }

    /**
     * 设置起始时间
     */
    private void setStartTime() {
        long currentTime = mediaPlayerComponent.mediaPlayer().status().time();
        // 读取进度
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(formatTime(currentTime) + "/" + formatTime(videoDuration));
        });
    }

    private String formatTime(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String url =
                        "";
                new SwingVlcjPlayer(url);
            }
        });
    }
}

class CustomSliderUI extends javax.swing.plaf.basic.BasicSliderUI {
    public CustomSliderUI() {
        super(null);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Rectangle thumbBounds = thumbRect;
        int w = thumbBounds.width;
        int h = thumbBounds.height;
        int arc = w / 2;
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, w, h, arc, arc);
    }
}