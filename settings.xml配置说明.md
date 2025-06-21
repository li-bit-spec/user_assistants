# 🔧 Maven Settings.xml 配置说明

## 📍 当前状态
你的settings.xml文件位置：`C:\Users\李华宪\.m2\settings.xml`

## 🎯 需要修改的内容

当前你需要在settings.xml中将以下占位符替换为真实的值：

### 1. Sonatype OSSRH 认证
```xml
<server>
    <id>central</id>
    <username>YOUR_SONATYPE_USERNAME</username>  <!-- 替换为你的Sonatype用户名 -->
    <password>YOUR_SONATYPE_TOKEN</password>     <!-- 替换为你的Sonatype密码 -->
</server>
```

### 2. GPG签名配置
```xml
<profile>
    <id>gpg</id>
    <properties>
        <gpg.executable>E:\gpg4\GnuPG\bin\gpg.exe</gpg.executable>
        <gpg.keyname>YOUR_GPG_KEY_ID</gpg.keyname>           <!-- 替换为你的GPG密钥ID -->
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase> <!-- 替换为你的GPG密钥密码 -->
    </properties>
</profile>
```

## 🚀 获取认证信息

### 方法一：使用传统Sonatype OSSRH（推荐）
1. 访问 [issues.sonatype.org](https://issues.sonatype.org)
2. 注册账户
3. 创建一个JIRA issue申请groupId权限
4. 使用JIRA用户名和密码作为认证

### 方法二：使用新的Sonatype Central Portal
1. 访问 [central.sonatype.com](https://central.sonatype.com)  
2. 注册账户
3. 生成User Token
4. 使用用户名和Token作为认证

## 🔐 GPG密钥配置

### 1. 安装GPG
已检测到你的GPG路径：`E:\gpg4\GnuPG\bin\gpg.exe`

### 2. 生成密钥
```bash
# 生成新的GPG密钥对
gpg --gen-key

# 查看密钥列表
gpg --list-secret-keys --keyid-format LONG

# 示例输出：
# sec   rsa3072/ABC123DEF456 2023-01-01 [SC]
#       密钥ID就是: ABC123DEF456
```

### 3. 发布公钥
```bash
# 发布到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys ABC123DEF456
gpg --keyserver keys.openpgp.org --send-keys ABC123DEF456
```

## 📝 完整的settings.xml示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <servers>
        <server>
            <id>central</id>
            <username>your-jira-username</username>
            <password>your-jira-password</password>
        </server>
    </servers>
    
    <profiles>
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.executable>E:\gpg4\GnuPG\bin\gpg.exe</gpg.executable>
                <gpg.keyname>ABC123DEF456</gpg.keyname>
                <gpg.passphrase>your-gpg-passphrase</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
    
    <activeProfiles>
        <activeProfile>gpg</activeProfile>
    </activeProfiles>
    
</settings>
```

## 🧪 测试配置

配置完成后，测试：

```bash
# 验证构建
cd user-assistant-spring-boot-starter
mvn clean verify -P release

# 如果成功，尝试发布
mvn clean deploy -P release
```

## ⚠️ 重要提醒

1. **域名验证**：使用`com.module` groupId需要拥有`module.com`域名
2. **首次发布**：需要等待Sonatype审核和同步（可能需要几小时到几天）
3. **版本管理**：中央仓库不允许覆盖已发布的版本 