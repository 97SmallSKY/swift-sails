package com.m3u8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m3u8.bean.po.DownloadTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * mapper
 *
 * @author Small_Tsk
 * @create 2023-06-07
 **/

@Mapper
public interface DownloadTaskMapper extends BaseMapper<DownloadTask> {


    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")

    @Insert("INSERT INTO download_task (download_url, name, progress, " +
            "time_consuming, size, save_directory, temp_directory, " +
            "thread_count, m3u8_key, key_bytes, download_bytes, total_file_size, " +
            "is_bytes, status, suffix, create_time, is_valid) " +
            "VALUES (#{downloadUrl}, #{name}, #{progress}, " +
            "#{timeConsuming}, #{size}, #{saveDirectory}, #{tempDirectory}, " +
            "#{threadCount}, #{m3u8Key}, #{keyBytes}, #{downloadBytes}, #{totalFileSize}, " +
            "#{isBytes}, #{status}, #{suffix}, #{createTime}, #{isValid})")
    int insert(DownloadTask downloadTask);
}
