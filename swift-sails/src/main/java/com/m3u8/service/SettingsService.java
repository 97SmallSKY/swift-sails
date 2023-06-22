package com.m3u8.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.Settings;
import com.m3u8.mapper.DownloadTaskMapper;
import com.m3u8.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 下载服务层
 *
 * @author Small_Tsk
 * @create 2023-06-19
 **/

public interface SettingsService {

    /**
     * 查询下载设置
     *
     * @return
     */
     Settings selectSetting();


    /**
     * 更新设置
     *
     * @param settings
     */
    void updateSetting(Settings settings);



}
