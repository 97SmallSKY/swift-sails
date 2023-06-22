package com.m3u8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m3u8.bean.po.Settings;
import com.m3u8.mapper.SettingMapper;
import com.m3u8.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 下载服务层
 *
 * @author Small_Tsk
 * @create 2023-06-19
 **/
@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private SettingMapper settingMapper;

    public Settings selectSetting(){
        QueryWrapper queryWrapper = new QueryWrapper<>()
                .orderByDesc("create_time");
        queryWrapper.last("limit 1");
        Settings selectOne = settingMapper.selectOne(queryWrapper);
        return selectOne;
    }

    public void updateSetting(Settings settings){
        Settings selectSetting = selectSetting();
        settings.setId(selectSetting.getId());
        settingMapper.updateById(settings);
    }



}
