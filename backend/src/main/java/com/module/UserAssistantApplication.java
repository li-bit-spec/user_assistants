package com.module;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户助手后端启动类
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.module")
@MapperScan("com.module.mapper")
public class UserAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAssistantApplication.class, args);
        log.info("用户助手admin服务启动成功。。。。。。");
    }
} 