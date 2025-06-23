# 用户助手Spring Boot Starter完整集成文档

## 📖 项目简介

用户助手Spring Boot Starter是一个开箱即用的企业级用户助手系统，提供用户手册管理、技术支持文档、用户反馈收集等完整功能。通过一个注解即可在任何Spring Boot项目中快速集成完整的用户助手功能。

### 🌟 核心特性

- **🚀 开箱即用**：一个注解启用所有功能
- **🎨 现代化UI**：基于Vue3 + Element Plus的响应式前端界面
- **📝 富文本编辑**：支持Markdown和所见即所得编辑
- **📁 文件管理**：完整的文件上传和管理功能
- **📊 API文档**：自动生成Swagger API文档
- **🔧 高度可配置**：支持自定义配置和扩展
- **💾 多数据库支持**：支持MySQL、PostgreSQL、H2等
- **🛡️ 企业级**：包含安全配置、日志记录、异常处理

### 📦 Maven仓库信息

- **GroupId**: `io.github.li-bit-spec`
- **ArtifactId**: `user-assistant-spring-boot-starter`
- **最新版本**: `1.0.0`
- **仓库地址**: [Maven Central](https://search.maven.org/artifact/io.github.li-bit-spec/user-assistant-spring-boot-starter)

---

## 🚀 快速开始

### 1. 环境要求

- **Java**: 8+
- **Spring Boot**: 2.0+
- **Maven**: 3.6+
- **数据库**: MySQL 5.7+ / PostgreSQL 9.6+ / H2

### 2. 添加Maven依赖

在您的Spring Boot项目 `pom.xml` 中添加依赖：

```xml
<dependencies>
    <!-- 用户助手Starter -->
    <dependency>
        <groupId>io.github.li-bit-spec</groupId>
        <artifactId>user-assistant-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- 数据库驱动（根据需要选择） -->
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
</dependencies>
```

### 3. 启用用户助手功能

在主启动类添加 `@EnableUserAssistant` 注解：

```java
package com.yourcompany.yourproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.module.starter.annotation.EnableUserAssistant;

@SpringBootApplication
@EnableUserAssistant  // 启用用户助手功能
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
        System.out.println("应用启动成功！");
        System.out.println("用户助手前端: http://localhost:8080");
        System.out.println("API文档: http://localhost:8080/swagger-ui.html");
    }
}
```

### 4. 配置数据库连接

在 `application.yml` 中配置数据库：

```yaml
# 服务器配置
server:
  port: 8080

# 数据库配置
spring:
  datasource:
    # MySQL配置示例
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    
    # 连接池配置
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# 用户助手模块配置
user-assistant:
  enabled: true  # 启用用户助手功能
  api-prefix: /api  # API路径前缀
  
  # 文件上传配置
  upload:
    path: ${user.dir}/uploads  # 文件存储路径
    url-prefix: /uploads  # 访问URL前缀
    max-size: 10MB  # 最大文件大小
    allowed-types: image/jpeg,image/png,image/gif,application/pdf  # 允许的文件类型
    
  # Swagger API文档配置
  swagger:
    enabled: true  # 启用Swagger文档
    title: "用户助手API文档"
    description: "用户助手系统接口说明"
    version: "1.0.0"
    base-package: com.module.controller  # Controller扫描包路径
    contact:
      name: "技术支持"
      email: "support@yourcompany.com"
      url: "https://yourcompany.com"

# MyBatis Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      logic-delete-field: deleted  # 逻辑删除字段
      logic-delete-value: 1
      logic-not-delete-value: 0

# 日志配置
logging:
  level:
    root: INFO
    com.module: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 5. 创建数据库表

执行以下SQL创建必要的数据库表：

```sql
-- 用户手册表
CREATE TABLE manual_articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    content LONGTEXT NOT NULL COMMENT '文章内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_title (title),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户手册文章表';

-- 技术支持文章表
CREATE TABLE support_articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    content LONGTEXT NOT NULL COMMENT '文章内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_title (title),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术支持文章表';

-- 用户反馈表
CREATE TABLE user_feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content TEXT NOT NULL COMMENT '反馈内容',
    contact_info VARCHAR(255) COMMENT '联系方式',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- 反馈图片表
CREATE TABLE feedback_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_feedback_id (feedback_id),
    FOREIGN KEY (feedback_id) REFERENCES user_feedbacks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈图片表';
```

### 6. 启动应用

```bash
# 使用Maven启动
mvn spring-boot:run

# 或编译后启动
mvn clean package -DskipTests
java -jar target/your-application.jar
```

---

## 🌐 访问系统

启动成功后，您可以通过以下方式访问用户助手系统：

### 前端界面

- **主页**: [http://localhost:8080](http://localhost:8080)
- **用户手册**: http://localhost:8080/#/manual
- **技术支持**: http://localhost:8080/#/support
- **用户反馈**: http://localhost:8080/#/feedback

### API文档

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API文档JSON**: http://localhost:8080/v2/api-docs

### REST API端点

#### 用户手册管理
```http
# 获取文章列表
GET /api/manual/list

# 分页查询文章
POST /api/manual/page
Content-Type: application/json
{
  "pageNum": 1,
  "pageSize": 10,
  "title": "搜索关键词"
}

# 获取指定文章
GET /api/manual/{id}

# 添加文章
POST /api/manual/add
Content-Type: application/json
{
  "title": "文章标题",
  "content": "文章内容"
}

# 更新文章
POST /api/manual/update
Content-Type: application/json
{
  "id": 1,
  "title": "更新标题",
  "content": "更新内容"
}

# 删除文章
DELETE /api/manual/{id}
```

#### 技术支持管理
```http
# 获取支持文档列表
GET /api/support/list

# 分页查询支持文档
POST /api/support/page

# 其他操作类似手册管理...
```

#### 用户反馈管理
```http
# 获取反馈列表
GET /api/feedback/list

# 提交反馈
POST /api/feedback/add
Content-Type: application/json
{
  "content": "反馈内容",
  "contactInfo": "联系方式"
}

# 删除反馈
DELETE /api/feedback/{id}
```

#### 文件上传
```http
# 上传文件
POST /api/upload
Content-Type: multipart/form-data
file: [文件]
```

---

## ⚙️ 高级配置

### 自定义配置选项

```yaml
user-assistant:
  # 基础配置
  enabled: true  # 是否启用用户助手功能
  api-prefix: /api  # API路径前缀
  table-prefix: ""  # 数据库表前缀
  enable-logging: true  # 是否启用日志记录
  
  # 前端配置
  frontend:
    title: "我的用户助手系统"  # 系统标题
    logo: "/logo.png"  # 系统Logo
    theme: "default"  # 主题样式
    
  # 文件上传配置
  upload:
    path: /data/uploads  # 文件存储路径
    url-prefix: /uploads  # URL访问前缀
    max-size: 20MB  # 最大文件大小
    max-request-size: 50MB  # 最大请求大小
    allowed-types: 
      - image/jpeg
      - image/png
      - image/gif
      - application/pdf
      - application/msword
    storage-type: local  # 存储类型: local|oss|cos
    
  # 安全配置
  security:
    enable-csrf: false  # 是否启用CSRF保护
    cors-allowed-origins: "*"  # 允许的跨域源
    rate-limit: 1000  # 请求频率限制（每分钟）
    
  # 缓存配置
  cache:
    enabled: true  # 是否启用缓存
    ttl: 3600  # 缓存TTL（秒）
    max-size: 1000  # 最大缓存条目数
    
  # Swagger配置
  swagger:
    enabled: true
    title: "我的API文档"
    description: "项目API接口文档"
    version: "2.0.0"
    base-package: com.yourcompany.controller
    contact:
      name: "技术支持"
      email: "support@yourcompany.com"
      url: "https://yourcompany.com"
    license:
      name: "Apache 2.0"
      url: "https://www.apache.org/licenses/LICENSE-2.0"
```

### 多数据库配置示例

#### PostgreSQL配置
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

#### H2内存数据库配置（测试用）
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true  # 启用H2控制台
      path: /h2-console
```

### 生产环境配置示例

```yaml
# application-prod.yml
server:
  port: 8080
  
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/user_assistant_db?useSSL=true&serverTimezone=UTC
    username: ${DB_USERNAME:prod_user}
    password: ${DB_PASSWORD:prod_password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 10
      max-active: 100
      min-idle: 10
      max-wait: 60000
      
user-assistant:
  upload:
    path: /data/user-assistant/uploads
    max-size: 50MB
  security:
    enable-csrf: true
    cors-allowed-origins: "https://yourcompany.com"
    rate-limit: 500
  swagger:
    enabled: false  # 生产环境关闭Swagger

logging:
  level:
    root: WARN
    com.module: INFO
  file:
    name: /var/log/user-assistant/application.log
```

---

## 🔧 自定义扩展

### 扩展Controller

如果需要添加自定义功能，可以创建自己的Controller：

```java
@RestController
@RequestMapping("/api/custom")
@Api(tags = "自定义接口")
public class CustomController {
    
    @Autowired
    private AssManualArticleService manualService;
    
    @Autowired
    private AssSupportArticleService supportService;
    
    @GetMapping("/stats")
    @ApiOperation("获取统计信息")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("manualCount", manualService.count());
        stats.put("supportCount", supportService.count());
        stats.put("totalViews", getTotalViews());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("用户助手系统运行正常");
    }
    
    private Long getTotalViews() {
        // 自定义统计逻辑
        return 0L;
    }
}
```

### 自定义配置类

```java
@Configuration
@ConditionalOnProperty(prefix = "user-assistant", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(UserAssistantProperties.class)
public class CustomUserAssistantConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService() {
        return new CustomServiceImpl();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "user-assistant.cache", name = "enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("userAssistant");
    }
    
    @Bean
    @ConditionalOnWebApplication
    public WebMvcConfigurer customWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new CustomInterceptor())
                       .addPathPatterns("/api/**");
            }
        };
    }
}
```

### 自定义事件监听

```java
@Component
public class UserAssistantEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(UserAssistantEventListener.class);
    
    @EventListener
    public void handleArticleCreated(ArticleCreatedEvent event) {
        logger.info("新文章创建: {}", event.getTitle());
        // 自定义处理逻辑，如发送通知、更新缓存等
    }
    
    @EventListener
    public void handleFeedbackSubmitted(FeedbackSubmittedEvent event) {
        logger.info("收到新反馈: {}", event.getContent());
        // 自定义处理逻辑，如发送邮件通知等
    }
}
```

---

## 🛠️ 故障排除

### 常见问题及解决方案

#### 1. 应用启动失败

**问题**: `Failed to configure a DataSource`
```
Description:
Failed to configure a DataSource: 'url' attribute is not specified
```

**解决方案**: 检查数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database  # 确保URL正确
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 2. 前端页面无法访问

**问题**: 访问 `http://localhost:8080` 显示404

**可能原因**:
- 没有添加 `@EnableUserAssistant` 注解
- 静态资源映射被覆盖
- 端口被其他应用占用

**解决方案**:
```java
// 确保主启动类有正确注解
@SpringBootApplication
@EnableUserAssistant  // 必须添加此注解
public class YourApplication {
    // ...
}
```

#### 3. API调用失败

**问题**: API返回 `Whitelabel Error Page` 或 404

**排查步骤**:
1. 检查控制台日志查看具体错误
2. 确认数据库表是否创建
3. 验证API路径是否正确

**常见错误**:
```
# 错误的API路径
GET /api/manual/articles  # ❌

# 正确的API路径  
GET /api/manual/list      # ✅
```

#### 4. 数据库连接问题

**问题**: `Unknown column 'created_at' in 'field list'`

**解决方案**: 确保数据库表结构与实体类字段匹配
```sql
-- 检查表结构
DESCRIBE manual_articles;

-- 如果字段名不匹配，修改表结构或实体类映射
```

#### 5. 文件上传失败

**问题**: 文件上传时报错 `Maximum upload size exceeded`

**解决方案**: 调整文件上传大小限制
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB      # 单个文件大小限制
      max-request-size: 10MB   # 请求总大小限制

user-assistant:
  upload:
    max-size: 10MB  # 用户助手模块文件大小限制
```

#### 6. Swagger无法访问

**问题**: 访问 `/swagger-ui.html` 显示404

**解决方案**:
```yaml
user-assistant:
  swagger:
    enabled: true  # 确保启用Swagger
```

### 调试技巧

#### 1. 启用调试日志
```yaml
logging:
  level:
    com.module: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
```

#### 2. 检查自动配置
```java
// 在主启动类添加，查看自动配置信息
@SpringBootApplication
@EnableUserAssistant
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(YourApplication.class);
        app.setLogStartupInfo(true);
        app.run(args);
    }
}
```

#### 3. 验证Bean加载
```java
@RestController
public class DebugController {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @GetMapping("/debug/beans")
    public Map<String, Object> getBeans() {
        Map<String, Object> result = new HashMap<>();
        result.put("userAssistantBeans", 
            Arrays.stream(applicationContext.getBeanDefinitionNames())
                  .filter(name -> name.contains("userAssistant") || name.contains("module"))
                  .collect(Collectors.toList()));
        return result;
    }
}
```

---

## 📈 性能优化

### 1. 数据库优化

```sql
-- 添加索引优化查询性能
CREATE INDEX idx_title ON manual_articles(title);
CREATE INDEX idx_created_at ON manual_articles(created_at);
CREATE INDEX idx_content_fulltext ON manual_articles(content) USING FULLTEXT;
```

### 2. 缓存配置

```yaml
user-assistant:
  cache:
    enabled: true
    ttl: 3600  # 1小时
    max-size: 1000
```

```java
@Service
@CacheConfig(cacheNames = "userAssistant")
public class CachedManualService {
    
    @Cacheable(key = "'list'")
    public List<AssManualArticle> listAll() {
        // 实现缓存
    }
    
    @CacheEvict(allEntries = true)
    public void clearCache() {
        // 清除缓存
    }
}
```

### 3. 数据库连接池优化

```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      max-active: 100
      min-idle: 10
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
```

---

## 🔒 安全配置

### 1. 基础安全配置

```java
@Configuration
@EnableWebSecurity
public class UserAssistantSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/feedback/**").permitAll()  // 反馈接口允许匿名访问
                .antMatchers("/api/manual/list").permitAll()   // 手册列表允许匿名访问
                .antMatchers("/api/admin/**").hasRole("ADMIN") // 管理接口需要管理员权限
                .anyRequest().authenticated()
            .and()
            .csrf().disable()  // 根据需要启用CSRF
            .cors();  // 启用CORS
    }
}
```

### 2. API接口权限控制

```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @DeleteMapping("/manual/{id}")
    @PreAuthorize("hasAuthority('DELETE_MANUAL')")
    public ResponseEntity<?> deleteManual(@PathVariable Long id) {
        // 管理员删除手册
    }
}
```

### 3. 文件上传安全

```yaml
user-assistant:
  upload:
    allowed-types: 
      - image/jpeg
      - image/png
      - image/gif
    max-size: 5MB
    scan-virus: true  # 启用病毒扫描（需要自定义实现）
    validate-content: true  # 验证文件内容
```

---

## 📊 监控和运维

### 1. 健康检查

```java
@Component
public class UserAssistantHealthIndicator implements HealthIndicator {
    
    @Autowired
    private AssManualArticleService manualService;
    
    @Override
    public Health health() {
        try {
            long count = manualService.count();
            return Health.up()
                    .withDetail("manual.count", count)
                    .withDetail("status", "运行正常")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

### 2. 指标监控

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### 3. 日志配置

```yaml
logging:
  level:
    com.module: INFO
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/user-assistant.log
    max-size: 100MB
    max-history: 30
```

---

## 🚀 部署指南

### 1. 生产环境部署

#### Docker部署
```dockerfile
FROM openjdk:8-jre-alpine

COPY target/your-application.jar app.jar
COPY uploads /app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_USERNAME=root
      - DB_PASSWORD=password
    depends_on:
      - mysql
    volumes:
      - ./uploads:/app/uploads
      
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: user_assistant_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      
volumes:
  mysql_data:
```

#### Kubernetes部署
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-assistant-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-assistant
  template:
    metadata:
      labels:
        app: user-assistant
    spec:
      containers:
      - name: app
        image: your-registry/user-assistant:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: "mysql-service"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
---
apiVersion: v1
kind: Service
metadata:
  name: user-assistant-service
spec:
  selector:
    app: user-assistant
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

### 2. CI/CD流水线

```yaml
# .github/workflows/deploy.yml
name: Deploy User Assistant

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 8
      uses: actions/setup-java@v2
      with:
        java-version: '8'
        distribution: 'adopt'
        
    - name: Build with Maven
      run: mvn clean package -DskipTests
      
    - name: Build Docker image
      run: docker build -t user-assistant:${{ github.sha }} .
      
    - name: Deploy to production
      run: |
        docker tag user-assistant:${{ github.sha }} your-registry/user-assistant:latest
        docker push your-registry/user-assistant:latest
        kubectl set image deployment/user-assistant-app app=your-registry/user-assistant:latest
```

---

## 📞 技术支持

### 获取帮助

1. **查看文档**: [GitHub项目文档](https://github.com/li-bit-spec/user_assistants)
2. **提交Issue**: [GitHub Issues](https://github.com/li-bit-spec/user_assistants/issues)
3. **联系开发者**: 18330982692@163.com

### 社区资源

- **GitHub仓库**: https://github.com/li-bit-spec/user_assistants
- **Maven中央仓库**: https://search.maven.org/artifact/io.github.li-bit-spec/user-assistant-spring-boot-starter
- **示例项目**: https://github.com/li-bit-spec/user_assistants/tree/main/example-app

### 贡献指南

欢迎提交PR和Issue！请查看 [CONTRIBUTING.md](https://github.com/li-bit-spec/user_assistants/blob/main/CONTRIBUTING.md) 了解贡献指南。

---

## 📄 许可证

本项目采用 Apache License 2.0 许可证，详情请查看 [LICENSE](https://github.com/li-bit-spec/user_assistants/blob/main/LICENSE) 文件。

---

## 🎉 结语

用户助手Spring Boot Starter为您提供了一个完整、现代化的用户助手解决方案。通过一个简单的注解，您就可以为任何Spring Boot项目添加强大的用户助手功能。

如果您在使用过程中遇到任何问题或有改进建议，欢迎随时联系我们！

**立即体验**: 添加依赖 → 加注解 → 启动应用 → 访问 `http://localhost:8080` 🚀 