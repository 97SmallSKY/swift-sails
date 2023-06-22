package com.m3u8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m3u8.bean.po.Settings;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Small_Tsk
 * @create 2023-06-17
 **/
@Mapper
public interface SettingMapper extends BaseMapper<Settings> {
}
