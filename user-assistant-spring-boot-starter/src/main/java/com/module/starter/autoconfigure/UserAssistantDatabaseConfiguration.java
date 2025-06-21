package com.module.starter.autoconfigure;

import com.module.starter.properties.UserAssistantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 用户助手数据库相关配置
 * 只有在配置了数据源时才启用数据库相关功能
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "user-assistant", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {
    "com.module.controller",
    "com.module.service",
    "com.module.config"
})
@RequiredArgsConstructor
public class UserAssistantDatabaseConfiguration {
    
    private final UserAssistantProperties properties;
} 