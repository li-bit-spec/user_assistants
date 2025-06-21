package com.module.starter.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.module.starter.properties.UserAssistantProperties;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户助手MyBatis Plus配置
 */
@Configuration
@ConditionalOnClass({MybatisPlusInterceptor.class, DataSourceAutoConfiguration.class})
@ConditionalOnProperty(prefix = "user-assistant", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@MapperScan("com.module.mapper")
public class UserAssistantMyBatisPlusConfiguration {

    private final UserAssistantProperties properties;

    /**
     * 分页插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
} 