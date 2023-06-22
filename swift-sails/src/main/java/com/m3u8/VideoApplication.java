package com.m3u8;

//import com.m3u8.download.video.gui.MainPage;
import com.m3u8.download.video.gui.UI.HomeDownloadJF;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(annotationClass = Mapper.class,basePackages = "com.m3u8.mapper")

public class VideoApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        // 创建一个Spring应用上下文
        ConfigurableApplicationContext context = SpringApplication.run(VideoApplication.class, args);

        // 在Spring应用程序上下文启动之后，创建Swing窗口
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // 从Spring应用程序上下文中获取窗口实例
                HomeDownloadJF homeDownloadJF = context.getBean(HomeDownloadJF.class);
                homeDownloadJF.setVisible(true);
            }
        });

        // 创建数据库

    }

}
