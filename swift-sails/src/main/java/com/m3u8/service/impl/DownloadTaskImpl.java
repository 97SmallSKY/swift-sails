package com.m3u8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.mapper.DownloadTaskMapper;
import com.m3u8.service.DownloadTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Small_Tsk
 * @create 2023-06-19
 **/
@Service
public class DownloadTaskImpl implements DownloadTaskService {

    @Autowired
    private DownloadTaskMapper downloadTaskMapper;


    @Override
    public List<DownloadTask> selectDownloadTaskList() {
        return downloadTaskMapper.selectList(
                new QueryWrapper<DownloadTask>().eq("is_valid", "1")
        );
    }

    @Override
    public List<DownloadTask> selectUnCompleteDownloadTaskList() {
        return downloadTaskMapper.selectList(
                new QueryWrapper<DownloadTask>()
                        .eq("is_valid", "1")
                        .ne("status", DownloadStatusEnum.COMPLETED.get())
        );
    }

    @Override
    public int updateDownloadTaskById(DownloadTask downloadTask) {
        return downloadTaskMapper.updateById(downloadTask);
    }

    @Override
    public int updateDownloadTaskByUrl(DownloadTask downloadTask) {

        return downloadTaskMapper.update(
                downloadTask, new UpdateWrapper<DownloadTask>()
                        .eq("is_valid", "1")
                        .eq("download_url", downloadTask.getDownloadUrl()));
    }

    @Override
    public int updateDownloadTaskListById(List<DownloadTask> downloadTaskList) {
        for (int i = 0; i < downloadTaskList.size(); i++) {
            downloadTaskMapper.updateById(downloadTaskList.get(i));
        }
        return downloadTaskList.size();
    }

    @Override
    public int deleteDownloadTaskByUrl(String url) {
        return downloadTaskMapper
                .delete(
                        new QueryWrapper<DownloadTask>().eq("download_url", url)
                );
    }

    @Override
    public int insertDownloadTask(DownloadTask downloadTask) {
        if (null == downloadTask && null == downloadTask.getDownloadUrl()) {
            return 0;
        }
        List<DownloadTask> downloadTasks = selectDownloadTaskList();
        boolean anyMatch = downloadTasks.stream()
                .anyMatch(
                        item -> downloadTask.getDownloadUrl().equals(item.getDownloadUrl())
                );
        if (anyMatch) {
            return 0;
        }
        downloadTaskMapper.insert(downloadTask);
        return 1;
    }

    @Override
    public DownloadTask selectDownloadTaskByUrl(String url) {
        return downloadTaskMapper.selectOne(
                new QueryWrapper<DownloadTask>()
                        .eq("download_url", url)
        );
    }
}
