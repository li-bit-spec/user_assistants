#!/bin/bash

echo "=== 开始构建 User Assistants 项目 ==="

# 设置错误时退出
set -e

# 获取项目根目录
PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/backend"

echo "项目根目录: $PROJECT_ROOT"

# 1. 构建前端
echo "🏗️  开始构建前端..."
cd "$FRONTEND_DIR"

# 检查node_modules是否存在，不存在则安装依赖
if [ ! -d "node_modules" ]; then
    echo "📦 安装前端依赖..."
    npm install
fi

echo "🔨 构建前端项目..."
npm run build

if [ ! -d "dist" ]; then
    echo "❌ 前端构建失败，dist目录不存在"
    exit 1
fi

echo "✅ 前端构建完成"

# 2. 复制前端资源到后端
echo "📋 复制前端资源到后端..."
STATIC_DIR="$BACKEND_DIR/src/main/resources/static"

# 创建static目录（如果不存在）
mkdir -p "$STATIC_DIR"

# 清理旧的静态资源
rm -rf "$STATIC_DIR"/*

# 复制新的静态资源
cp -r "$FRONTEND_DIR/dist"/* "$STATIC_DIR/"

echo "✅ 前端资源复制完成"

# 3. 构建后端
echo "🏗️  开始构建后端..."
cd "$BACKEND_DIR"

# 清理之前的构建
echo "🧹 清理之前的构建..."
mvn clean

# 编译和打包
echo "📦 Maven打包..."
mvn package -DskipTests=true

# 检查jar文件是否生成
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ 后端构建失败，未找到jar文件"
    exit 1
fi

echo "✅ 后端构建完成"
echo "📦 JAR文件位置: $JAR_FILE"

# 4. 创建发布目录
RELEASE_DIR="$PROJECT_ROOT/release"
mkdir -p "$RELEASE_DIR"

# 复制jar文件到发布目录
cp "$JAR_FILE" "$RELEASE_DIR/user-assistant.jar"

# 复制配置文件
cp "$BACKEND_DIR/src/main/resources/application.yml" "$RELEASE_DIR/"
cp "$BACKEND_DIR/src/main/resources/application-prod.yml" "$RELEASE_DIR/" 2>/dev/null || true

echo "=== 构建完成 ==="
echo "📦 发布文件位置: $RELEASE_DIR"
echo "🚀 运行命令: java -jar $RELEASE_DIR/user-assistant.jar"
echo ""
echo "📋 部署说明:"
echo "1. 将 release 目录下的文件复制到目标服务器"
echo "2. 配置数据库连接信息（application.yml）"
echo "3. 运行: java -jar user-assistant.jar" 