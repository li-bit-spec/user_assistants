package com.example;

import com.module.starter.annotation.EnableUserAssistant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 示例应用启动类
 * 演示如何使用用户助手Starter
 */
@SpringBootApplication
@EnableUserAssistant  // 启用用户助手功能
public class ExampleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
        System.out.println("========================================");
        System.out.println("示例应用启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("Swagger文档：http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
} 