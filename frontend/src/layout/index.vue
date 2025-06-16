<template>
  <div class="layout-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <!-- Logo和标题 -->
      <div class="sidebar-header">
        <el-icon class="mr-2 text-primary-600"><Monitor /></el-icon>
        <span class="text-primary-600">用户助手系统</span>
      </div>
      <!-- 导航菜单 -->
      <div class="sidebar-menu">
        <el-menu
          class="navigation-menu"
          :router="true"
          :default-active="$route.path"
        >
          <el-sub-menu index="/manual">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>使用手册</span>
            </template>
            <el-menu-item class="menu-item-button">
              <el-button 
                type="primary" 
                size="small" 
                class="new-article-btn"
                @click="handleNewArticle"
              >
                <el-icon class="mr-1"><Plus /></el-icon>
                新增文章
              </el-button>
            </el-menu-item>
            <el-menu-item
              v-for="article in articles"
              :key="article.id"
              :index="'/manual/' + article.id"
              class="article-item"
            >
              <el-icon><Document /></el-icon>
              <span class="ml-2">{{ article.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/feedback">
            <el-icon><ChatLineRound /></el-icon>
            <span>用户反馈</span>
          </el-menu-item>
          <el-menu-item index="/support">
            <el-icon><Service /></el-icon>
            <span>技术支持</span>
          </el-menu-item>
        </el-menu>
      </div>
    </div>

    <!-- 右侧内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="content-header">
        <el-breadcrumb>
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ getMenuTitle() }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <!-- 内容区域 -->
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Document, ChatLineRound, Service, Monitor, Plus } from '@element-plus/icons-vue'
import { useManualStore } from '@/store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = useManualStore()
const articles = ref([])
const dialogVisible = ref(false)

// 监听路由变化，当路由变化时刷新文章列表
watch(
  () => route.path,
  async () => {
    if (route.path.startsWith('/manual')) {
      await fetchArticles()
    }
  }
)

onMounted(async () => {
  await fetchArticles()
  if (route.query.action === 'new') {
    showNewArticleDialog()
  }
})

watch(
  () => route.query.action,
  (action) => {
    if (action === 'new') {
      showNewArticleDialog()
    }
  }
)

const fetchArticles = async () => {
  try {
    articles.value = await store.fetchArticles()
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  }
}

const handleNewArticle = () => {
  store.setShowNewArticleDialog(true)
}

function showNewArticleDialog() {
  newArticle.value = { title: '', content: '' }
  dialogVisible.value = true
  drawerVisible.value = false
}

const getMenuTitle = () => {
  const path = route.path
  if (path.startsWith('/manual')) return '使用手册'
  if (path === '/feedback') return '用户反馈'
  if (path === '/support') return '技术支持'
  return ''
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: #f5f5f5;
}

.sidebar {
  width: 300px;
  background-color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  z-index: 1000;
}

.sidebar-header {
  height: 60px;
  padding: 0 24px;
  background-color: #e6f7ff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

.navigation-menu {
  border-right: none !important;
  height: 100%;
}

.main-content {
  flex: 1;
  margin-left: 300px;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.content-header {
  height: 60px;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 24px;
  display: flex;
  align-items: center;
  z-index: 100;
}

.content-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.menu-item-button {
  padding: 8px 16px;
}

.new-article-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.article-item {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;
  border-radius: 4px;
}

:deep(.el-menu-item.is-active) {
  background-color: #f0f9ff !important;
  color: #1890ff;
}

:deep(.el-sub-menu .el-menu-item) {
  margin: 4px 16px;
}

:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;
  border-radius: 4px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.text-primary-600 {
  color: #1890ff;
}
</style>
