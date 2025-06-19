<template>
  <div class="layout-container">
    <!-- Drawer导航栏 -->
    <el-drawer
      v-model="isDrawerOpen"
      direction="ltr"
      size="300px"
      :with-header="false"
      class="sidebar-drawer"
    >
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
    </el-drawer>

    <!-- 右侧内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="content-header">
        <!-- 添加菜单切换按钮 -->
        <el-button
          type="text"
          class="toggle-btn"
          @click="toggleDrawer"
        >
          <el-icon :size="20">
            <Fold v-if="isDrawerOpen" />
            <Expand v-else />
          </el-icon>
        </el-button>
        <el-breadcrumb>
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ getMenuTitle() }}</el-breadcrumb-item>
        </el-breadcrumb>
        <!-- 右侧操作按钮 -->
        <div class="header-actions">
          <!-- 新增文章按钮 -->
          <el-button 
            v-if="$route.path.startsWith('/manual')"
            type="primary" 
            :icon="Plus"
            @click="handleNewArticle"
            class="action-btn"
            size="small"
          >
            新增文章
          </el-button>
          
          <!-- 技术支持编辑按钮 -->
          <template v-if="$route.path === '/support'">
            <el-button 
              v-if="!supportEditingState"
              type="primary" 
              :icon="Edit"
              @click="handleSupportEdit"
              class="action-btn"
              size="small"
            >
              编辑
            </el-button>
            <template v-else>
              <el-button 
                type="success" 
                :icon="Check"
                @click="handleSupportSave"
                :loading="supportSaving"
                class="action-btn"
                size="small"
              >
                保存
              </el-button>
              <el-button 
                :icon="Close"
                @click="handleSupportCancel"
                class="action-btn"
                size="small"
                plain
              >
                取消
              </el-button>
            </template>
          </template>
        </div>
      </div>
      <!-- 内容区域 -->
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" ref="manualViewRef" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Document, ChatLineRound, Service, Monitor, Plus, ArrowDown, Delete, Fold, Expand, Edit, Close, Check } from '@element-plus/icons-vue'
import { useManualStore } from '@/store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = useManualStore()
const articles = ref([])
const manualViewRef = ref(null)
const isDrawerOpen = ref(false)
const supportEditingState = ref(false)
const supportSaving = ref(false)

// 监听路由变化，当路由变化时刷新文章列表
watch(
  () => route.path,
  async (newPath) => {
    if (newPath.startsWith('/manual')) {
      await fetchArticles()
    }
    // 路由变化时重置技术支持编辑状态
    if (newPath !== '/support') {
      supportEditingState.value = false
      supportSaving.value = false
    }
  }
)

onMounted(async () => {
  await fetchArticles()
})

const fetchArticles = async () => {
  try {
    articles.value = await store.fetchArticles()
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  }
}

const handleNewArticle = () => {
  if (manualViewRef.value) {
    manualViewRef.value.showNewArticleDialog()
  }
}

// 技术支持编辑相关方法
const handleSupportEdit = () => {
  supportEditingState.value = true
  if (manualViewRef.value && manualViewRef.value.handleEdit) {
    manualViewRef.value.handleEdit()
  }
}

const handleSupportSave = async () => {
  supportSaving.value = true
  try {
    if (manualViewRef.value && manualViewRef.value.handleSave) {
      await manualViewRef.value.handleSave()
      supportEditingState.value = false
    }
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    supportSaving.value = false
  }
}

const handleSupportCancel = () => {
  supportEditingState.value = false
  if (manualViewRef.value && manualViewRef.value.handleCancel) {
    manualViewRef.value.handleCancel()
  }
}



const getMenuTitle = () => {
  const path = route.path
  if (path.startsWith('/manual')) return '使用手册'
  if (path === '/feedback') return '用户反馈'
  if (path === '/support') return '技术支持'
  return ''
}

const toggleDrawer = () => {
  isDrawerOpen.value = !isDrawerOpen.value
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: #f5f5f5;
}

.sidebar-drawer {
  :deep(.el-drawer__body) {
    padding: 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
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

.toggle-btn {
  margin-right: 16px;
  padding: 0;
  height: 32px;
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: #f5f7fa;
  }
}

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
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
