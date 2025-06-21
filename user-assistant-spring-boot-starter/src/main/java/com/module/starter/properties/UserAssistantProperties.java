package com.module.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用户助手配置属性
 */
@Data
@ConfigurationProperties(prefix = "user-assistant")
public class UserAssistantProperties {

    /**
     * 是否启用用户助手功能
     */
    private boolean enabled = true;

    /**
     * API路径前缀
     */
    private String apiPrefix = "/api";

    /**
     * 文件上传配置
     */
    private Upload upload = new Upload();

    /**
     * Swagger配置
     */
    private Swagger swagger = new Swagger();

    /**
     * 数据库表前缀
     */
    private String tablePrefix = "ass_";

    /**
     * 是否启用日志记录
     */
    private boolean enableLogging = true;

    /**
     * 文件上传配置
     */
    @Data
    public static class Upload {
        /**
         * 上传路径
         */
        private String path = "${user.dir}/uploads";

        /**
         * URL前缀
         */
        private String urlPrefix = "/uploads";

        /**
         * 允许的文件类型
         */
        private String allowedTypes = "image/jpeg,image/png,image/gif";

        /**
         * 最大文件大小
         */
        private String maxSize = "2MB";

        /**
         * 最大请求大小
         */
        private String maxRequestSize = "10MB";
    }

    /**
     * Swagger配置
     */
    @Data
    public static class Swagger {
        /**
         * 是否启用Swagger
         */
        private boolean enabled = true;

        /**
         * 标题
         */
        private String title = "用户助手API";

        /**
         * 描述
         */
        private String description = "用户助手系统接口文档";

        /**
         * 版本
         */
        private String version = "1.0.0";

        /**
         * 扫描包路径
         */
        private String basePackage = "com.module";
    }
} 