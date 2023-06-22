package com.m3u8.download.video.gui.UI.event;

import com.m3u8.download.video.gui.UI.DownloadSettingPage;
import com.m3u8.download.video.gui.UI.SettingPage;
import com.m3u8.download.video.gui.common.MenuConstants;
import com.m3u8.download.video.gui.UI.HomeDownloadJF;
import com.m3u8.download.video.gui.utils.LoadingUtils;
import com.m3u8.download.video.gui.utils.dialog.DialogUtil;
import com.m3u8.download.video.m3u8.download.DownloadManager;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.download.video.m3u8.uiEnum.TableColumnEnum;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Small_Tsk
 * @create 2023-06-15
 **/
public class MenuEvent implements ActionListener {
    // 菜单名
    private String name;
    // 父窗体
    private HomeDownloadJF parentFrame;
    // 表实例
    private JTable table;

    // 选择的行
    public int[] selectedRows;

    private final List list = new ArrayList<String>();

    {
        list.add(MenuConstants.PAUSE_DOWNLOAD);
        list.add(MenuConstants.RESUME_DOWNLOAD);
        list.add(MenuConstants.START_ALL);
        list.add(MenuConstants.CANCEL_DOWNLOAD);
    }

    public MenuEvent(String name) {
        this.name = name;
    }


    public MenuEvent(String name, JTable table) {
        this.name = name;
        this.parentFrame = parentFrame;
        this.table = table;
    }

    public MenuEvent(String name, HomeDownloadJF parentFrame, JTable table) {
        this.name = name;
        this.parentFrame = parentFrame;
        this.table = table;
    }

    private DefaultTableModel model;

    public MenuEvent(String name, JTable table, DefaultTableModel model) {
        this.name = name;
        this.parentFrame = parentFrame;
        this.table = table;
        this.model = model;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // 有关的菜单触发


        if (MenuConstants.NEW_MENU.equals(name)) {

            DownloadSettingPage page = new DownloadSettingPage();
            page.setVisible(true);
            page.setParentFrame(parentFrame);

        } else if (MenuConstants.IMPORT_DOWNLOAD.equals(name)) {
            // todo 导入下载


        } else if (MenuConstants.CANCEL_DOWNLOAD.equals(name)) {

            changeStatusByMenuAndStatus(MenuConstants.CANCEL_DOWNLOAD,
                    DownloadStatusEnum.DOWNLOADING.get(),
                    DownloadStatusEnum.NOT_STARTED.get(),
                    DownloadStatusEnum.CONNECTING.get(),
                    DownloadStatusEnum.PAUSED.get()

            );

        } else if (MenuConstants.EXPORT_DOWNLOAD.equals(name)) {
            // todo 导出下载

        } else if (MenuConstants.PAUSE_DOWNLOAD.equals(name)) {
            // 暂停下载

            changeStatusByMenuAndStatus(
                    MenuConstants.PAUSE_DOWNLOAD,
                    DownloadStatusEnum.CONNECTING.get(),
                    DownloadStatusEnum.DOWNLOADING.get());

        } else if (MenuConstants.DELETE_DOWNLOADED_FILE.equals(name)) {
                // 下载完成删除
            changeStatusByMenuAndStatus(MenuConstants.DELETE_DOWNLOADED_FILE, DownloadStatusEnum.CONNECTING.get());

        } else if (MenuConstants.OPEN_DOWNLOAD_FOLDER.equals(name)) {

            // todo 设置里的下载文件保存位置
            File folder = new File("");
            if (!folder.exists() || !folder.isDirectory()) {
                DialogUtil.showCustomDialog("错误", "指定的路径不是有效的文件夹路径");
                return;
            }

            try {
                Desktop.getDesktop().open(folder);
            } catch (IOException en) {
                DialogUtil.showCustomDialog("错误", "无法打开文件：" + "下载文件");
            }

        } else if (MenuConstants.SETTING.equals(name)) {

            new SettingPage().setVisible(true);

        } else if (MenuConstants.START_ALL.equals(name) || MenuConstants.RESUME_DOWNLOAD.equals(name)) {

            changeStatusByMenuAndStatus(MenuConstants.START_ALL, DownloadStatusEnum.PAUSED.get());
        }
    }


    /**
     * 根据选择菜单和下载状态去操作
     *
     * @param menuStatus
     * @param downloadStatus
     * @return
     */
    private boolean changeStatusByMenuAndStatus(String menuStatus, String... downloadStatus) {

        LoadingUtils.showLoadingPage(new JFrame(), 0.1F);
        setSelectedRows();
        if (null == selectedRows) {
            DialogUtil.showCustomDialog("警告", "非法行为");
            return false;
        }
        if (MenuConstants.CANCEL_DOWNLOAD.equals(menuStatus)) {
            // 取消下载
            StringBuffer msg = new StringBuffer();
            if (selectedRows.length == table.getRowCount()) {
                msg.append("<html>你确认要取消所有<span style=\"color: red;\">非完成任务</span>吗？<h1>取消不可恢复<h1></html>");
            } else {
                msg.append("<html>你确认要取消<span style=\"color: red;\">非完成任务</span>吗？<h1>取消不可恢复<h1></html>");

                String nameCollect = Arrays.stream(selectedRows)
                        .mapToObj(i -> table.getValueAt(i, TableColumnEnum.NAME.getColumnIndex()) + "")
                        .collect(Collectors.joining(",<br>"));
                msg.append(nameCollect);
            }

            int select = DialogUtil.showCustomDialog("取消任务", msg.toString());
            if (select == 1) {
                // 取消
                return false;
            }
        }
        // 创建并执行SwingWorker
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 在后台线程中执行耗时的操作
                doTimeConsumingTask(menuStatus, downloadStatus);
                return null;
            }

            @Override
            protected void done() {
                // 在完成后更新UI
                updateUI(menuStatus,
                        TableColumnEnum.STATUS.getColumnIndex(), downloadStatus);
            }
        };
        worker.execute(); // 执行SwingWorker

        return true;
    }

    public void updateUI(String menuStatus, int columnIndex, String... downloadStatus) {
        for (int i = 0; i < selectedRows.length; i++) {
            String status = (String) table.getValueAt(i, TableColumnEnum.STATUS.getColumnIndex());
            if (Arrays.stream(downloadStatus).anyMatch(s -> s.equals(status))) {
                if (MenuConstants.CANCEL_DOWNLOAD.equals(menuStatus)) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setValueAt("已取消", i, columnIndex);
                    model.setValueAt("", i, TableColumnEnum.SPEED.getColumnIndex());
                    model.setValueAt("", i, TableColumnEnum.PROGRESS.getColumnIndex());
                    model.setValueAt("", i, TableColumnEnum.SIZE.getColumnIndex());
                    model.setValueAt("", i, TableColumnEnum.ELAPSED_TIME.getColumnIndex());
                    model.fireTableCellUpdated(i, columnIndex);

                }
            }
        }
    }


    public void doTimeConsumingTask(String menuStatus, String... downloadStatus) {
        for (int i = 0; i < selectedRows.length; i++) {
            String status = (String) table.getValueAt(i, TableColumnEnum.STATUS.getColumnIndex());
            if (Arrays.stream(downloadStatus).anyMatch(s -> s.equals(status))) {
                if (MenuConstants.START_ALL.equals(menuStatus)) {
                    // 开始全部 / 恢复下载
                    if (null != DownloadManager.ROW_M3U8DOWNLOAD_MAP.get(i)) {
                        // 恢复下载
                        DownloadManager.resumeDownload(i, table);
                    } else {
                        // 从数据库恢复下载
                        DownloadManager.downloadFileByUrl(
                                (String) table.getValueAt(i, TableColumnEnum.LINK.getColumnIndex()),
                                (DefaultTableModel) table.getModel()
                              );
                    }

                } else if (MenuConstants.DELETE_DOWNLOADED_FILE.equals(menuStatus)) {
                    // 删除下载完成文件
                    table.remove(i);
                } else if (MenuConstants.PAUSE_DOWNLOAD.equals(menuStatus)) {
                    // 暂停下载
                    DownloadManager.pauseDownloadTask(i);
                } else if (MenuConstants.CANCEL_DOWNLOAD.equals(menuStatus)) {
                    // 确认
                    // 先处理正在下载的任务
                    int columnIndex = TableColumnEnum.STATUS.getColumnIndex();
                    if (!DownloadStatusEnum.COMPLETED.get().equals(
                            table.getValueAt(i, columnIndex))) {
                        DownloadManager.cancelDownloadTask(i);
                    }


                }
            }

        }
    }


    public void setSelectedRows() {

        selectedRows = table.getSelectedRows();
        if (list.contains(name)) {
            if (table.getRowCount() < 1) {
                selectedRows = null;
            } else if (selectedRows.length < 1) {
                if (table.getSelectedRowCount() < 1) {
                    selectedRows = new int[table.getRowCount()];
                    for (int i = 0; i < table.getRowCount(); i++) {
                        selectedRows[i] = i;
                    }
                } else {
                    DialogUtil.showCustomDialog("错误", "非法操作");
                }

            }
        }
    }
}
