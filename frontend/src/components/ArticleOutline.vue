<template>
  <div class="article-outline" :class="{ collapsed: !expanded }">
    <!-- 目录头部 -->
    <div class="outline-header" @click="toggleExpanded">
      <span class="outline-title">目录</span>
      <el-icon class="toggle-icon" :class="{ rotated: !expanded }">
        <ArrowRight />
      </el-icon>
    </div>

    <!-- 目录内容 -->
    <div v-show="expanded" class="outline-content">
      <div v-if="headings.length === 0" class="no-headings">
        暂无目录
      </div>
      <div
        v-for="(heading, index) in headings"
        :key="index"
        class="outline-item"
        :class="[`level-${heading.level}`, { active: activeHeading === heading.id }]"
        @click="scrollToHeading(heading.id)"
      >
        {{ heading.text }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['toggle'])

const expanded = ref(true)
const activeHeading = ref('')

// 切换展开/收起
const toggleExpanded = () => {
  expanded.value = !expanded.value
  emit('toggle', expanded.value)
}

// 解析HTML内容中的标题
const headings = computed(() => {
  if (!props.content) return []
  
  try {
    // 创建临时DOM解析HTML
    const parser = new DOMParser()
    const doc = parser.parseFromString(props.content, 'text/html')
    const headingElements = doc.querySelectorAll('h1, h2, h3, h4, h5, h6')
    
    return Array.from(headingElements).map((el, index) => {
      const level = parseInt(el.tagName.substring(1))
      const text = el.textContent.trim()
      const id = `heading-${level}-${index}-${text.toLowerCase().replace(/[^a-z0-9]/g, '-')}`
      
      return { level, text, id, index }
    })
  } catch (error) {
    console.error('解析标题失败:', error)
    return []
  }
})

// 设置编辑器中标题的ID
const setHeadingIds = () => {
  if (headings.value.length === 0) return
  
  // 查找编辑器容器
  const findEditor = () => {
    return document.querySelector('[contenteditable="true"]') || 
           document.querySelector('.w-e-text-container') ||
           document.querySelector('.ProseMirror')
  }
  
  const trySetIds = (retryCount = 0) => {
    const editor = findEditor()
    if (!editor) {
      if (retryCount < 10) {
        setTimeout(() => trySetIds(retryCount + 1), 200)
      }
      return
    }
    
    const editorHeadings = editor.querySelectorAll('h1, h2, h3, h4, h5, h6')
    editorHeadings.forEach((heading, index) => {
      if (headings.value[index]) {
        heading.id = headings.value[index].id
      }
    })
    
    console.log(`设置了 ${editorHeadings.length} 个标题ID`)
  }
  
  nextTick(() => {
    setTimeout(() => trySetIds(), 500)
  })
}

// 滚动到指定标题
const scrollToHeading = (headingId) => {
  const heading = document.getElementById(headingId)
  const container = document.querySelector('.manual-container')
  
  if (!heading || !container) {
    console.warn('未找到标题或滚动容器', { headingId, heading, container })
    return
  }
  
  // 计算滚动位置
  const containerRect = container.getBoundingClientRect()
  const headingRect = heading.getBoundingClientRect()
  const offset = 80 // 顶部偏移量
  
  const scrollTop = container.scrollTop + (headingRect.top - containerRect.top) - offset
  
  container.scrollTo({
    top: Math.max(0, scrollTop),
    behavior: 'smooth'
  })
  
  // 设置活跃标题
  activeHeading.value = headingId
  
  // 添加高亮效果
  heading.classList.add('heading-highlight')
  setTimeout(() => {
    heading.classList.remove('heading-highlight')
  }, 2000)
}

// 监听滚动更新活跃标题
const updateActiveHeading = () => {
  const container = document.querySelector('.manual-container')
  if (!container || headings.value.length === 0) return
  
  const containerRect = container.getBoundingClientRect()
  const offset = 100
  
  for (let i = headings.value.length - 1; i >= 0; i--) {
    const heading = document.getElementById(headings.value[i].id)
    if (heading) {
      const headingRect = heading.getBoundingClientRect()
      if (headingRect.top - containerRect.top <= offset) {
        activeHeading.value = headings.value[i].id
        break
      }
    }
  }
}

// 防抖滚动处理
let scrollTimer = null
const handleScroll = () => {
  if (scrollTimer) clearTimeout(scrollTimer)
  scrollTimer = setTimeout(updateActiveHeading, 100)
}

// 监听内容变化
let contentWatcher = null
const watchContent = () => {
  if (contentWatcher) clearTimeout(contentWatcher)
  contentWatcher = setTimeout(() => {
    setHeadingIds()
  }, 100)
}

// 组件挂载
onMounted(() => {
  setHeadingIds()
  
  const container = document.querySelector('.manual-container')
  if (container) {
    container.addEventListener('scroll', handleScroll)
  }
})

// 组件卸载
onUnmounted(() => {
  const container = document.querySelector('.manual-container')
  if (container) {
    container.removeEventListener('scroll', handleScroll)
  }
  
  if (scrollTimer) clearTimeout(scrollTimer)
  if (contentWatcher) clearTimeout(contentWatcher)
})

// 监听内容变化
watch(() => props.content, () => {
  watchContent()
}, { immediate: true })
</script>

<style scoped>
.article-outline {
  position: fixed;
  top: 84px;
  right: 24px;
  width: 260px;
  max-height: calc(100vh - 120px);
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
  z-index: 100;
  transition: all 0.3s ease;
}

.article-outline.collapsed {
  width: 48px;
  overflow: hidden;
}

.outline-header {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  user-select: none;
  background: #fafafa;
  border-radius: 8px 8px 0 0;
}

.outline-header:hover {
  background: #f5f5f5;
}

.outline-title {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.toggle-icon {
  transition: transform 0.3s ease;
  color: #666;
}

.toggle-icon.rotated {
  transform: rotate(90deg);
}

.outline-content {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  padding: 8px 0;
}

.no-headings {
  padding: 16px;
  text-align: center;
  color: #999;
  font-size: 12px;
}

.outline-item {
  padding: 6px 16px;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.outline-item:hover {
  background-color: #f8f9fa;
  color: #409eff;
}

.outline-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  border-left-color: #409eff;
  font-weight: 500;
}

/* 不同级别标题的缩进 */
.level-1 { padding-left: 16px; font-weight: 500; }
.level-2 { padding-left: 28px; }
.level-3 { padding-left: 40px; }
.level-4 { padding-left: 52px; }
.level-5 { padding-left: 64px; }
.level-6 { padding-left: 76px; }

/* 滚动条样式 */
.outline-content::-webkit-scrollbar {
  width: 4px;
}

.outline-content::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.outline-content::-webkit-scrollbar-track {
  background: transparent;
}

/* 标题高亮效果 */
:deep(.heading-highlight) {
  background-color: #fff3cd !important;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}
</style> 