# User Assistant Spring Boot Starter

## 📋 简介

User Assistant Spring Boot Starter 是一个开箱即用的用户助手系统组件，提供了完整的前后端功能，包括：

- 📖 **手册管理** - 支持创建、编辑、删除手册文档
- 💬 **支持文档** - 技术支持文档管理  
- 📝 **用户反馈** - 用户意见反馈收集
- 📸 **文件上传** - 图片等文件上传功能
- 📄 **API文档** - 自动生成Swagger文档
- 🎨 **前端UI** - 基于Vue 3 + Element Plus的现代化界面

## 🚀 快速开始

### 1. 添加依赖

在你的Spring Boot项目中添加以下依赖：

```xml
<dependency>
    <groupId>com.module</groupId>
    <artifactId>user-assistant-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 数据库驱动（必需） -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>

<!-- 连接池（推荐） -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.13</version>
</dependency>
```

### 2. 启用功能

在主启动类上添加 `@EnableUserAssistant` 注解：

```java
@SpringBootApplication
@EnableUserAssistant
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 配置数据库

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: your_username
    password: your_password

# 用户助手配置（可选）
user-assistant:
  enabled: true
  api-prefix: /api
  upload:
    path: ${user.dir}/uploads
    max-size: 2MB
```

### 4. 创建数据库表

执行以下SQL创建必要的数据库表：

```sql
-- 手册文章表
CREATE TABLE `ass_manual_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` longtext COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支持文章表
CREATE TABLE `ass_support_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` longtext COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户反馈表
CREATE TABLE `ass_user_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL COMMENT '反馈内容',
  `contact` varchar(255) COMMENT '联系方式',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 反馈图片表
CREATE TABLE `ass_feedback_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `feedback_id` bigint(20) NOT NULL COMMENT '反馈ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_feedback_id` (`feedback_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 5. 启动应用

启动你的Spring Boot应用，然后访问：

- **前端界面**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html

## ⚙️ 配置选项

### 完整配置示例

```yaml
user-assistant:
  # 基础配置
  enabled: true                    # 是否启用用户助手功能
  api-prefix: /api                 # API路径前缀
  table-prefix: ass_               # 数据库表前缀
  enable-logging: true             # 是否启用日志记录
  
  # 文件上传配置
  upload:
    path: ${user.dir}/uploads      # 文件上传目录
    url-prefix: /uploads           # URL访问前缀
    allowed-types: image/jpeg,image/png,image/gif  # 允许的文件类型
    max-size: 2MB                  # 单个文件最大大小
    max-request-size: 10MB         # 请求最大大小
  
  # Swagger文档配置
  swagger:
    enabled: true                  # 是否启用Swagger
    title: 用户助手API             # 文档标题
    description: 用户助手系统接口文档  # 文档描述
    version: 1.0.0                # API版本
    base-package: com.module       # 扫描的包路径
```

### 配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `user-assistant.enabled` | `true` | 是否启用用户助手功能 |
| `user-assistant.api-prefix` | `/api` | API接口路径前缀 |
| `user-assistant.table-prefix` | `ass_` | 数据库表名前缀 |
| `user-assistant.upload.path` | `${user.dir}/uploads` | 文件上传存储路径 |
| `user-assistant.upload.max-size` | `2MB` | 单个文件最大大小 |
| `user-assistant.swagger.enabled` | `true` | 是否启用Swagger文档 |

## 🔧 高级用法

### 自定义配置

如果你需要自定义某些功能，可以通过以下方式：

#### 1. 自定义文件上传处理

```java
@Component
public class CustomUploadHandler {
    
    @EventListener
    public void handleFileUpload(FileUploadEvent event) {
        // 自定义文件上传逻辑
        String fileName = event.getFileName();
        // 处理逻辑...
    }
}
```

#### 2. 自定义数据处理

```java
@Service
public class CustomDataService {
    
    @Autowired
    private AssManualArticleService manualService;
    
    public void customProcess() {
        // 自定义业务逻辑
    }
}
```

### 禁用某些功能

```yaml
user-assistant:
  swagger:
    enabled: false  # 禁用Swagger文档
  upload:
    max-size: 0     # 禁用文件上传
```

## 📡 API接口

启用Starter后，会自动提供以下REST API：

### 手册管理
- `GET /api/manual/list` - 获取手册列表
- `POST /api/manual/save` - 创建/更新手册
- `GET /api/manual/{id}` - 获取手册详情
- `DELETE /api/manual/{id}` - 删除手册

### 支持文档
- `GET /api/support/list` - 获取支持文档列表
- `POST /api/support/save` - 创建/更新支持文档
- `GET /api/support/{id}` - 获取支持文档详情
- `DELETE /api/support/{id}` - 删除支持文档

### 用户反馈
- `GET /api/feedback/list` - 获取反馈列表
- `POST /api/feedback/save` - 提交反馈
- `GET /api/feedback/{id}` - 获取反馈详情
- `DELETE /api/feedback/{id}` - 删除反馈

### 文件上传
- `POST /api/upload` - 上传文件

## 🏗️ 构建和发布

### 从源码构建

1. **克隆仓库**
```bash
git clone https://github.com/your-org/user-assistant-starter.git
cd user-assistant-starter
```

2. **构建Starter**
```bash
# Linux/macOS
chmod +x build-starter.sh
./build-starter.sh

# Windows
build-starter.bat
```

3. **安装到本地仓库**
```bash
cd user-assistant-spring-boot-starter
mvn install -DskipTests=true
```

### 发布到Maven仓库

1. **配置仓库信息**

修改 `user-assistant-spring-boot-starter/pom.xml` 中的 `distributionManagement` 配置：

```xml
<distributionManagement>
    <repository>
        <id>your-releases</id>
        <url>http://your-nexus-server/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>your-snapshots</id>
        <url>http://your-nexus-server/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

2. **配置Maven settings.xml**

```xml
<servers>
    <server>
        <id>your-releases</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
    <server>
        <id>your-snapshots</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

3. **执行发布**
```bash
mvn deploy -DskipTests=true
```

## 🔍 故障排除

### 常见问题

#### 1. 启动时提示找不到数据库表

**解决方案**: 确保已经执行了数据库建表SQL，并且数据库连接配置正确。

#### 2. 前端页面访问404

**解决方案**: 
- 检查静态资源是否正确打包到jar中
- 确认WebMvc配置是否生效
- 查看是否有路径冲突

#### 3. Swagger文档无法访问

**解决方案**:
- 检查 `user-assistant.swagger.enabled` 配置
- 确认Swagger依赖是否存在
- 查看控制台是否有相关错误日志

#### 4. 文件上传失败

**解决方案**:
- 检查上传目录权限
- 确认文件大小是否超过限制
- 检查磁盘空间是否充足

### 日志调试

启用调试日志：

```yaml
logging:
  level:
    com.module: DEBUG
    com.module.starter: TRACE
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

此项目基于 [MIT License](LICENSE) 开源。

## 📞 支持

如果你在使用过程中遇到问题：

1. 查看 [FAQ](#故障排除)
2. 搜索或创建 [Issue](https://github.com/your-org/user-assistant-starter/issues)
3. 联系技术支持

---

**享受编码！** 🎉 