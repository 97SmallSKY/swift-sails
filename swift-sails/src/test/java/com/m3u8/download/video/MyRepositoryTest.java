package com.m3u8.download.video;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.mapper.DownloadTaskMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyRepositoryTest {
    @Autowired
    public DownloadTaskMapper downloadTaskMapper;
    QueryWrapper<DownloadTask> queryWrapper = new QueryWrapper<>();

    @Test
    @Order(1)
    public void testInsertAndUpdate() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setName("2023-6-17");
        downloadTask.setDownloadUrl("https://theporn.cc/video/m3u8/eab2b9452d04378a74e2032031d1d426ed178d86.m3u8?video_server=b2server&hash=6c43efad5");
        downloadTask.setIsValid("1");
        downloadTask.setStatus("已完成");
        downloadTask.setCreateTime(new Date());
        downloadTask.setSuffix(".mp4");
        downloadTask.setSaveDirectory("f://M3U8");
        downloadTask.setTempDirectory(System.getProperty("user.dir"));
        downloadTask.setSize("1111");
        int insert = downloadTaskMapper.insert(downloadTask);
        Assert.assertEquals(1L, insert);
        Assert.assertNotNull(downloadTask.getId());

        Date date = new Date();
        downloadTask.setCreateTime(date);
        long updatedCount = downloadTaskMapper.updateById(downloadTask);
        Assert.assertEquals(1l,updatedCount);

    }

    @Test
    @Order(2)
    public void testUpdate(){

    }



    @Test
    @Order(3)
    public void testSelect(){
        queryWrapper.clear();
       long count = downloadTaskMapper.selectCount(queryWrapper);
       Assert.assertEquals(1l, count);
    }

    @Test
    @Order(4)
    public void testDelete(){
        queryWrapper.clear();

        downloadTaskMapper.delete(queryWrapper);

        long value = downloadTaskMapper.selectCount(queryWrapper);
        Assert.assertEquals(0l,value);
    }

    @Test
    public void dropTable(){
//        downloadTaskMapper.
    }
}
