#!/bin/bash

echo "=== 构建并发布 User Assistant Spring Boot Starter ==="

# 设置错误时退出
set -e

# 获取项目根目录
PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
STARTER_DIR="$PROJECT_ROOT/user-assistant-spring-boot-starter"
EXAMPLE_DIR="$PROJECT_ROOT/example-app"

echo "项目根目录: $PROJECT_ROOT"

# 1. 构建前端资源
echo "🏗️  构建前端资源..."
cd "$PROJECT_ROOT/frontend"

if [ ! -d "node_modules" ]; then
    echo "📦 安装前端依赖..."
    npm install
fi

echo "🔨 构建前端项目..."
npm run build

if [ ! -d "dist" ]; then
    echo "❌ 前端构建失败"
    exit 1
fi

# 2. 复制前端静态资源到Starter
echo "📋 复制前端资源到Starter..."
STATIC_DIR="$STARTER_DIR/src/main/resources/static"
mkdir -p "$STATIC_DIR"
rm -rf "$STATIC_DIR"/*
cp -r "$PROJECT_ROOT/frontend/dist"/* "$STATIC_DIR/"

echo "✅ 前端资源复制完成"

# 3. 构建Starter
echo "🏗️  构建User Assistant Starter..."
cd "$STARTER_DIR"

# 清理之前的构建
echo "🧹 清理之前的构建..."
mvn clean

# 编译和打包
echo "📦 Maven打包..."
mvn package -DskipTests=true

# 安装到本地仓库
echo "🔧 安装到本地Maven仓库..."
mvn install -DskipTests=true

echo "✅ Starter构建完成"

# 4. 构建示例应用（可选）
read -p "是否构建示例应用？(y/N): " build_example

if [[ $build_example =~ ^[Yy]$ ]]; then
    echo "🏗️  构建示例应用..."
    cd "$EXAMPLE_DIR"
    mvn clean package -DskipTests=true
    echo "✅ 示例应用构建完成"
fi

# 5. 发布到远程仓库（可选）
read -p "是否发布到远程Maven仓库？(y/N): " deploy_remote

if [[ $deploy_remote =~ ^[Yy]$ ]]; then
    echo "🚀 发布到远程仓库..."
    cd "$STARTER_DIR"
    mvn deploy -DskipTests=true
    echo "✅ 发布完成"
fi

echo "=== 构建完成 ==="
echo "📦 Starter已安装到本地仓库"
echo "🔧 可以在其他项目中使用以下依赖："
echo ""
echo "<dependency>"
echo "    <groupId>com.module</groupId>"
echo "    <artifactId>user-assistant-spring-boot-starter</artifactId>"
echo "    <version>1.0.0</version>"
echo "</dependency>"
echo ""
echo "�� 使用说明请查看 README.md" 