package com.m3u8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.DownloadTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * @author Small_Tsk
 * @create 2023-06-07
 **/
@Mapper
public interface DownloadSegmentMapper extends BaseMapper<DownloadSegment> {


    @Insert("INSERT INTO download_segment (download_task_id, segment_number, segment_name, segment_path, is_valid) " +
            "VALUES " +
            "(#{segment.downloadTaskId}, #{segment.segmentNumber}," +
            " #{segment.segmentName}, #{segment.segmentPath}, #{segment.isValid})")

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DownloadTask downloadTask);

    @Insert("<script> " +
            "INSERT INTO download_segment (download_task_id, segment_number, segment_name, segment_path, status, save_directory, is_valid) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','> " +
            "(#{item.downloadTaskId}, #{item.segmentNumber}, #{item.segmentName}, #{item.segmentPath}, #{item.status}, #{item.saveDirectory}, #{item.isValid}) " +
            "</foreach> " +
            "</script>")
    int insertList(List<DownloadSegment> downloadSegmentList);


}
