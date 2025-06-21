<template>
  <div class="manual-container">
    <div class="manual-header">
      <template v-if="isEditing">
        <el-button
          type="info"
          size="small"
          class="mr-2"
          @click="toggleEdit"
        >
          取消
        </el-button>
        <el-button
          type="success"
          size="small"
          class="mr-2"
          @click="saveContent"
        >
          保存
        </el-button>
      </template>
    </div>

    <!-- 文章列表抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="文章列表"
      size="300px"
      :with-header="false"
    >
      <div class="p-4">
        <el-button
          type="primary"
          class="w-full mb-4"
          @click="showNewArticleDialog"
        >
          新增文章
        </el-button>
        <el-menu
          :default-active="currentArticle?.id?.toString()"
          @select="handleArticleSelect"
        >
          <el-menu-item
            v-for="article in articles"
            :key="article.id"
            :index="article.id.toString()"
            class="hover:bg-gray-100"
          >
            {{ article.title }}
          </el-menu-item>
        </el-menu>
      </div>
    </el-drawer>

    <!-- 新增文章对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增文章"
      width="80%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="closeNewArticleDialog"
      class="manual-dialog"
      :fullscreen="true"
    >
      <el-form :model="newArticle" label-width="80px" class="manual-form">
        <el-form-item label="标题" class="mb-4">
          <el-input 
            v-model="newArticle.title" 
            placeholder="请输入文章标题"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" class="editor-form-item">
          <div class="editor-container">
            <Toolbar
              :editor="dialogEditorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
              style="border-bottom: 1px solid #dcdfe6"
            />
            <Editor
              :defaultConfig="editorConfig"
              :mode="mode"
              v-model="newArticle.content"
              style="height: 100%"
              @onCreated="handleDialogEditorCreated"
              @onChange="handleDialogEditorChange"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeNewArticleDialog">取 消</el-button>
          <el-button type="primary" @click="createArticle" :loading="loading">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="30%"
    >
      <span>确定要删除这篇文章吗？此操作不可恢复。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 内容展示/编辑区域 -->
    <div class="manual-content">
      <!-- 顶部固定工具栏（编辑模式下始终显示） -->
      <div v-if="currentArticle.id" class="article-toolbar">
        <div class="toolbar-container">
          <!-- 编辑器工具栏（编辑模式） -->
          <div v-if="isEditing" class="editor-toolbar-section">
            <Toolbar
              :key="'toolbar-' + toolbarKey"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
              class="inline-editor-toolbar"
            />
          </div>
          
          <!-- 操作按钮 -->
          <div class="toolbar-actions">
            <template v-if="!isEditing">
              <!-- 查看模式操作按钮 -->
              <el-button 
                type="primary" 
                :icon="Edit" 
                @click="handleEdit"
                class="action-btn"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                :icon="Delete" 
                @click="handleDelete"
                class="action-btn"
                plain
              >
                删除
              </el-button>
            </template>
            <template v-else>
              <!-- 编辑模式操作按钮 -->
              <el-button 
                type="success" 
                :icon="Check" 
                @click="handleSave"
                :loading="loading"
                class="action-btn"
              >
                保存
              </el-button>
              <el-button 
                :icon="Close" 
                @click="handleCancel"
                class="action-btn"
                plain
              >
                取消
              </el-button>
            </template>
          </div>
        </div>
      </div>

      <!-- 文章标题区域（移到工具栏下方） -->
      <div v-if="currentArticle.id" class="article-title-section">
        <el-input
          v-if="isEditing"
          v-model="currentArticle.title"
          placeholder="请输入标题"
          class="title-input"
          size="large"
        />
        <h1 v-else class="article-title">{{ currentArticle.title }}</h1>
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
      v-if="!isEditing && currentArticle.content"
      :content="currentArticle.content"
    />

    <!-- 浮动操作按钮（备选方案，当前已注释） -->
    <!-- 
    <div v-if="currentArticle.id" class="floating-actions">
      <el-button-group v-if="!isEditing" class="floating-group">
        <el-button 
          type="primary" 
          :icon="Edit" 
          @click="handleEdit"
          class="floating-btn"
          circle
          size="large"
        />
        <el-button 
          type="danger" 
          :icon="Delete" 
          @click="handleDelete"
          class="floating-btn"
          circle
          size="large"
        />
      </el-button-group>
      
      <el-button-group v-else class="floating-group">
        <el-button 
          type="success" 
          :icon="Check" 
          @click="handleSave"
          :loading="loading"
          class="floating-btn"
          circle
          size="large"
        />
        <el-button 
          type="info" 
          :icon="Close" 
          @click="handleCancel"
          class="floating-btn"
          circle
          size="large"
        />
      </el-button-group>
    </div>
    -->
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onMounted, watch, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as manualApi from '@/api/manual'
import { useManualStore } from '@/store'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/upload'
import ArticleOutline from '@/components/ArticleOutline.vue'
import { Edit, Delete, Check, Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useManualStore()
const isEditing = ref(false)
const toolbarKey = ref(0) // 用于强制重新渲染工具栏

const drawerVisible = ref(false)
const dialogVisible = ref(false)
const loading = ref(false)
const articles = ref([])
const currentArticle = ref({ id: null, title: '', content: '' })
const newArticle = ref({ title: '', content: '' })
const deleteDialogVisible = ref(false)
const editingContent = ref('')


// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
const dialogEditorRef = shallowRef()

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

// 编辑器回调函数
const handleCreated = (editor) => {
  editorRef.value = editor
  if (!isEditing.value) {
    editor.disable()
  }
  // 设置初始内容
  nextTick(() => {
    editor.setHtml(currentArticle.value.content)
  })
}

const handleDialogEditorCreated = (editor) => {
  dialogEditorRef.value = editor
}

const handleDialogEditorChange = (editor) => {
  newArticle.value.content = editor.getHtml()
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
    // 进入编辑模式时，强制刷新工具栏（使用时间戳确保唯一性）
    toolbarKey.value = Date.now()
    
    const editor = editorRef.value
    if (editor) {
      // 进入编辑模式
      editor.enable()
      editingContent.value = currentArticle.value.content
      nextTick(() => {
        editor.setHtml(currentArticle.value.content)
        // 确保工具栏能正确关联到编辑器
        setTimeout(() => {
          editor.focus()
        }, 100)
      })
    }
  }
}

// 保持向后兼容的toggleEdit函数
const toggleEdit = () => {
  if (isEditing.value) {
    // 如果正在编辑，则退出编辑模式（取消编辑）
    handleCancel()
  } else {
    // 如果不在编辑，则进入编辑模式
    enterEditMode()
  }
}

// 保存内容
const saveContent = async () => {
  if (!currentArticle.value.title) {
    ElMessage.warning('请输入标题')
    throw new Error('标题不能为空')
  }
  loading.value = true
  try {
    await manualApi.updateManual({
      ...currentArticle.value,
      content: editingContent.value
    })
    currentArticle.value.content = editingContent.value
    ElMessage.success('保存成功')
    
    // 保存成功后退出编辑模式
    exitEditMode()
    
    await fetchArticles()
    return true
  } catch (error) {
    ElMessage.error('保存失败')
    throw error
  } finally {
    loading.value = false
  }
}

// 显示新增文章对话框
const showNewArticleDialog = () => {
  newArticle.value = { title: '', content: '' }
  dialogVisible.value = true
  drawerVisible.value = false
  nextTick(() => {
    if (dialogEditorRef.value) {
      dialogEditorRef.value.setHtml('')
    }
  })
}

// 处理删除操作
const handleDelete = () => {
  if (!currentArticle.value?.id) {
    ElMessage.warning('请先选择要删除的文章')
    return
  }
  deleteDialogVisible.value = true
}

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
  const dialogEditor = dialogEditorRef.value
  if (dialogEditor) {
    dialogEditor.destroy()
  }
})

const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await manualApi.fetchManualList()
    articles.value = res.data || []
    
    if (route.params.id) {
      const article = articles.value.find(a => a.id.toString() === route.params.id)
      if (article) {
        currentArticle.value = article
        editingContent.value = article.content
        nextTick(() => {
          const editor = editorRef.value
          if (editor) {
            editor.setHtml(article.content)
            if (!isEditing.value) {
              editor.disable()
            }
          }
        })
      } else {
        if (articles.value.length > 0) {
          currentArticle.value = articles.value[0]
          editingContent.value = articles.value[0].content
          router.push(`/manual/${articles.value[0].id}`)
        } else {
          currentArticle.value = { id: null, title: '', content: '' }
          editingContent.value = ''
          router.push('/manual')
        }
      }
    } else if (articles.value.length > 0) {
      currentArticle.value = articles.value[0]
      editingContent.value = currentArticle.value.content
      router.push(`/manual/${articles.value[0].id}`)
    } else {
      currentArticle.value = { id: null, title: '', content: '' }
      editingContent.value = ''
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const fetchArticleById = async (id) => {
  loading.value = true
  try {
    const res = await manualApi.fetchManualById(id)
    currentArticle.value = res.data
    editingContent.value = res.data.content
    nextTick(() => {
      const editor = editorRef.value
      if (editor) {
        editor.setHtml(res.data.content)
        editor.disable() // 确保获取文章后处于查看模式
      }
    })
  } catch (error) {
    ElMessage.error('获取文章失败')
  } finally {
    loading.value = false
  }
}

// 监听路由参数id变化
watch(
  () => route.params.id,
  (newId) => {
    // 路由变化时重置编辑状态和工具栏
    isEditing.value = false
    toolbarKey.value++
    
    if (newId) {
      fetchArticleById(newId)
    } else if (articles.value.length > 0) {
      currentArticle.value = articles.value[0]
      editingContent.value = currentArticle.value.content
      nextTick(() => {
        const editor = editorRef.value
        if (editor) {
          editor.setHtml(currentArticle.value.content)
          editor.disable() // 确保新文章加载时处于查看模式
        }
      })
    }
  }
)

// 监听store中的showNewArticleDialog状态
watch(
  () => store.showNewArticleDialog,
  (show) => {
    if (show) {
      showNewArticleDialog()
    }
  }
)

onMounted(() => {
  fetchArticles()
})

const createArticle = async () => {
  if (!newArticle.value.title || !newArticle.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await manualApi.addManual({
      title: newArticle.value.title,
      content: newArticle.value.content
    })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    const newArticleId = res.data?.id
    await fetchArticles()
    if (newArticleId) {
      router.push(`/manual/${newArticleId}`)
    } else if (articles.value.length > 0) {
      router.push(`/manual/${articles.value[0].id}`)
    }
    if (route.query.action === 'new') {
      router.replace({ path: '/manual' })
    }
    drawerVisible.value = true
    setTimeout(() => {
      drawerVisible.value = false
    }, 100)
  } catch (error) {
    ElMessage.error('创建失败')
  } finally {
    loading.value = false
  }
}

const confirmDelete = async () => {
  if (!currentArticle.value?.id) return
  loading.value = true
  try {
    await manualApi.deleteManual(currentArticle.value.id)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    
    currentArticle.value = { id: null, title: '', content: '' }
    editingContent.value = ''
    
    const res = await manualApi.fetchManualList()
    articles.value = res.data || []
    
    await store.fetchArticles()
    
    if (articles.value.length > 0) {
      const firstArticle = articles.value[0]
      currentArticle.value = firstArticle
      editingContent.value = firstArticle.content
      router.push(`/manual/${firstArticle.id}`)
    } else {
      router.push('/manual')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error(error.response?.data?.message || '删除失败')
  } finally {
    loading.value = false
  }
}

// 监听路由参数 action=new 时自动弹出新增文章对话框
watch(
  () => route.query.action,
  (action) => {
    if (action === 'new') {
      dialogVisible.value = true
    }
  },
  { immediate: true }
)

const handleArticleSelect = async (id) => {
  router.push(`/manual/${id}`)
  drawerVisible.value = false
}

const closeNewArticleDialog = () => {
  dialogVisible.value = false
  store.setShowNewArticleDialog(false)
  if (dialogEditorRef.value) {
    dialogEditorRef.value.setHtml('')
  }
}



// 新的操作函数
const handleEdit = () => {
  // 直接进入编辑模式
  enterEditMode()
}

const handleSave = async () => {
  try {
    await saveContent()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const exitEditMode = () => {
  if (isEditing.value) {
    isEditing.value = false
    
    const editor = editorRef.value
    if (editor) {
      editor.disable()
      editingContent.value = currentArticle.value.content
      nextTick(() => {
        editor.setHtml(currentArticle.value.content)
      })
    }
  }
}

const handleCancel = () => {
  // 取消编辑，直接退出编辑模式
  if (isEditing.value) {
    // 恢复原始内容，丢弃未保存的修改
    editingContent.value = currentArticle.value.content
    exitEditMode()
  }
}

// 组件方法导出
defineExpose({
  showNewArticleDialog,
  toggleEdit,
  handleDelete,
  saveContent
})
</script>

<style scoped>
.manual-container {
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

.manual-header {
  padding: 16px;
  display: flex;
  align-items: center;
  min-height: 60px;
}

.manual-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: visible;
  padding: 0 16px 16px;
  padding-right: 300px; /* 为目录留出空间 */
  min-height: calc(100% - 60px);
  position: relative;
}

/* 当工具栏存在时，为内容区域添加顶部填充 */
.manual-content:has(.article-toolbar) {
  padding-top: 70px; /* 为固定工具栏留出空间，与工具栏高度匹配 */
}

.editor-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: none;
  min-height: 500px; /* 设置最小高度确保编辑器高度足够 */
  height: calc(100vh - 220px); /* 减去导航栏、工具栏等高度 */
  overflow: visible;
}

:deep(.w-e-text-container) {
  flex: 1;
  overflow-y: visible;
  height: 100% !important;
  min-height: 400px !important; /* 确保最小高度满足编辑器要求 */
  border-left: none !important;
}

:deep(.w-e-scroll) {
  height: 100% !important;
  min-height: 400px !important; /* 确保最小高度满足编辑器要求 */
  overflow: visible !important;
}

:deep(.ProseMirror) {
  height: 100% !important;
  min-height: 400px !important; /* 确保最小高度满足编辑器要求 */
  padding: 16px;
  box-sizing: border-box;
  overflow: visible;

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

/* 移除所有编辑器边框 */
:deep(.w-e-toolbar) {
  border: none !important;
}

:deep(.w-e-text-container),
:deep(.w-e-scroll) {
  border: none !important;
}

:deep(.el-drawer__body) {
  padding: 0;
}

:deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
}

.editor-toolbar {
  padding: 8px;
  border-bottom: 1px solid #dcdfe6;
  background-color: #f5f7fa;
  display: flex;
  gap: 8px;
}

.editor-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
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

.is-active {
  background-color: #409EFF;
  color: white;
}

.flex.items-center.mb-4 {
  align-items: center;
  gap: 0.5rem;
}

.text-xl.font-bold {
  margin-bottom: 0;
}

.hidden {
  display: none;
}

/* 新增文章弹窗样式 */
:deep(.manual-dialog) {
  display: flex;
  flex-direction: column;

  .el-dialog {
    margin: 0 !important;
    display: flex;
    flex-direction: column;
    max-height: 100vh;
  }

  .el-dialog__header {
    padding: 20px;
    margin: 0;
    border-bottom: 1px solid #dcdfe6;
  }

  .el-dialog__body {
    flex: 1;
    padding: 20px;
    margin: 0;
    overflow-y: auto;
  }

  .el-dialog__footer {
    padding: 10px 20px;
    margin: 0;
    border-top: 1px solid #dcdfe6;
  }

  .manual-form {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .editor-form-item {
    flex: 1;
    margin-bottom: 0;
    height: calc(100vh - 300px);
    min-height: 400px;

    .el-form-item__content {
      height: 100%;
      margin-left: 0 !important;
      width: calc(100% - 80px);
      margin-right: 0;
    }
  }

  .editor-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    width: 100%;
  }

  .editor-content {
    flex: 1;
    overflow-y: auto;
  }

  :deep(.w-e-text-container) {
    flex: 1;
  }

  :deep(.w-e-scroll) {
    height: 100% !important;
    min-height: 0 !important;
  }
}

.dialog-footer {
  text-align: right;
}

/* 修复表单项样式 */
:deep(.el-form-item) {
  margin-bottom: 20px;

  .el-form-item__content {
    margin-left: 0 !important;
    width: calc(100% - 80px);
    margin-right: 0;
  }
}

:deep(.el-form-item__label) {
  padding: 0;
  line-height: 32px;
}

:deep(.el-input) {
  width: 100%;
}

.article-toolbar {
  position: fixed;
  top: 60px; /* 导航栏60px + 10px间距，紧贴导航栏 */
  left: 25px; /* content-body padding 24px + manual-content padding 16px */
  right: 324px; /* content-body padding 24px + manual-content padding-right 300px */
  z-index: 50;
  padding: 12px 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-bottom: 2px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-radius: 8px;
  margin-bottom: 16px;
}

.toolbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.editor-toolbar-section {
  flex: 1;
  display: flex;
  align-items: center;
}

.article-title-section {
  padding: 16px 24px;
  margin-bottom: 16px;
}



.article-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
}

.title-input {
  flex: 1;
  max-width: 600px;
}

:deep(.title-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #409eff;
  border-radius: 8px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.action-btn.el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
  border: none;
}

.action-btn.el-button--success {
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
  border: none;
}

.action-btn.el-button--danger {
  color: #f56c6c;
  border-color: #f56c6c;
}

.action-btn.el-button--danger:hover {
  background-color: #f56c6c;
  color: white;
}

/* 内联编辑器工具栏样式 */
.inline-editor-toolbar {
  border: 1px solid #e4e7ed !important;
  border-radius: 6px;
  background-color: #ffffff;
  padding: 4px 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}



/* 浮动操作按钮样式（备选方案） */
.floating-actions {
  position: fixed;
  bottom: 80px;
  right: 40px;
  z-index: 200;
}

.floating-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-radius: 50px;
  background: white;
  padding: 8px;
}

.floating-btn {
  width: 56px !important;
  height: 56px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.floating-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
}
</style>
