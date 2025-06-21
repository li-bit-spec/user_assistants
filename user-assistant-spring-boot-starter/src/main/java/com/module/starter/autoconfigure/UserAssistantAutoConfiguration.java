package com.module.starter.autoconfigure;

import com.module.starter.properties.UserAssistantProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * 用户助手自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UserAssistantProperties.class)
@ConditionalOnProperty(prefix = "user-assistant", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = {
    "com.module.controller",
    "com.module.service",
    "com.module.config"
})
@Import({
    UserAssistantWebMvcConfiguration.class,
    UserAssistantSwaggerConfiguration.class,
    UserAssistantMyBatisPlusConfiguration.class,
    UserAssistantDatabaseConfiguration.class
})
public class UserAssistantAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("用户助手模块自动配置已启用");
        log.info("UserAssistant Auto Configuration Enabled");
        log.info("========================================");
    }
} 