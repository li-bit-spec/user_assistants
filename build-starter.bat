@echo off
chcp 65001 >nul
echo === 构建并发布 User Assistant Spring Boot Starter ===

setlocal enabledelayedexpansion

rem 获取项目根目录
set PROJECT_ROOT=%~dp0
set PROJECT_ROOT=%PROJECT_ROOT:~0,-1%
set STARTER_DIR=%PROJECT_ROOT%\user-assistant-spring-boot-starter
set EXAMPLE_DIR=%PROJECT_ROOT%\example-app

echo 项目根目录: %PROJECT_ROOT%

rem 1. 构建前端资源
echo 🏗️ 构建前端资源...
cd /d "%PROJECT_ROOT%\frontend"

if not exist "node_modules" (
    echo 📦 安装前端依赖...
    call npm install
    if errorlevel 1 (
        echo ❌ 前端依赖安装失败
        pause
        exit /b 1
    )
)

echo 🔨 构建前端项目...
call npm run build
if errorlevel 1 (
    echo ❌ 前端构建失败
    pause
    exit /b 1
)

if not exist "dist" (
    echo ❌ 前端构建失败，dist目录不存在
    pause
    exit /b 1
)

rem 2. 复制前端静态资源到Starter
echo 📋 复制前端资源到Starter...
set STATIC_DIR=%STARTER_DIR%\src\main\resources\static
if not exist "%STATIC_DIR%" mkdir "%STATIC_DIR%"

rem 清理旧资源
if exist "%STATIC_DIR%\*" del /q "%STATIC_DIR%\*"
if exist "%STATIC_DIR%" for /d %%i in ("%STATIC_DIR%\*") do rmdir /s /q "%%i"

rem 复制新资源
xcopy "%PROJECT_ROOT%\frontend\dist\*" "%STATIC_DIR%\" /s /e /y
if errorlevel 1 (
    echo ❌ 前端资源复制失败
    pause
    exit /b 1
)

echo ✅ 前端资源复制完成

rem 3. 构建Starter
echo 🏗️ 构建User Assistant Starter...
cd /d "%STARTER_DIR%"

rem 清理之前的构建
echo 🧹 清理之前的构建...
call mvn clean
if errorlevel 1 (
    echo ❌ Maven清理失败
    pause
    exit /b 1
)

rem 编译和打包
echo 📦 Maven打包...
call mvn package -DskipTests=true
if errorlevel 1 (
    echo ❌ Maven打包失败
    pause
    exit /b 1
)

rem 安装到本地仓库
echo 🔧 安装到本地Maven仓库...
call mvn install -DskipTests=true
if errorlevel 1 (
    echo ❌ 安装到本地仓库失败
    pause
    exit /b 1
)

echo ✅ Starter构建完成

rem 4. 构建示例应用（可选）
set /p build_example="是否构建示例应用？(y/N): "
if /i "%build_example%"=="y" (
    echo 🏗️ 构建示例应用...
    cd /d "%EXAMPLE_DIR%"
    call mvn clean package -DskipTests=true
    if errorlevel 1 (
        echo ❌ 示例应用构建失败
        pause
        exit /b 1
    )
    echo ✅ 示例应用构建完成
)

rem 5. 发布到远程仓库（可选）
set /p deploy_remote="是否发布到远程Maven仓库？(y/N): "
if /i "%deploy_remote%"=="y" (
    echo 🚀 发布到远程仓库...
    cd /d "%STARTER_DIR%"
    call mvn deploy -DskipTests=true
    if errorlevel 1 (
        echo ❌ 发布失败
        pause
        exit /b 1
    )
    echo ✅ 发布完成
)

echo === 构建完成 ===
echo 📦 Starter已安装到本地仓库
echo 🔧 可以在其他项目中使用以下依赖：
echo.
echo ^<dependency^>
echo     ^<groupId^>com.module^</groupId^>
echo     ^<artifactId^>user-assistant-spring-boot-starter^</artifactId^>
echo     ^<version^>1.0.0^</version^>
echo ^</dependency^>
echo.
echo 📖 使用说明请查看 README.md

pause 