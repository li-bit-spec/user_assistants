# 后端目录结构与技术架构说明

## 总体结构

```
backend/
├── src/                # 源代码目录
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── module/          # 业务模块
│       │           ├── controller/  # 控制器
│       │           ├── service/     # 服务层
│       │           ├── mapper/      # MyBatis Mapper接口
│       │           ├── entity/      # 实体类
│       │           └── UserAssistantApplication.java  # 启动类
│       └── resources/              # 配置文件目录
│           ├── application*.yml    # 各环境配置文件
│           └── logback-spring.xml  # 日志配置
├── SQL/                # 数据库建表脚本
├── uploads/           # 文件上传目录
├── logs/             # 日志文件目录
└── pom.xml           # Maven配置文件
```

## 技术架构
- Spring Boot 2.x
- MyBatis Plus
- Redis
- Druid 数据库连接池
- Lombok
- Swagger2（API文档）
- Hutool、Fastjson

## 模块说明

### 业务模块 (com.module)
- **controller/**：控制器层，处理HTTP请求
- **service/**：业务逻辑层
  - impl/：服务实现类
- **mapper/**：MyBatis Mapper接口
- **entity/**：实体类

### 主要功能
- 手册文章管理
- 支持文章管理
- 用户反馈管理
- 文件上传

## 配置说明
- **application.yml**：基础配置
- **application-{env}.yml**：环境特定配置（sit/uat/pre/prod）
- **logback-spring.xml**：日志配置

## 说明
- 采用单模块Spring Boot应用架构
- 直接使用实体类进行数据传输，简化了DTO层
- 统一使用Map作为控制器返回类型，简化了响应封装
- 文件上传支持jpg、jpeg、png等格式 