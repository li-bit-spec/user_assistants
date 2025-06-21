@echo off
chcp 65001 >nul
echo ====================================
echo   用户助手Spring Boot Starter安装器
echo ====================================
echo.

echo [1/3] 正在清理并编译项目...
cd user-assistant-spring-boot-starter
call mvn clean install -DskipTests -q

if %ERRORLEVEL% EQU 0 (
    echo ✓ 编译成功
) else (
    echo ✗ 编译失败，请检查Maven配置
    pause
    exit /b 1
)

echo.
echo [2/3] 正在安装到本地Maven仓库...
echo ✓ 已安装到: %USERPROFILE%\.m2\repository\com\module\user-assistant-spring-boot-starter\1.0.0\

echo.
echo [3/3] 安装完成！
echo.
echo ====================================
echo   使用方法
echo ====================================
echo.
echo 在您的Spring Boot项目的pom.xml中添加以下依赖：
echo.
echo ^<dependency^>
echo     ^<groupId^>com.module^</groupId^>
echo     ^<artifactId^>user-assistant-spring-boot-starter^</artifactId^>
echo     ^<version^>1.0.0^</version^>
echo ^</dependency^>
echo.
echo 然后在启动类上添加注解：
echo @EnableUserAssistant
echo.
echo 详细使用说明请参考：用户助手系统集成指南.md
echo.
echo ====================================
pause 