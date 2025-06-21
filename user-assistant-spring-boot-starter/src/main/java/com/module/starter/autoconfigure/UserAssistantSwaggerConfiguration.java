package com.module.starter.autoconfigure;

import com.module.starter.properties.UserAssistantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 用户助手Swagger配置
 */
@Configuration
@ConditionalOnClass({Docket.class, EnableSwagger2.class})
@ConditionalOnProperty(prefix = "user-assistant.swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@EnableSwagger2
public class UserAssistantSwaggerConfiguration {

    private final UserAssistantProperties properties;

    @Bean
    public Docket userAssistantApi() {
        UserAssistantProperties.Swagger swagger = properties.getSwagger();
        
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user-assistant")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swagger.getBasePackage()))
                .paths(PathSelectors.any())  // 扫描所有路径，不仅仅是/api/*
                .build();
    }

    private ApiInfo apiInfo() {
        UserAssistantProperties.Swagger swagger = properties.getSwagger();
        
        return new ApiInfoBuilder()
                .title(swagger.getTitle())
                .description(swagger.getDescription())
                .version(swagger.getVersion())
                .build();
    }
} 