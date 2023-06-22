package com.m3u8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.mapper.DownloadSegmentMapper;
import com.m3u8.service.DownloadSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Small_Tsk
 * @create 2023-06-19
 **/
@Service
public class DownloadSegmentImpl implements DownloadSegmentService {

    @Autowired
    private DownloadSegmentMapper downloadSegmentMapper;


    @Override
    public int updateDownloadSegment(DownloadSegment downloadSegment) {
        return downloadSegmentMapper.updateById(downloadSegment);
    }

    @Override
    public int updateDownloadSegmentByUrl(DownloadSegment downloadSegment) {
        return 0;
    }

    @Override
    public List<DownloadSegment> getUnfinishedSegments(long downloadTaskId) {
        return downloadSegmentMapper.selectList(
                new QueryWrapper<DownloadSegment>().eq("is_valid", "1")
                        .eq("download_task_id", downloadTaskId)
                        .eq("is_valid", "1")
        );
    }

    @Override
    public int deleteSegment(long id) {

        return downloadSegmentMapper.deleteById(id);
    }

    @Override
    public int insertListSegment(List<DownloadSegment> downloadSegmentList) {

        return downloadSegmentMapper.insertList(downloadSegmentList);
    }

    @Override
    public List<DownloadSegment> selectListByDownloadTaskId(long taskId) {
        return downloadSegmentMapper.selectList(
                new QueryWrapper<DownloadSegment>()
                        .eq("download_task_id", taskId)
                        .eq("is_valid", "1")
        );
    }

    @Override
    public int selectCountCompleteByTaskId(Long taskId) {
        return downloadSegmentMapper.selectCount(
                new QueryWrapper<DownloadSegment>()
                        .eq("status", DownloadStatusEnum.COMPLETED.get())
                        .eq("download_task_id", taskId)
                        .eq("is_valid", "1")
        );
    }

    @Override
    public int selectCountUnCompleteByTaskId(Long taskId) {
        return downloadSegmentMapper.selectCount(
                new QueryWrapper<DownloadSegment>()
                        .ne("status", DownloadStatusEnum.COMPLETED.get())
                        .eq("download_task_id", taskId)
                        .eq("is_valid", "1")
        );
    }

    @Override
    public int selectCountByTaskId(Long taskId) {
        return downloadSegmentMapper.selectCount(
                new QueryWrapper<DownloadSegment>()
                        .eq("download_task_id", taskId)
                        .eq("is_valid", "1")
        );
    }

    @Override
    public int deleteSegmentListByDownloadId(List<Long> ids) {
        List<DownloadSegment> downloadSegmentList = downloadSegmentMapper.selectList(
                new QueryWrapper<DownloadSegment>().in("download_task_id", ids));
        return downloadSegmentMapper.deleteBatchIds(
                downloadSegmentList.stream()
                        .map(item -> item.getId())
                        .collect(Collectors.toList()));
    }
}
