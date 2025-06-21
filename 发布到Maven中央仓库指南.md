# 发布到Maven中央仓库指南

## 概述

将用户助手Spring Boot Starter发布到Maven中央仓库，使其他开发者可以直接通过Maven/Gradle依赖使用。

## 准备工作

### 1. 注册Sonatype账号

1. 访问 [Sonatype JIRA](https://issues.sonatype.org/)
2. 注册账号
3. 创建一个Issue申请Group ID（如 `io.github.yourusername`）

### 2. 修改pom.xml

更新 `user-assistant-spring-boot-starter/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 修改Group ID为您的域名 -->
    <groupId>io.github.yourusername</groupId>
    <artifactId>user-assistant-spring-boot-starter</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>User Assistant Spring Boot Starter</name>
    <description>A Spring Boot Starter for User Assistant System with frontend and backend integration</description>
    <url>https://github.com/yourusername/user-assistant-spring-boot-starter</url>

    <!-- 许可证信息 -->
    <licenses>
        <license>
            <name>MIT License</name>
            <url>https://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
        </license>
    </licenses>

    <!-- 开发者信息 -->
    <developers>
        <developer>
            <id>yourusername</id>
            <name>Your Name</name>
            <email>your.email@example.com</email>
            <organization>Your Organization</organization>
            <organizationUrl>https://github.com/yourusername</organizationUrl>
        </developer>
    </developers>

    <!-- SCM信息 -->
    <scm>
        <connection>scm:git:git://github.com/yourusername/user-assistant-spring-boot-starter.git</connection>
        <developerConnection>scm:git:ssh://github.com:yourusername/user-assistant-spring-boot-starter.git</developerConnection>
        <url>https://github.com/yourusername/user-assistant-spring-boot-starter/tree/main</url>
    </scm>

    <!-- 分发管理 -->
    <distributionManagement>
        <snapshotRepository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
        <repository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
    </distributionManagement>

    <!-- 现有的dependencies和build配置... -->

    <build>
        <plugins>
            <!-- 现有插件... -->

            <!-- Nexus Staging Plugin -->
            <plugin>
                <groupId>org.sonatype.plugins</groupId>
                <artifactId>nexus-staging-maven-plugin</artifactId>
                <version>1.6.13</version>
                <extensions>true</extensions>
                <configuration>
                    <serverId>ossrh</serverId>
                    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
                    <autoReleaseAfterClose>true</autoReleaseAfterClose>
                </configuration>
            </plugin>

            <!-- GPG Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>3.0.1</version>
                <executions>
                    <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>sign</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. 配置Maven设置

在 `~/.m2/settings.xml` 中添加：

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>your-jira-username</username>
            <password>your-jira-password</password>
        </server>
    </servers>
</settings>
```

### 4. 生成GPG密钥

```bash
# 生成GPG密钥对
gpg --gen-key

# 列出密钥
gpg --list-secret-keys --keyid-format LONG

# 上传公钥到服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

## 发布步骤

### 1. 部署快照版本（可选）

```bash
mvn clean deploy
```

### 2. 发布正式版本

```bash
mvn clean deploy -P release
```

### 3. 发布后使用

其他项目可以直接使用：

```xml
<dependency>
    <groupId>io.github.yourusername</groupId>
    <artifactId>user-assistant-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
``` 