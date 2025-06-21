# 🚀 Maven中央仓库发布配置指南

## 📋 前提条件

### 1. 注册Sonatype账户
1. 访问 [central.sonatype.com](https://central.sonatype.com)
2. 注册账户并验证邮箱
3. 创建命名空间（namespace）

### 2. 域名验证
对于 `com.module` groupId，需要：
- 拥有域名 `module.com` 并添加DNS验证
- 或者使用GitHub命名空间：`io.github.li-bit-spec`

### 3. GPG密钥配置
```bash
# 生成GPG密钥
gpg --gen-key

# 查看密钥列表
gpg --list-secret-keys --keyid-format LONG

# 导出公钥到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

## ⚙️ Maven配置

### 1. 修改 ~/.m2/settings.xml
```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>your-sonatype-username</username>
            <password>your-sonatype-token</password>
        </server>
    </servers>
    
    <profiles>
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.executable>E:\gpg4\GnuPG\bin\gpg.exe</gpg.executable>
                <gpg.keyname>YOUR_GPG_KEY_ID</gpg.keyname>
                <gpg.passphrase>your-gpg-passphrase</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
    
    <activeProfiles>
        <activeProfile>gpg</activeProfile>
    </activeProfiles>
</settings>
```

### 2. 获取Sonatype Token
1. 登录 [central.sonatype.com](https://central.sonatype.com)
2. 点击右上角头像 → View Account
3. 生成User Token
4. 将用户名和密码配置到settings.xml

## 🏗️ 项目配置检查

### 1. 必需的POM元素
确保pom.xml包含：
- ✅ name
- ✅ description  
- ✅ url
- ✅ licenses
- ✅ developers
- ✅ scm

### 2. 建议的GroupId
由于域名验证问题，建议修改为：
```xml
<groupId>io.github.li-bit-spec</groupId>
<artifactId>user-assistant-spring-boot-starter</artifactId>
```

## 🚀 发布流程

### 1. 构建并验证
```bash
# 本地测试构建
mvn clean install

# 验证所有必需文件
mvn clean verify -P release
```

### 2. 发布到中央仓库
```bash
# 发布release版本
mvn clean deploy -P release

# 或使用central publishing插件
mvn clean deploy -P release -Dcentral.autoPublish=false
```

### 3. 版本管理
- 开发版本：`1.0.0-SNAPSHOT`
- 发布版本：`1.0.0`

## ⚠️ 注意事项

### 1. 版本要求
- Maven中央仓库不接受SNAPSHOT版本用于正式发布
- 确保版本号符合语义化版本规范

### 2. 文件要求
发布时必须包含：
- ✅ 主JAR文件
- ✅ 源码JAR文件（-sources.jar）
- ✅ Javadoc JAR文件（-javadoc.jar）
- ✅ GPG签名文件（.asc）

### 3. 质量检查
- 所有依赖必须已在中央仓库中
- POM文件必须有效
- 包含完整的元数据

## 🐛 常见问题

### 1. GPG签名失败
```bash
# 检查GPG配置
gpg --version
gpg --list-secret-keys

# 重新配置GPG路径
export GPG_TTY=$(tty)
```

### 2. 权限问题
- 确保Sonatype账户有该命名空间的发布权限
- 验证Token是否正确配置

### 3. 网络问题
- 使用代理时需要配置Maven代理设置
- 确保可以访问central.sonatype.com

## 🎯 快速验证脚本

```bash
#!/bin/bash
# 检查发布准备情况

echo "检查GPG配置..."
gpg --list-secret-keys

echo "检查Maven配置..."
mvn help:effective-settings

echo "验证项目..."
mvn clean verify -P release

echo "准备完成！可以执行发布命令：mvn clean deploy -P release"
```

## 📚 参考链接

- [Central Publishing Documentation](https://central.sonatype.org/publish/publish-guide/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/) 