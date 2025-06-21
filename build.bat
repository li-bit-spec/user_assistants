@echo off
chcp 65001 >nul
echo === 开始构建 User Assistants 项目 ===

setlocal enabledelayedexpansion

rem 获取项目根目录
set PROJECT_ROOT=%~dp0
set PROJECT_ROOT=%PROJECT_ROOT:~0,-1%
set FRONTEND_DIR=%PROJECT_ROOT%\frontend
set BACKEND_DIR=%PROJECT_ROOT%\backend

echo 项目根目录: %PROJECT_ROOT%

rem 1. 构建前端
echo 🏗️ 开始构建前端...
cd /d "%FRONTEND_DIR%"

rem 检查node_modules是否存在
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

echo ✅ 前端构建完成

rem 2. 复制前端资源到后端
echo 📋 复制前端资源到后端...
set STATIC_DIR=%BACKEND_DIR%\src\main\resources\static

rem 创建static目录
if not exist "%STATIC_DIR%" mkdir "%STATIC_DIR%"

rem 清理旧的静态资源
if exist "%STATIC_DIR%\*" del /q "%STATIC_DIR%\*"
if exist "%STATIC_DIR%" for /d %%i in ("%STATIC_DIR%\*") do rmdir /s /q "%%i"

rem 复制新的静态资源
xcopy "%FRONTEND_DIR%\dist\*" "%STATIC_DIR%\" /s /e /y
if errorlevel 1 (
    echo ❌ 前端资源复制失败
    pause
    exit /b 1
)

echo ✅ 前端资源复制完成

rem 3. 构建后端
echo 🏗️ 开始构建后端...
cd /d "%BACKEND_DIR%"

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

rem 查找jar文件
for %%f in (target\*.jar) do (
    set JAR_FILE=%%f
    goto found_jar
)

echo ❌ 后端构建失败，未找到jar文件
pause
exit /b 1

:found_jar
echo ✅ 后端构建完成
echo 📦 JAR文件位置: %JAR_FILE%

rem 4. 创建发布目录
set RELEASE_DIR=%PROJECT_ROOT%\release
if not exist "%RELEASE_DIR%" mkdir "%RELEASE_DIR%"

rem 复制jar文件到发布目录
copy "%JAR_FILE%" "%RELEASE_DIR%\user-assistant.jar"

rem 复制配置文件
copy "%BACKEND_DIR%\src\main\resources\application.yml" "%RELEASE_DIR%\"
if exist "%BACKEND_DIR%\src\main\resources\application-prod.yml" (
    copy "%BACKEND_DIR%\src\main\resources\application-prod.yml" "%RELEASE_DIR%\"
)

echo === 构建完成 ===
echo 📦 发布文件位置: %RELEASE_DIR%
echo 🚀 运行命令: java -jar %RELEASE_DIR%\user-assistant.jar
echo.
echo 📋 部署说明:
echo 1. 将 release 目录下的文件复制到目标服务器
echo 2. 配置数据库连接信息（application.yml）
echo 3. 运行: java -jar user-assistant.jar

pause 