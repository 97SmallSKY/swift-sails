package com.m3u8.download.video.gui.UI.event;

import com.m3u8.config.SpringUtil;
import com.m3u8.download.video.gui.UI.SwingVlcjPlayer;
import com.m3u8.download.video.gui.utils.dialog.DialogUtil;
import com.m3u8.download.video.m3u8.download.DownloadManager;
import com.m3u8.download.video.m3u8.uiEnum.PopupMenuItemEnum;
import com.m3u8.download.video.m3u8.uiEnum.TableColumnEnum;
import com.m3u8.service.DownloadTaskService;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Small_Tsk
 * @create 2023-06-20
 **/
public class PopupMenuEvent implements ActionListener {

    private JMenuItem item;

    private JTable table;

    private DownloadTaskService downloadTaskService = SpringUtil.getBean(DownloadTaskService.class);

    public PopupMenuEvent(JMenuItem item, JTable table) {
        this.item = item;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executeMenuItem();
    }

    private void executeMenuItem()  {
        try {

            int[] selectRows = selectRows();
            if (null == selectRows) {
                return;
            }
            for (int selectRow : selectRows) {
                if (item.getText().equals(PopupMenuItemEnum.START.getChineseName())) {
                    // 开始
                    DownloadManager.resumeDownload(selectRow, table);
                } else if (item.getText().equals(PopupMenuItemEnum.PAUSED.getChineseName())) {
                    // 暂停下载
                    DownloadManager.pauseDownloadTask(selectRow);
                } else if (item.getText().equals(PopupMenuItemEnum.PLAY.getChineseName())) {
                    // 播放
                    String url = (String)table.getValueAt(selectRow, TableColumnEnum.LINK.getColumnIndex());
                    new SwingVlcjPlayer(url);
                } else if (item.getText().equals(PopupMenuItemEnum.OPEN_FOLDER.getChineseName())) {
                    // 打开文件所在位置
                    String link = (String) table.getValueAt(selectRow, TableColumnEnum.LINK.getColumnIndex());
                    String saveDirectory = downloadTaskService.selectDownloadTaskByUrl(link).getSaveDirectory();
                    Desktop.getDesktop().open(new File(saveDirectory));
                } else if (item.getText().equals(PopupMenuItemEnum.PLAY_WHEN_FINISHED.getChineseName())) {
                    // 完成时播放
                } else if (item.getText().equals(PopupMenuItemEnum.DELETE.getChineseName())) {
                    // 删除
                    DownloadManager.cancelDownloadTask(selectRow);
                } else if (item.getText().equals(PopupMenuItemEnum.COPY_LINK.getChineseName())) {
                    // 复制链接
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(
                            (String) table.getValueAt(selectRow, TableColumnEnum.LINK.getColumnIndex())
                    );
                    clipboard.setContents(selection, null);
                } else if (item.getText().equals(PopupMenuItemEnum.PERSONAL_DIRECTORY.getChineseName())) {
                    // 移动至个人目录
                } else if (item.getText().equals(PopupMenuItemEnum.OTHER_DIRECTORY.getChineseName())) {
                    // 其他目录
                } else if (item.getText().equals(PopupMenuItemEnum.TOP.getChineseName())) {
                    // 顶部
                    table.moveColumn(selectRow, 0);
                } else if (item.getText().equals(PopupMenuItemEnum.BOTTOM.getChineseName())) {
                    // 底部
                    table.moveColumn(selectRow, table.getRowCount() - 1);
                }
            }
        }catch (Exception e){
            DialogUtil.showCustomDialog("错误", e.getMessage(), new String[]{"确定"});
        }

    }

    private int[] selectRows() {
        int[] selectedRows = table.getSelectedRows();
        if (null == selectedRows) {
            // 表格操作
            DialogUtil.showCustomDialog("非法操作", "必须需要选择行",
                    new String[]{"确定"});
            return null;
        }
        return selectedRows;
    }

    public JMenuItem getItem() {
        return item;
    }

    public void setItem(JMenuItem item) {
        this.item = item;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }
}
