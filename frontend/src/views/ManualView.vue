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
      <el-button
        type="danger"
        size="small"
        @click="handleDelete"
        :disabled="!currentArticle?.id"
      >
        删除
      </el-button>
      <h2 class="text-xl font-bold mr-6">{{ currentArticle?.title || '文章内容' }}</h2>
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
    <div class="content-area">
      <el-input
        v-if="isEditing"
        v-model="currentArticle.title"
        placeholder="请输入标题"
        class="mb-4"
      />
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
import { ref, shallowRef, onMounted, watch, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as manualApi from '@/api/manual'
import { useManualStore } from '@/store'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/upload'

const route = useRoute()
const router = useRouter()
const store = useManualStore()
const isEditing = ref(false)
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
const dialogEditorRef = shallowRef() // 新增对话框编辑器实例

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
  editorRef.value = editor // 记录 editor 实例
  if (!isEditing.value) {
    editor.disable()
  }
}

// 对话框编辑器回调函数
const handleDialogEditorCreated = (editor) => {
  dialogEditorRef.value = editor
}

const handleDialogEditorChange = (editor) => {
  newArticle.value.content = editor.getHtml()
}

const handleChange = (editor) => {
  if (isEditing.value) {
    editingContent.value = editor.getHtml()
  } else {
    newArticle.value.content = editor.getHtml()
  }
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

// 保存内容
const saveContent = async () => {
  if (!currentArticle.value.title) {
    ElMessage.warning('请输入标题')
    return
  }
  loading.value = true
  try {
    await manualApi.updateManual({
      ...currentArticle.value,
      content: editingContent.value
    })
    currentArticle.value.content = editingContent.value
    toggleEdit() // 保存后切换到禁用状态
    ElMessage.success('保存成功')
    await fetchArticles()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
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
    
    // 如果当前路由有id参数，优先加载该文章
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
        if (!isEditing.value) {
          editor.disable()
        }
      }
    })
  } catch (error) {
    ElMessage.error('获取文章失败')
  } finally {
    loading.value = false
  }
}

// 监听路由参数id变化，自动加载对应文章
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      fetchArticleById(newId)
    } else if (articles.value.length > 0) {
      currentArticle.value = articles.value[0]
      editingContent.value = currentArticle.value.content
      nextTick(() => {
        const editor = editorRef.value
        if (editor) {
          editor.setHtml(currentArticle.value.content)
          if (!isEditing.value) {
            editor.disable()
          }
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

const closeNewArticleDialog = () => {
  dialogVisible.value = false
  store.setShowNewArticleDialog(false)
  if (dialogEditorRef.value) {
    dialogEditorRef.value.setHtml('')
  }
}

const handleArticleSelect = async (id) => {
  router.push(`/manual/${id}`)
  drawerVisible.value = false
}

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

const handleDelete = () => {
  if (!currentArticle.value?.id) {
    ElMessage.warning('请先选择要删除的文章')
    return
  }
  deleteDialogVisible.value = true
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

defineExpose({
  showNewArticleDialog
})
</script>

<style scoped>
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
</style>
