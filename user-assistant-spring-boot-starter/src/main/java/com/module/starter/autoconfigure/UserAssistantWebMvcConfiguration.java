package com.module.starter.autoconfigure;

import com.module.starter.properties.UserAssistantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户助手WebMvc配置
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "user-assistant", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class UserAssistantWebMvcConfiguration implements WebMvcConfigurer {

    private final UserAssistantProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件上传静态资源映射
        registry.addResourceHandler(properties.getUpload().getUrlPrefix() + "/**")
                .addResourceLocations("file:" + properties.getUpload().getPath() + "/");
        
        // 前端静态资源映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
} 