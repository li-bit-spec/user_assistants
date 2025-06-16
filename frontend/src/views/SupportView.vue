<template>
  <div class="p-4 bg-white rounded shadow">
    <div class="flex items-center mb-4">
      <el-button
        type="primary"
        size="small"
        class="mr-2"
        @click="toggleEdit"
      >
        {{ isEditing ? '取消' : '编辑' }}
      </el-button>
      <el-button
        v-if="isEditing"
        type="success"
        size="small"
        class="mr-2"
        @click="saveContent"
      >
        保存
      </el-button>
      <h2 class="text-xl font-bold mr-6">技术支持</h2>
    </div>

    <!-- 内容展示/编辑区域 -->
    <div class="content-area">
      <div style="border: 1px solid #ccc; margin-top: 10px">
        <Toolbar
          :editor="editorRef"
          :defaultConfig="toolbarConfig"
          :mode="mode"
          style="border-bottom: 1px solid #ccc"
          :class="{ 'hidden': !isEditing }"
        />
        <Editor
          :defaultConfig="editorConfig"
          :mode="mode"
          v-model="editingContent"
          style="height: 400px; overflow-y: hidden"
          @onCreated="handleCreated"
          @onChange="handleChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as supportApi from '@/api/support'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/upload'

const isEditing = ref(false)
const content = ref('')
const editingContent = ref('')
const editorRef = shallowRef()

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
  editingContent.value = editor.getHtml()
}

// 切换编辑状态
const toggleEdit = () => {
  isEditing.value = !isEditing.value
  const editor = editorRef.value
  if (editor) {
    if (isEditing.value) {
      editor.enable()
    } else {
      editor.disable()
    }
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
    toggleEdit() // 保存后切换到禁用状态
    ElMessage.success('保存成功')
    await fetchContent()
  } catch (error) {
    console.error('保存失败:', error) // 添加错误日志
    ElMessage.error('保存失败')
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
</script>

<style scoped>
.content-area {
  margin-top: 20px;
}

:deep(.el-button) {
  margin-right: 8px;
}

:deep(.ProseMirror) {
  min-height: 300px;
  outline: none;
  height: 100%;
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

.hidden {
  display: none;
}
</style>
