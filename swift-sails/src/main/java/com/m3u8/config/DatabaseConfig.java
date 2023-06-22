package com.m3u8.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m3u8.bean.po.Settings;
import com.m3u8.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

@Configuration
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SettingMapper settingMapper;

    @PostConstruct
    public void initDatabase() throws IOException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:schema/*.sql");
        populator.addScripts(resources);
        populator.execute(dataSource);


        Integer selectCount = settingMapper.selectCount(new QueryWrapper<>());
        if(selectCount > 0){
            return;
        }
        // 初始化数据
        String currentDir = Paths.get("").toAbsolutePath().toString();
        String defaultSaveFolder = currentDir + "/video";
        String defaultTempFolder = currentDir + "/temp";

        // 将defaultSaveFolder和defaultTempFolder插入到settings表中
        Settings settings = new Settings();
        settings.setDefaultSaveFolder(defaultSaveFolder);
        settings.setDefaultTempFolder(defaultTempFolder);
        settings.setDownloadCores(15);
        settings.setCreateTime(new Date());

        settingMapper.insert(settings);
    }
}
