# 🚀 User Assistant Spring Boot Starter 复制指南

## 📋 复制前准备

### 1. 确定目标位置
- 选择合适的目录位置
- 确保有足够的磁盘空间
- 检查目标目录权限

## 📁 复制步骤

### 方式一：完整复制（推荐）
```bash
# 复制整个starter目录
cp -r user-assistant-spring-boot-starter /目标路径/
```

### 方式二：选择性复制（精简）
```bash
# 只复制必要文件
mkdir /目标路径/user-assistant-spring-boot-starter
cp -r src/ /目标路径/user-assistant-spring-boot-starter/
cp pom.xml /目标路径/user-assistant-spring-boot-starter/
cp README*.md /目标路径/user-assistant-spring-boot-starter/
```

## ⚙️ 复制后必要操作

### 1. 清理构建产物
```bash
cd /目标路径/user-assistant-spring-boot-starter
rm -rf target/
rm -rf .idea/
rm -f *.iml
```

### 2. 修改Maven配置（如需要）
根据目标环境修改 `pom.xml`：

```xml
<!-- 可能需要修改的部分 -->
<groupId>com.yourcompany</groupId>
<artifactId>user-assistant-spring-boot-starter</artifactId>
<version>1.0.0-SNAPSHOT</version>

<!-- 修改仓库配置 -->
<distributionManagement>
    <repository>
        <id>your-nexus</id>
        <url>http://your-nexus-url/repository/maven-releases/</url>
    </repository>
</distributionManagement>
```

### 3. 重新构建项目
```bash
# 清理并安装到本地仓库
mvn clean install

# 或者发布到私有仓库
mvn clean deploy
```

## 🔧 环境适配

### Java版本检查
```bash
# 确保Java版本兼容（推荐JDK 8+）
java -version
javac -version
```

### Maven配置检查
```bash
# 检查Maven版本（推荐3.6+）
mvn -version

# 检查settings.xml配置
cat ~/.m2/settings.xml
```

## 📦 在新项目中使用

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.module</groupId>
    <artifactId>user-assistant-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用功能
```java
@SpringBootApplication
@EnableUserAssistant
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 配置参数
```yaml
user-assistant:
  upload:
    path: "/uploads"
    max-size: 10MB
  swagger:
    enabled: true
    title: "Your API"
```

## ⚠️ 注意事项

### 1. 数据库准备
确保目标环境有相应的数据库表：
- ass_manual_article
- ass_support_article  
- ass_user_feedback
- ass_feedback_image

### 2. 依赖冲突检查
检查是否与现有项目依赖冲突：
```bash
mvn dependency:tree
```

### 3. 端口和路径配置
确保以下配置不冲突：
- Web端口：默认8080
- 访问路径：/user-assistant/
- API路径：/api/*
- 上传路径：/uploads/*

## 🐛 常见问题

### 1. 编译错误
```bash
# 检查Java版本兼容性
mvn clean compile

# 更新依赖
mvn clean install -U
```

### 2. 静态资源404
- 检查 src/main/resources/static/ 目录
- 确认WebMvcConfig配置正确

### 3. Bean注入失败
- 检查@ComponentScan配置
- 确认数据源配置正确

## 📚 相关文档

- [用户助手系统集成指南.md](用户助手系统集成指南.md)
- [发布到Maven中央仓库指南.md](发布到Maven中央仓库指南.md)
- [私有仓库分发指南.md](私有仓库分发指南.md)

## 🎯 快速验证

复制完成后，快速验证：
```bash
# 1. 构建测试
mvn clean compile

# 2. 运行测试（如果有）
mvn test

# 3. 打包验证
mvn package

# 4. 本地安装
mvn install
```

验证成功后即可在其他项目中正常使用！ 