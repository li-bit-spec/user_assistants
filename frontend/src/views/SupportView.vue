<template>
  <div class="support-container">
    <!-- 内容展示/编辑区域 -->
    <div class="support-content">
      <!-- 固定的编辑器工具栏（编辑模式下显示） -->
      <div v-if="isEditing" class="fixed-editor-toolbar">
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
          :defaultConfig="editorConfig"
          :mode="mode"
          v-model="editingContent"
          @onCreated="handleCreated"
          @onChange="handleChange"
        />
      </div>
    </div>

    <!-- 文章目录 -->
    <ArticleOutline 
      v-if="!isEditing && content"
      :content="content"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as supportApi from '@/api/support'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/upload'
import ArticleOutline from '@/components/ArticleOutline.vue'
import { Edit, Check, Close } from '@element-plus/icons-vue'

const isEditing = ref(false)
const content = ref('')
const editingContent = ref('')
const editorRef = shallowRef()
const loading = ref(false)
const toolbarKey = ref(0) // 用于强制重新渲染工具栏

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
    }
  }
}

let supportId = null

// 编辑器回调函数
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例
  if (!isEditing.value) {
    editor.disable()
  }
  // 如果已经有内容，则设置内容
  if (content.value) {
    editor.setHtml(content.value)
  }
}

const handleChange = (editor) => {
  if (isEditing.value) {
    editingContent.value = editor.getHtml()
  }
}

// 进入编辑模式
const enterEditMode = () => {
  if (!isEditing.value) {
    isEditing.value = true
    // 进入编辑模式时，强制刷新工具栏
    toolbarKey.value = Date.now()
    
    const editor = editorRef.value
    if (editor) {
      editor.enable()
      editingContent.value = content.value
      nextTick(() => {
        editor.setHtml(content.value)
        setTimeout(() => {
          editor.focus()
        }, 100)
      })
    }
  }
}

// 退出编辑模式
const exitEditMode = () => {
  if (isEditing.value) {
    isEditing.value = false
    
    const editor = editorRef.value
    if (editor) {
      editor.disable()
      editingContent.value = content.value
      nextTick(() => {
        editor.setHtml(content.value)
      })
    }
  }
}

// 新的操作函数
const handleEdit = () => {
  enterEditMode()
}

const handleSave = async () => {
  try {
    await saveContent()
  } catch (error) {
    console.error('保存失败:', error)
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

const fetchContent = async () => {
  try {
    const res = await supportApi.fetchSupportList()
    console.log('Support content response:', res) // 添加调试日志
    if (res.data && res.data.length > 0) {
      const supportData = res.data[0]
      content.value = supportData.content || ''
      editingContent.value = supportData.content || ''
      supportId = supportData.id
      
      // 设置编辑器内容
      nextTick(() => {
        const editor = editorRef.value
        if (editor) {
          editor.setHtml(content.value)
          if (!isEditing.value) {
            editor.disable()
          }
        }
      })
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
      nextTick(() => {
        const editor = editorRef.value
        if (editor) {
          editor.setHtml(content.value)
          if (!isEditing.value) {
            editor.disable()
          }
        }
      })
    }
  } catch (error) {
    console.error('获取内容失败:', error) // 添加错误日志
    ElMessage.error('获取内容失败')
    // 设置默认内容
    content.value = '<p>欢迎使用技术支持</p>'
    editingContent.value = content.value
    nextTick(() => {
      const editor = editorRef.value
      if (editor) {
        editor.setHtml(content.value)
        if (!isEditing.value) {
          editor.disable()
        }
      }
    })
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
    console.error('保存失败:', error) // 添加错误日志
    ElMessage.error('保存失败')
    throw error
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchContent()
})

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})

// 导出方法供父组件调用
defineExpose({
  handleEdit,
  handleSave,
  handleCancel
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
.support-content:has(.fixed-editor-toolbar) {
  padding-top: 150px;
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
}

:deep(.ProseMirror table td),
:deep(.ProseMirror table th) {
  border: 1px solid #dcdfe6;
  padding: 8px;
  min-width: 100px;
}

:deep(.ProseMirror table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.ProseMirror table tr:hover) {
  background-color: #f5f7fa;
}

:deep(.ProseMirror .image-resizer) {
  display: none;
}

:deep(.ProseMirror .image-resizer .resize-handle) {
  display: none;
}

/* 固定编辑器工具栏样式 */
.fixed-editor-toolbar {
  position: fixed;
  top: 43px; /* 紧贴顶部导航栏 */
  left: 20px; /* content-body padding 24px + support-content padding 16px */
  right: 324px; /* content-body padding 24px + support-content padding-right 300px */
  z-index: 40;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 16px 0;
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
