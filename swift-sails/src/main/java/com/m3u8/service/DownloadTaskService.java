package com.m3u8.service;

import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.mapper.DownloadTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 下载任务
 *
 * @author Small_Tsk
 * @create 2023-06-19
 **/
public interface DownloadTaskService {

    List<DownloadTask> selectDownloadTaskList();

    List<DownloadTask> selectUnCompleteDownloadTaskList();

    int updateDownloadTaskById(DownloadTask downloadTask);

    int updateDownloadTaskByUrl(DownloadTask downloadTask);

    int updateDownloadTaskListById(List<DownloadTask> downloadTaskList);

    int deleteDownloadTaskByUrl(String url);

    int insertDownloadTask(DownloadTask downloadTask);

    DownloadTask selectDownloadTaskByUrl(String url);
}
