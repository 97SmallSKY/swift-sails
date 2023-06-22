package com.m3u8.download.video.gui.utils.tool;

import com.m3u8.bean.po.DownloadTask;
import com.m3u8.config.SpringUtil;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.service.DownloadSegmentService;
import com.m3u8.service.DownloadTaskService;

import java.util.List;

/**
 * @author Small_Tsk
 * @create 2023-06-20
 **/
public class PerformClosingOperation {


    private static DownloadTaskService downloadTaskService
            = SpringUtil.getBean(DownloadTaskService.class);

    private static DownloadSegmentService downloadSegmentService
            = SpringUtil.getBean(DownloadSegmentService.class);

    public static void SynchronizeDatabase() {
        // 执行关闭前的操作逻辑
        List<DownloadTask> downloadTasks = downloadTaskService.selectUnCompleteDownloadTaskList();
        for (int i = 0; i < downloadTasks.size(); i++) {
            DownloadTask downloadTask = downloadTasks.get(i);
            if (downloadTask.getStatus().equals(DownloadStatusEnum.COMPLETED.get())) {
                continue;
            }
            int completeCount = downloadSegmentService.selectCountCompleteByTaskId(downloadTask.getId());
            int allCount = downloadSegmentService.selectCountByTaskId(downloadTask.getId());
            downloadTask.setProgress(Math.floorDiv(completeCount, allCount) + "%");
        }

        downloadTaskService.updateDownloadTaskListById(downloadTasks);

    }
}
