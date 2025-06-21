# 🎯 Sonatype权限申请指南

## 📋 申请步骤

### 1. 注册JIRA账户
访问：[issues.sonatype.org](https://issues.sonatype.org)
- 点击"Sign up"注册账户
- 验证邮箱

### 2. 创建新的Issue
登录后，创建一个新的Issue：

**项目信息**：
- **Project**: Community Support - Open Source Project Repository Hosting (OSSRH)
- **Issue Type**: New Project
- **Summary**: Request publishing rights for io.github.li-bit-spec
- **Description**: 
  ```
  I would like to request publishing rights for the groupId io.github.li-bit-spec
  
  This is for my open source project: User Assistant Spring Boot Starter
  
  GitHub repository: https://github.com/li-bit-spec/user_assistants
  ```

**关键字段**：
- **Group Id**: `io.github.li-bit-spec`
- **Project URL**: `https://github.com/li-bit-spec/user_assistants`
- **SCM URL**: `https://github.com/li-bit-spec/user_assistants.git`

### 3. 验证GitHub所有权
Sonatype会要求你证明拥有GitHub账户：

1. 他们会给你一个JIRA issue号，比如 `OSSRH-12345`
2. 在你的GitHub账户中创建一个**公开仓库**，名为：`OSSRH-12345`
3. 在JIRA issue中回复确认已创建仓库

### 4. 等待审批
- 通常需要1-2个工作日
- Sonatype会在JIRA issue中回复
- 一旦批准，你就可以发布到该groupId

## 📝 申请模板

```
Subject: Request publishing rights for io.github.li-bit-spec

Hello,

I would like to request publishing rights for the groupId: io.github.li-bit-spec

Project Details:
- Project Name: User Assistant Spring Boot Starter
- Description: A Spring Boot starter that provides user assistant functionality including manual management, technical support, and user feedback features
- GitHub Repository: https://github.com/li-bit-spec/user_assistants
- License: Apache License 2.0

I am the owner of the GitHub account li-bit-spec and can verify ownership as required.

Thank you for your consideration.

Best regards,
[你的名字]
```

## ⏱️ 时间安排

- **申请提交**: 5分钟
- **GitHub验证**: 1分钟
- **等待审批**: 1-2个工作日
- **配置发布**: 完成后立即可用

## 📋 期间可以做的

在等待权限审批期间：

1. **继续使用本地安装**：
   ```bash
   mvn clean install
   ```

2. **准备发布脚本**：
   ```bash
   # 权限获得后使用
   mvn clean deploy -P release
   ```

3. **测试在其他项目中的集成**

## 🔗 相关链接

- [OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [GitHub命名空间指南](https://central.sonatype.org/publish/requirements/coordinates/#github-namespace)
- [Sonatype JIRA](https://issues.sonatype.org) 