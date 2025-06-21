package com.module.starter.annotation;

import com.module.starter.autoconfigure.UserAssistantAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用用户助手功能
 * 在主启动类上添加此注解即可启用用户助手模块
 * 
 * 示例：
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableUserAssistant
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(UserAssistantAutoConfiguration.class)
public @interface EnableUserAssistant {
    
    /**
     * 是否启用Swagger文档
     */
    boolean enableSwagger() default true;
    
    /**
     * 是否启用文件上传功能
     */
    boolean enableUpload() default true;
} 