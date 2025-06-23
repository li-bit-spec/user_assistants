<template>
  <div class="support-container">
    <!-- 内容展示/编辑区域 -->
    <div class="support-content">
      <!-- 工具栏区域空白遮罩（编辑模式且工具栏展开时显示） -->
      <div v-if="isEditing && isToolbarExpanded" class="toolbar-blank-overlay"></div>
      
      <!-- 工具栏展开/收起按钮（编辑模式下显示） -->
      <div v-if="isEditing" class="toolbar-toggle-btn" @click="toggleToolbar">
        <el-icon :class="{ 'rotated': !isToolbarExpanded }">
          <ArrowDown />
        </el-icon>
      </div>

      <!-- 固定的编辑器工具栏（编辑模式下显示） -->
      <div 
        v-if="isEditing" 
        class="fixed-editor-toolbar"
        :class="{ 'collapsed': !isToolbarExpanded }"
      >
        <Toolbar
          :key="'toolbar-' + toolbarKey"
          :editor="editorRef"
          :defaultConfig="toolbarConfig"
          :mode="mode"
          class="editor-toolbar"
        />
      </div>

      <div class="editor-wrapper">
        <Editor
          :key="'editor-' + editorKey"
          :defaultConfig="editorConfig"
          :mode="mode"
          v-model="editingContent"
          @onCreated="handleCreated"
          @onChange="handleChange"
          @onDestroyed="handleDestroyed"
        />
      </div>
    </div>

    <!-- 文章目录 -->
    <ArticleOutline 
      v-if="content || editingContent"
      :content="isEditing ? editingContent : content"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as supportApi from '@/api/support'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/upload'
import ArticleOutline from '@/components/ArticleOutline.vue'
import { Edit, Check, Close, ArrowDown } from '@element-plus/icons-vue'

const isEditing = ref(false)
const content = ref('')
const editingContent = ref('')
const editorRef = shallowRef()
const loading = ref(false)
const toolbarKey = ref(0) // 用于强制重新渲染工具栏
const editorKey = ref(0) // 用于强制重新渲染编辑器
const isToolbarExpanded = ref(true) // 工具栏展开状态

// 编辑器配置
const mode = 'default'
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const response = await uploadFile(file)
          let url = ''
          if (response.code === 0 && response.data && response.data.code === 0) {
            url = response.data.data
          } else if (response.code === 0 && response.data) {
            url = response.data
          }
          if (url) {
            insertFn(url, '', '')
          } else {
            ElMessage.error('图片上传失败')
          }
        } catch (e) {
          ElMessage.error('图片上传失败')
        }
      }
    },
    // 表格配置
    insertTable: {
      rows: 3,
      cols: 3,
    },
    // 禁用一些可能导致表格问题的功能
    editTable: {
      // 禁用表格的一些自动功能，避免冲突
    }
  },
  // 自定义解析HTML，确保表格结构正确
  customPasteFilterStyle: (node) => {
    // 处理表格相关的样式过滤
    if (node.tagName === 'TABLE' || node.tagName === 'TD' || node.tagName === 'TH') {
      return true
    }
    return true
  },
  // 添加表格处理的自定义配置
  customParseElemHtml: (elemNode, children, editor) => {
    // 确保表格元素的正确解析
    if (elemNode.tagName === 'TABLE') {
      return `<table style="border-collapse: collapse; width: 100%; margin: 10px 0;">${children}</table>`
    }
    if (elemNode.tagName === 'TD' || elemNode.tagName === 'TH') {
      return `<${elemNode.tagName.toLowerCase()} style="border: 1px solid #dcdfe6; padding: 8px; min-width: 100px;">${children}</${elemNode.tagName.toLowerCase()}>`
    }
    return null
  }
}

let supportId = null

// 编辑器回调函数
const handleCreated = (editor) => {
  console.log('Editor created, editing mode:', isEditing.value)
  editorRef.value = editor // 记录 editor 实例
  
  // 添加表格事件监听，防止单元格内容互相影响
  setupTableEventListeners(editor)
  
  // 延迟设置内容和状态，确保编辑器完全初始化
  setTimeout(() => {
    // 设置编辑器内容
    if (content.value) {
      editor.setHtml(content.value)
    }
    
    // 根据当前编辑状态设置编辑器状态
    if (isEditing.value) {
      editor.enable()
      setTimeout(() => {
        editor.focus()
      }, 100)
    } else {
      editor.disable()
    }
  }, 50)
}

// 设置表格事件监听器
const setupTableEventListeners = (editor) => {
  // 监听编辑器内的表格操作
  const editorDom = editor.getDom()
  if (editorDom) {
    // 阻止表格单元格的默认行为，确保焦点正确
    editorDom.addEventListener('click', (e) => {
      const target = e.target
      if (target.tagName === 'TD' || target.tagName === 'TH') {
        // 确保点击的单元格获得正确的焦点
        setTimeout(() => {
          if (target.contentEditable !== 'false') {
            target.focus()
          }
        }, 10)
      }
    })
    
    // 监听输入事件，确保内容只在当前单元格内
    editorDom.addEventListener('input', (e) => {
      const target = e.target
      if ((target.tagName === 'TD' || target.tagName === 'TH') && target.isContentEditable) {
        // 阻止内容跨单元格传播
        e.stopPropagation()
      }
    })
    
    // 监听键盘事件，处理Tab键在表格中的行为
    editorDom.addEventListener('keydown', (e) => {
      const target = e.target
      if ((target.tagName === 'TD' || target.tagName === 'TH') && e.key === 'Tab') {
        e.preventDefault()
        const cells = Array.from(editorDom.querySelectorAll('td, th'))
        const currentIndex = cells.indexOf(target)
        const nextIndex = e.shiftKey ? currentIndex - 1 : currentIndex + 1
        
        if (nextIndex >= 0 && nextIndex < cells.length) {
          cells[nextIndex].focus()
        }
      }
    })
  }
}

const handleDestroyed = () => {
  console.log('Editor destroyed')
  editorRef.value = null
}

// 用于实时更新目录的防抖函数
let updateTimer = null
const debouncedUpdateContent = (html) => {
  if (updateTimer) clearTimeout(updateTimer)
  updateTimer = setTimeout(() => {
    editingContent.value = html
  }, 300) // 300ms延迟，避免过于频繁的更新
}

const handleChange = (editor) => {
  if (isEditing.value) {
    const html = editor.getHtml()
    
    // 处理表格内容，确保表格单元格独立性
    const processedHtml = processTableContent(html)
    
    // 立即更新用于保存的内容
    editingContent.value = processedHtml
    // 防抖更新用于目录显示的内容
    debouncedUpdateContent(processedHtml)
  }
}

// 处理表格内容，确保单元格独立性
const processTableContent = (html) => {
  try {
    // 创建临时DOM来处理HTML
    const parser = new DOMParser()
    const doc = parser.parseFromString(html, 'text/html')
    
    // 查找所有表格
    const tables = doc.querySelectorAll('table')
    tables.forEach(table => {
      // 确保表格有正确的属性
      if (!table.style.borderCollapse) {
        table.style.borderCollapse = 'collapse'
      }
      if (!table.style.width) {
        table.style.width = '100%'
      }
      
      // 处理表格单元格
      const cells = table.querySelectorAll('td, th')
      cells.forEach(cell => {
        // 确保每个单元格有独立的边框和内边距
        if (!cell.style.border) {
          cell.style.border = '1px solid #dcdfe6'
        }
        if (!cell.style.padding) {
          cell.style.padding = '8px'
        }
        if (!cell.style.minWidth) {
          cell.style.minWidth = '100px'
        }
        
        // 移除可能导致内容重复的属性
        cell.removeAttribute('contenteditable')
      })
    })
    
    return doc.body.innerHTML
  } catch (error) {
    console.warn('处理表格内容时出错:', error)
    return html
  }
}

// 进入编辑模式
const enterEditMode = () => {
  if (!isEditing.value) {
    console.log('Entering edit mode')
    // 先销毁当前编辑器，然后重新创建
    if (editorRef.value) {
      editorRef.value.destroy()
      editorRef.value = null
    }
    
    // 更新编辑状态和keys，强制重新渲染编辑器和工具栏
    isEditing.value = true
    toolbarKey.value = Date.now()
    editorKey.value = Date.now()
    editingContent.value = content.value
  }
}

// 退出编辑模式
const exitEditMode = () => {
  if (isEditing.value) {
    console.log('Exiting edit mode')
    // 先销毁当前编辑器
    if (editorRef.value) {
      editorRef.value.destroy()
      editorRef.value = null
    }
    
    // 更新编辑状态和keys，强制重新渲染编辑器
    isEditing.value = false
    toolbarKey.value = Date.now()
    editorKey.value = Date.now()
    editingContent.value = content.value
  }
}

// 新的操作函数
const handleEdit = () => {
  enterEditMode()
}

const handleSave = async () => {
  const success = await saveContent()
  if (!success) {
    // 保存失败，保持编辑状态
    console.log('保存失败，保持编辑状态')
  }
}

const handleCancel = () => {
  if (isEditing.value) {
    // 恢复原始内容，丢弃未保存的修改
    editingContent.value = content.value
    exitEditMode()
  }
}

// 切换编辑状态（保持向后兼容）
const toggleEdit = () => {
  if (isEditing.value) {
    handleCancel()
  } else {
    enterEditMode()
  }
}

// 切换工具栏展开/收起状态
const toggleToolbar = () => {
  isToolbarExpanded.value = !isToolbarExpanded.value
}

const fetchContent = async () => {
  try {
    const res = await supportApi.fetchSupportList()
    console.log('Support content response:', res) // 添加调试日志
    if (res.data && res.data.length > 0) {
      const supportData = res.data[0]
      content.value = supportData.content || ''
      editingContent.value = supportData.content || ''
      supportId = supportData.id
    } else {
      // 如果没有内容，创建一个初始内容
      content.value = '<p>欢迎使用技术支持</p>'
      editingContent.value = content.value
      const res = await supportApi.addSupport({ 
        content: content.value, 
        title: '技术支持' 
      })
      if (res.data) {
        supportId = res.data.id
      }
    }
    
    // 如果当前不在编辑模式，强制重新渲染编辑器以显示新内容
    if (!isEditing.value) {
      editorKey.value = Date.now()
    }
  } catch (error) {
    console.error('获取内容失败:', error) // 添加错误日志
    ElMessage.error('获取内容失败')
    // 设置默认内容
    content.value = '<p>欢迎使用技术支持</p>'
    editingContent.value = content.value
    // 强制重新渲染编辑器以显示默认内容
    editorKey.value = Date.now()
  }
}

const saveContent = async () => {
  loading.value = true
  try {
    if (supportId) {
      await supportApi.updateSupport({ 
        id: supportId, 
        content: editingContent.value,
        title: '技术支持'
      })
    } else {
      const res = await supportApi.addSupport({ 
        content: editingContent.value, 
        title: '技术支持' 
      })
      if (res.data) {
        supportId = res.data.id
      }
    }
    content.value = editingContent.value
    exitEditMode() // 保存后切换到查看状态
    ElMessage.success('保存成功')
    await fetchContent()
    return true
  } catch (error) {
    console.error('保存失败详情:', error)
    
    // 根据错误类型显示不同的友好提示
    let errorMessage = '保存失败，请检查网络连接后重试'
    
    if (error.response) {
      // 服务器返回错误响应
      const status = error.response.status
      if (status === 401) {
        errorMessage = '登录状态已过期，请刷新页面重新登录'
      } else if (status === 403) {
        errorMessage = '您没有权限修改此内容'
      } else if (status === 404) {
        errorMessage = '内容不存在，可能已被删除'
      } else if (status === 413) {
        errorMessage = '内容过长，请减少文字或图片后重试'
      } else if (status >= 500) {
        errorMessage = '服务器繁忙，请稍后再试'
      } else if (status === 400) {
        errorMessage = '内容格式有误，请检查特殊字符或表情符号后重试'
      } else if (status >= 400) {
        // 处理其他4xx错误
        errorMessage = '保存失败，请检查内容格式后重试'
      }
      
      // 如果服务器返回了具体的错误信息，优先使用
      if (error.response.data?.message) {
        const serverMessage = error.response.data.message
        
        // 检查是否是emoji编码错误
        if (serverMessage.includes('Incorrect string value') || 
            serverMessage.includes('\\xF0') || 
            serverMessage.includes('emoji') || 
            serverMessage.includes('utf8mb4') ||
            /\\x[A-F0-9]{2}/.test(serverMessage)) {
          errorMessage = '内容包含不支持的表情符号，请移除表情后重试'
        } else if (serverMessage.includes('SQLException') || serverMessage.includes('database')) {
          errorMessage = '数据保存失败，请检查内容格式后重试'
        } else if (!serverMessage.includes('status code') && 
                   !serverMessage.includes('Request failed')) {
          errorMessage = serverMessage
        }
      }
    } else if (error.code === 'NETWORK_ERROR' || !navigator.onLine) {
      errorMessage = '网络连接异常，请检查网络设置'
    } else if (error.code === 'TIMEOUT') {
      errorMessage = '连接超时，请稍后重试'
    } else if (error.message && 
               !error.message.includes('status code') &&
               !error.message.includes('Request failed')) {
      errorMessage = error.message
    }
    
    // 最后检查，确保不显示技术性错误信息
    const technicalTerms = ['status code', 'Request failed', 'HTTP', 'xhr', 'ajax', 'fetch']
    if (technicalTerms.some(term => errorMessage.toLowerCase().includes(term.toLowerCase()))) {
      errorMessage = '保存失败，请稍后重试'
    }

    ElMessage({
      type: 'error',
      message: errorMessage,
      duration: 5000, // 延长显示时间
      showClose: true
    })
    
    // 保存失败时保持编辑状态，不退出编辑模式
    return false
  } finally {
    loading.value = false
  }
}

// 监听编辑状态变化，确保工具栏和编辑器状态同步
watch(isEditing, (newValue, oldValue) => {
  console.log('Edit mode changed:', oldValue, '->', newValue)
  // 编辑状态变化时，编辑器会通过key重新创建，这里不需要额外操作
})

onMounted(() => {
  fetchContent()
})

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  console.log('Component unmounting')
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
    editorRef.value = null
  }
  // 清理定时器
  if (updateTimer) {
    clearTimeout(updateTimer)
  }
})

// 导出方法供父组件调用
defineExpose({
  handleEdit,
  handleSave,
  handleCancel,
  saveContent // 确保导出saveContent方法
})
</script>

<style scoped>
.support-container {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background-color: white;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
}

.support-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: visible;
  padding: 50px;
  padding-right: 300px; /* 为目录留出空间 */
  min-height: calc(100% - 60px);
  position: relative;
}

/* 当编辑模式下，为固定工具栏留出空间 */
.support-content:has(.fixed-editor-toolbar:not(.collapsed)) {
  padding-top: 180px; /* 工具栏展开时的padding */
}

.support-content:has(.fixed-editor-toolbar.collapsed) {
  padding-top: 90px; /* 工具栏收起时的padding */
}

.editor-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: none;
  min-height: 500px;
  height: calc(100vh - 220px);
  overflow: visible;
}

:deep(.w-e-text-container) {
  flex: 1;
  overflow-y: visible;
  height: 100% !important;
  min-height: 400px !important;
  border: none !important;
}

:deep(.w-e-scroll) {
  height: 100% !important;
  min-height: 400px !important;
  overflow: visible !important;
}

:deep(.ProseMirror) {
  height: 100% !important;
  min-height: 400px !important;
  padding: 16px;
  box-sizing: border-box;
  overflow: visible;
  outline: none;

  &[contenteditable="false"] {
    background-color: transparent;
    cursor: default;
    user-select: text;
  }

  /* 标题高亮效果 */
  h1, h2, h3, h4, h5, h6 {
    scroll-margin-top: 80px;
    transition: background-color 0.3s;

    &.heading-highlight {
      background-color: #fff3cd !important;
      border-radius: 4px;
      padding: 4px 8px;
      margin: -4px -8px;
    }
  }
}

:deep(.ProseMirror img) {
  max-width: 100%;
  height: auto;
  margin: 10px 0;
}

:deep(.ProseMirror table) {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
  table-layout: fixed; /* 固定表格布局，避免单元格内容互相影响 */
}

:deep(.ProseMirror table td),
:deep(.ProseMirror table th) {
  border: 1px solid #dcdfe6;
  padding: 8px;
  min-width: 100px;
  position: relative; /* 确保单元格独立定位 */
  vertical-align: top; /* 垂直对齐到顶部 */
  word-wrap: break-word; /* 长文本换行 */
  overflow-wrap: break-word;
}

:deep(.ProseMirror table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.ProseMirror table tr:hover) {
  background-color: #f5f7fa;
}

/* 修复表格输入问题的关键样式 */
:deep(.ProseMirror table td[contenteditable]),
:deep(.ProseMirror table th[contenteditable]) {
  outline: 2px solid #409eff;
  outline-offset: -2px;
}

/* 确保表格单元格内的文本不会跨单元格 */
:deep(.ProseMirror table td *),
:deep(.ProseMirror table th *) {
  max-width: 100%;
  box-sizing: border-box;
}

/* 防止表格内容溢出 */
:deep(.ProseMirror table) {
  overflow: hidden;
}

:deep(.ProseMirror table td),
:deep(.ProseMirror table th) {
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.ProseMirror .image-resizer) {
  display: none;
}

:deep(.ProseMirror .image-resizer .resize-handle) {
  display: none;
}

/* 工具栏区域空白遮罩 */
.toolbar-blank-overlay {
  position: fixed;
  top: 60px; /* 从导航栏下方开始 */
  left: 0;
  right: 0;
  height: 120px; /* 覆盖工具栏和按钮区域 */
  background-color: white;
  z-index: 35; /* 在内容之上，但在工具栏之下 */
  pointer-events: none; /* 允许点击穿透 */
}

/* 工具栏切换按钮样式 */
.toolbar-toggle-btn {
  position: fixed;
  top: 60px; /* 紧贴顶部导航栏 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 45;
  width: 40px;
  height: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
  border-bottom: none; /* 移除底部边框，与工具栏连接 */
  border-radius: 6px 6px 0 0; /* 只有顶部圆角 */
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.05); /* 向上的阴影 */
}

.toolbar-toggle-btn:hover {
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  box-shadow: 0 -2px 6px rgba(0, 0, 0, 0.1);
}

.toolbar-toggle-btn .el-icon {
  color: #606266;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.toolbar-toggle-btn .el-icon.rotated {
  transform: rotate(180deg);
}

/* 固定编辑器工具栏样式 */
.fixed-editor-toolbar {
  position: fixed;
  top: 76px; /* 紧贴按钮底部 */
  left: 20px; /* 减少左侧填充 */
  right: 324px; /* content-body padding 24px + support-content padding-right 300px */
  z-index: 40;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
  border-radius: 0 0 8px 8px; /* 只有底部圆角 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 0; /* 移除margin，紧贴按钮 */
  transition: all 0.3s ease;
  max-height: 200px;
  /* overflow: hidden; */
  overflow: visible;
}

.fixed-editor-toolbar.collapsed {
  max-height: 0;
  margin: 0;
  border: none;
  box-shadow: none;
  opacity: 0;
  transform: translateY(-10px);
}

.editor-toolbar {
  border: none !important;
  background-color: transparent !important;
  padding: 8px 16px;
}

/* 移除所有编辑器边框 */
:deep(.w-e-toolbar) {
  border: none !important;
}

:deep(.w-e-text-container),
:deep(.w-e-scroll) {
  border: none !important;
}
</style>
