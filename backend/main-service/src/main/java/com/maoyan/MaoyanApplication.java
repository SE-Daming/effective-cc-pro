package com.maoyan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 猫眼电影票务平台 - 主服务启动类
 *
 * @author maoyan
 */
@SpringBootApplication
@MapperScan("com.maoyan.mapper")
public class MaoyanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaoyanApplication.class, args);
    }

}
