package com.m3u8.download.video.m3u8.download;

import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.bean.po.Settings;
import com.m3u8.download.video.gui.utils.dialog.DialogUtil;
import com.m3u8.download.video.m3u8.Exception.M3u8Exception;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.download.video.m3u8.listener.DownloadListener;
import com.m3u8.download.video.m3u8.listener.DownloadListenerImpl;
import com.m3u8.download.video.m3u8.uiEnum.TableColumnEnum;
import com.m3u8.download.video.m3u8.utils.StringUtils;
import com.m3u8.service.DownloadSegmentService;
import com.m3u8.service.DownloadTaskService;
import com.m3u8.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 下载管理类
 *
 * @author Small_Tsk
 * @create 2023-06-16
 **/
@Component
public class DownloadManager {

    public static ConcurrentHashMap<Integer, M3U8ResumeDownloader>
            ROW_M3U8DOWNLOAD_MAP = new ConcurrentHashMap();


    public static DownloadTaskService downloadTaskService;

    public static DownloadSegmentService downloadSegmentService;

    public static SettingsService settingsService;

    @Autowired
    public void setDownloadTaskService(DownloadTaskService downloadTaskService) {
        this.downloadTaskService = downloadTaskService;
    }

    @Autowired
    public void setDownloadSegmentService(DownloadSegmentService downloadSegmentService) {
        this.downloadSegmentService = downloadSegmentService;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * 断点续传
     *
     * @param url   片段连接
     * @param model 表格的model
     */
    public static void downloadFileByUrl(String url, DefaultTableModel model) {
        try {
            int row = model.getRowCount() - 1;
            DownloadTask downloadTask = downloadTaskService.selectDownloadTaskByUrl(url);
            // 未找到下载记录
            if (null == downloadTask) {
                return;
            }
            // 下载已完成
            if (downloadTask.getStatus().equals(DownloadStatusEnum.COMPLETED.get())) {
                return;
            }
            List<DownloadSegment> downloadSegments = downloadSegmentService
                    .selectListByDownloadTaskId(downloadTask.getId());
            // 未找到下载片段
            if (downloadSegments.size() == 0) {
                return;
            }
            // 读取临时文件夹
            Settings settings = settingsService.selectSetting();
            File tempDir = new File(settings.getDefaultTempFolder());
            if (!tempDir.exists()) {
                boolean mkdirs = tempDir.mkdirs();
                if (!mkdirs) {
                    JOptionPane.showMessageDialog(null,
                            "临时文件夹创建失败，请重新设置临时目录：" + tempDir.getAbsolutePath(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                    model.removeRow(row);
                    return;
                }
            }

            // 已下载的url
            Set<String> downloadedUrl = downloadSegments.stream()
                    .filter(item -> item.getStatus().equals(DownloadStatusEnum.COMPLETED))
                    .map(item -> item.getSegmentPath())
                    .collect(Collectors.toSet());
            // 已下载的片段
            Set<File> finishedFiles = downloadSegments.stream()
                    .filter(item -> item.getStatus().equals(DownloadStatusEnum.COMPLETED)
                            && StringUtils.isNotBlank(item.getStatus()))
                    .map(item -> new File(item.getSaveDirectory()))
                    .collect(Collectors.toSet());

            // 监听器
            DownloadListenerImpl downloadListener = new DownloadListenerImpl();
            downloadListener.setModel(model);
            downloadListener.setRowIndex(row);
            downloadListener.setDownloadTask(downloadTask);
            Set<DownloadListener> downloadListeners = new HashSet<>();
            downloadListeners.add(downloadListener);

            BigDecimal bigDecimal = null == downloadTask.getDownloadBytes() ? downloadTask.getDownloadBytes() : BigDecimal.ZERO;
            // 参数： 名称、保存位置、临时文件保存位置、核心数
            M3U8ResumeDownloader m3U8ResumeDownloader = new M3U8ResumeDownloader(downloadTask.getThreadCount(),
                    downloadTask.getSaveDirectory(), downloadTask.getTempDirectory(),
                    downloadTask.getName(), finishedFiles.size(), finishedFiles, downloadTask.getM3u8Key(),
                    downloadTask.getKeyBytes().getBytes(), downloadTask.isByte(), bigDecimal,
                    downloadTask.getTotalFileSize(), downloadListeners, downloadTask.getSuffix(), true,
                    false, downloadSegments.stream().collect(Collectors.toCollection(LinkedHashSet::new))

            );
            // 记录实例，用于恢复下载
            ROW_M3U8DOWNLOAD_MAP.put(row, m3U8ResumeDownloader);
            m3U8ResumeDownloader.setThreadCount(downloadTask.getThreadCount());
            m3U8ResumeDownloader.setTEMP_PATH(System.getProperty("user.dir") + File.separator + "temp" + File.separator + downloadTask.getName());
            m3U8ResumeDownloader.setDownloadedUrl(downloadedUrl);
            m3U8ResumeDownloader.setDownloadTask(downloadTask);
            // 开始分片下载
            m3U8ResumeDownloader.startDownload();

        } catch (Exception e) {
            // 创建错误窗口
            DialogUtil.showCustomDialog("系统错误", e.getMessage());
            throw new RuntimeException(e);

        }
    }

    /**
     * 获取链接片段
     *
     * @param url
     * @param fileName
     * @param position
     * @param suffix
     */
    public void getTsUrl(String url, String fileName, String position, String suffix) {
        M3U8SegmentInfoExtractor m3U8SegmentInfoExtractor = new M3U8SegmentInfoExtractor(url);
        HashMap<Class, Object> map = m3U8SegmentInfoExtractor.getTsUrl();
        if (null == map) {
            // 创建错误窗口
            int showCustomDialog = DialogUtil.showCustomDialog("系统错误", "请联系管理员");
            return;

        }
        DownloadTask downloadTask = (DownloadTask) map.get(DownloadTask.class);
        downloadTask.setName(fileName);
        downloadTask.setSaveDirectory(position);
        downloadTask.setSuffix(suffix);
        downloadTask.setThreadCount(15);
        downloadTask.setDownloadBytes(BigDecimal.ZERO);
        downloadTask.setTempDirectory(System.getProperty("user.dir") + File.separator + fileName);
        ArrayList<DownloadSegment> downloadSegmentArrayList = (ArrayList<DownloadSegment>) map.get(DownloadSegment.class);
        int i = downloadTaskService.insertDownloadTask(downloadTask);
        if (i < 1) {
            // 创建错误窗口
            int showCustomDialog = DialogUtil.showCustomDialog(
                    "重复下载", "已经下载过该任务，是否忽略继续下载",
                    new String[]{"重新下载,取消"});
            if (showCustomDialog == 0) {
                // 确定
                downloadTaskService.deleteDownloadTaskByUrl(downloadTask.getDownloadUrl());
                downloadSegmentService.deleteSegmentListByDownloadId(Collections.singletonList(downloadTask.getId()));
                downloadTaskService.insertDownloadTask(downloadTask);
            } else {
                return;
            }
        }

        // 插入片段信息
        downloadSegmentService.insertListSegment(
                downloadSegmentArrayList.stream()
                        .map(item -> {
                            item.setDownloadTaskId(downloadTask.getId());
                            item.setStatus(DownloadStatusEnum.CONNECTING.get());
                            return item;
                        })
                        .collect(Collectors.toList())
        );

    }

    /**
     * 暂停下载
     *
     * @param rowIndex 表格第rowIndex行数据
     * @return
     */
    public static boolean pauseDownloadTask(int rowIndex) {
        M3U8ResumeDownloader m3u8Download = ROW_M3U8DOWNLOAD_MAP.get(rowIndex);
        if (null == m3u8Download) {
            DialogUtil.showCustomDialog("错误", "未找到对应的下载，请联系管理员");
            return false;
        }
        m3u8Download.setPause(true);
        // 结束线程
        ExecutorService fixedThreadPool = m3u8Download.getFixedThreadPool();
        if (fixedThreadPool != null) {
            boolean b = !fixedThreadPool.isShutdown();
            if (b) {
                fixedThreadPool.shutdownNow();
            }
        }
        // 关闭监听事件
        m3u8Download.setListening(false);
        return true;
    }

    /**
     * 恢复下载
     *
     * @param rowIndex 表格第rowIndex行数据
     * @return
     */
    public static boolean resumeDownload(int rowIndex, JTable table) {
        M3U8ResumeDownloader m3u8Download = ROW_M3U8DOWNLOAD_MAP.get(rowIndex);
        String link = (String) table.getValueAt(rowIndex, TableColumnEnum.LINK.getColumnIndex());

        if (null == m3u8Download) {
            // 查找数据库

            DownloadTask downloadTask = downloadTaskService.selectDownloadTaskByUrl(link);
            if (null == downloadTask || StringUtils.isBlank(downloadTask.getDownloadUrl())) {
                DialogUtil.showCustomDialog("错误", "未找到对应的下载，请联系管理员");
                return false;
            }
            downloadFileByUrl(downloadTask.getDownloadUrl(), (DefaultTableModel) table.getModel());
            return true;
        }

        // 直接恢复
        m3u8Download.setListening(true);
        m3u8Download.startDownload();

        return true;
    }

    /**
     * 取消下载
     *
     * @param rowIndex 选择取消的行数
     * @return
     */
    public static boolean cancelDownloadTask(int rowIndex) {
        M3U8ResumeDownloader m3u8Download = ROW_M3U8DOWNLOAD_MAP.get(rowIndex);
        if (null == m3u8Download) {
            // 列表删除
            throw new M3u8Exception("此数据不存在表格中：" + rowIndex);
            // todo 数据库数据失效
        }
        pauseDownloadTask(rowIndex);
        // 取消下载线程
        m3u8Download.getFixedThreadPool().shutdown();

        // 清空下载队列
        m3u8Download.getBLOCKING_QUEUE().clear();
        // 重置已完成的片段数量
        m3u8Download.setFinishedCount(0);
        // 重置下载大小
        m3u8Download.setDownloadBytes(BigDecimal.ZERO);
        // 清空已完成的文件集合
        m3u8Download.getFinishedFiles().clear();
        String tempPath = m3u8Download.getTEMP_PATH();
        // 删除文件夹
        try {
            Files.walk(Path.of(m3u8Download.getTEMP_PATH()))
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            // 等待已暂停状态附上
            Thread.sleep(1000);
        } catch (IOException e) {
            throw new M3u8Exception("删除对应临时文件夹失败：" + m3u8Download.getTEMP_PATH());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 释放内存
        ROW_M3U8DOWNLOAD_MAP.put(rowIndex, null);
        m3u8Download = null;

        return true;
    }
}
