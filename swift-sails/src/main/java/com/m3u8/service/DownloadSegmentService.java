package com.m3u8.service;

import com.m3u8.bean.po.DownloadSegment;

import java.util.List;

/**
 * 片段
 *
 * @author Small_Tsk
 * @create 2023-06-19
 **/
public interface DownloadSegmentService {

    int updateDownloadSegment(com.m3u8.bean.po.DownloadSegment downloadSegment);

    int updateDownloadSegmentByUrl(com.m3u8.bean.po.DownloadSegment downloadSegment);

    List<DownloadSegment> getUnfinishedSegments(long downloadTaskId);

    int deleteSegment(long id);

    int insertListSegment(List<DownloadSegment> downloadSegmentList);

    List<DownloadSegment> selectListByDownloadTaskId(long taskId);

    /**
     * 查询已完成的数量
     *
     * @param taskId
     * @return
     */
    int selectCountCompleteByTaskId(Long taskId);

    /**
     * 查询未完成的数量
     *
     * @param taskId
     * @return
     */
    int selectCountUnCompleteByTaskId(Long taskId);

    int selectCountByTaskId(Long taskId);


    int deleteSegmentListByDownloadId(List<Long> ids);
}
