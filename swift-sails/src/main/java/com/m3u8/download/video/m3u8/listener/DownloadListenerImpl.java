package com.m3u8.download.video.m3u8.listener;

import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.config.SpringUtil;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.download.video.m3u8.uiEnum.TableColumnEnum;
import com.m3u8.service.DownloadTaskService;

import javax.swing.table.DefaultTableModel;

/**
 * @author Small_Tsk
 * @create 2023-06-14
 **/
public class DownloadListenerImpl implements DownloadListener {

    private DefaultTableModel model;

    private int rowIndex;

//    @Autowired
//    private DownloadTaskMapper downloadTaskMapper;

    private DownloadTask downloadTask;


//    @Autowired
//    private DownloadSegmentMapper downloadSegmentMapper;

    private DownloadSegment downloadSegment;

    private DownloadTaskService downloadTaskService = SpringUtil.getBean(DownloadTaskService.class);





    @Override
    public void start(DownloadTask downloadTask) {
        model.setValueAt(DownloadStatusEnum.DOWNLOADING.get(), rowIndex, 5);
        model.fireTableDataChanged();
        downloadTask.setStatus(DownloadStatusEnum.DOWNLOADING.get());
//        downloadTaskMapper.updateById(downloadTask);

    }

    @Override
    public void percent(double percent) {
        String aValue = percent + "%";
        model.setValueAt(aValue, rowIndex, 3);
//        // 通知表格模型的监听器数据已经改变，需要更新UI
        model.fireTableDataChanged();
        downloadTask.setProgress(aValue);
        if(!aValue.equals(downloadTask.getProgress())){
            downloadTask.setProgress(aValue);
            downloadTaskService.updateDownloadTaskByUrl(downloadTask);
        }
    }

    @Override
    public void downloadedFile(String downloadedFile) {

    }

    @Override
    public void fileSize(String fileSize) {
        model.setValueAt(fileSize, rowIndex, 6);
        model.fireTableDataChanged();
//        downloadTaskMapper.updateById(downloadTask);
    }

    @Override
    public void process(String downloadUrl, int finished, int sum, double percent) {
//        downloadTaskMapper.updateById(downloadTask);

    }

    @Override
    public void speed(String speedPerSecond) {
        model.setValueAt(speedPerSecond, rowIndex, 4);
        model.fireTableDataChanged();
        System.out.println(rowIndex + " -- 速度 ---- " + speedPerSecond );
    }

    @Override
    public void timeConsuming(String timeConsuming) {
        model.setValueAt(timeConsuming, rowIndex, 8);
        model.fireTableDataChanged();
        downloadTask.setTimeConsuming(timeConsuming);
//        downloadTaskMapper.updateById(downloadTask);
    }

    @Override
    public void expectedCompletionTime(String completionTime) {
        model.setValueAt(completionTime, rowIndex, 7);
        model.fireTableDataChanged();

    }

    @Override
    public void mergeAndDelete() {
        model.setValueAt("正在合并文件，删除临时文件", rowIndex, 5);
    }


    @Override
    public void end() {
        for (int i = 3; i < model.getRowCount(); i++) {
            model.setValueAt("", rowIndex, 3);
        }
        model.setValueAt(DownloadStatusEnum.COMPLETED.get(), rowIndex, 5);
        model.fireTableDataChanged();
        downloadTask.setStatus(DownloadStatusEnum.COMPLETED.get());
//        downloadTaskMapper.updateById(downloadTask);
        // todo 显示连接失败的 url，选择是否重新尝试 connectFailUrl

        downloadTask.setProgress("100%");
        downloadTaskService.updateDownloadTaskByUrl(downloadTask);


    }

    @Override
    public void connectionFailed() {
        model.setValueAt("连接失败，请检查网络是否设置正确",
                rowIndex, TableColumnEnum.STATUS.getColumnIndex());
        downloadTask.setStatus(DownloadStatusEnum.CONNECTING_FAIL.get());
//        downloadTaskMapper.updateById(downloadTask);
    }

    @Override
    public void pause() {
        for (int i = 3; i < model.getRowCount(); i++) {
            model.setValueAt("", rowIndex, TableColumnEnum.PROGRESS.getColumnIndex());
        }
        model.setValueAt(DownloadStatusEnum.PAUSED.get(), rowIndex, 5);
        model.fireTableDataChanged();
        downloadTask.setStatus(DownloadStatusEnum.PAUSED.get());
        String progress = (String) model.getValueAt(rowIndex, TableColumnEnum.PROGRESS.getColumnIndex());
        downloadTask.setProgress(progress);
        downloadTaskService.updateDownloadTaskByUrl(downloadTask);
//        downloadTaskMapper.updateById(downloadTask);
    }


    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public DownloadSegment getDownloadSegment() {
        return downloadSegment;
    }

    public void setDownloadSegment(DownloadSegment downloadSegment) {
        this.downloadSegment = downloadSegment;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
