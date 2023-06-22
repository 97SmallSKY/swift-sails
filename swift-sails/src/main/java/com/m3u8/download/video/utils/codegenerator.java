package com.m3u8.download.video.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author Small_Tsk
 * @create 2022-11-20
 **/
public class codegenerator {
    public static void main(String[] args) {
String s = "*FC\\2PP|V-3098696 #63【無・個撮】媚〇で感度アップ生ハメSEX!?幼い顔に似合いすぎる制服コスで美マンにたっぷり中出し❤【複数特典あり】.mp4";
        System.out.println(s.replaceAll("\\\\", "")
                .replaceAll("/", "")
                .replaceAll(":", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\\|", "")
                .replaceAll("。", ""));
    }


    private static void create(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/download?serverTimezone=Asia/Shanghai", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("smallTSky") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.m3u8.download.video") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "F:\\IdeaProjects\\video\\src\\MainPage\\java\\com\\download\\video")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("detail") // 设置需要生成的表名
                            .addTablePrefix("", ""); // 设置过滤表前缀
                })

                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

}
