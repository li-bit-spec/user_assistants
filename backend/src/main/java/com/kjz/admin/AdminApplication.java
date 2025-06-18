package com.kjz.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * B端启动类
 *
 * @author zhangcheng
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.kjz")
@MapperScan("com.kjz.admin.module.dao")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        log.info("用户助手admin服务启动成功。。。。。。");
    }
}
