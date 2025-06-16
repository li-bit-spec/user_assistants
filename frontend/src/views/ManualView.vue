<template>
  <div class="p-4 bg-white rounded shadow">
    <div class="flex items-center mb-4">
      
      <el-button
        type="primary"
        size="small"
        class="mr-2"
        @click="isEditing = !isEditing"
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
      width="70%"
      @close="closeNewArticleDialog"
    >
      <el-form :model="newArticle" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="newArticle.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="内容" class="h-[500px]">
          <QuillEditor
            v-model:content="newArticle.content"
            :options="editorOptions"
            contentType="html"
            class="h-[450px]"
            toolbar="full"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeNewArticleDialog">取消</el-button>
          <el-button type="primary" @click="createArticle">确定</el-button>
        </span>
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
    <div v-if="!isEditing && currentArticle?.content" v-html="currentArticle.content" class="prose max-w-none min-h-[300px] p-4 bg-gray-50 rounded"></div>
    <div v-else-if="isEditing" class="edit-area flex flex-col min-h-[400px]" style="height: 60vh;">
      <el-form-item>
        <el-input
          v-model="currentArticle.title"
          placeholder="请输入标题"
          class="mb-4"
        />
      </el-form-item>
      <el-form-item class="flex-1 flex flex-col">
        <QuillEditor
          v-model:content="editingContent"
          :options="editorOptions"
          contentType="html"
          class="h-full"
          toolbar="full"
          @ready="onEditorReady"
        />
      </el-form-item>
    </div>
    <div v-else class="prose max-w-none min-h-[300px] p-4 bg-gray-50 rounded flex items-center justify-center text-gray-500">
      暂无文章内容
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as manualApi from '@/api/manual'
import * as uploadApi from '@/api/upload'
import { useManualStore } from '@/store'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const route = useRoute()
const router = useRouter()
const store = useManualStore()
const isEditing = ref(false)
const drawerVisible = ref(false)
const dialogVisible = ref(false)
const loading = ref(false)
const editingContent = ref('')
const articles = ref([])
const currentArticle = ref({ id: null, title: '', content: '' })
const newArticle = ref({ title: '', content: '' })
const deleteDialogVisible = ref(false)

// 添加 Quill 编辑器配置
const editorOptions = {
  theme: 'snow',
  modules: {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'direction': 'rtl' }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        ['clean'],
        ['link', 'image']
      ],
      handlers: {
        image: function() {
          console.log('图片上传按钮被点击')
          const input = document.createElement('input')
          input.setAttribute('type', 'file')
          input.setAttribute('accept', 'image/*')
          input.click()

          input.onchange = async () => {
            const file = input.files[0]
            console.log('选择的文件:', file)
            if (file) {
              try {
                console.log('开始上传文件:', file.name, '大小:', file.size, '类型:', file.type)
                const res = await uploadApi.uploadFile(file)
                console.log('上传响应:', res)
                if (res.code === 0) {
                  console.log('上传成功，准备插入图片:', res.data)
                  const quill = this.quill
                  console.log('Quill 实例:', quill)
                  const range = quill.getSelection(true)
                  console.log('当前选区:', range)
                  quill.insertEmbed(range.index, 'image', res.data)
                  console.log('图片插入完成')
                } else {
                  console.error('上传失败:', res.message)
                  ElMessage.error(res.message || '上传失败')
                }
              } catch (error) {
                console.error('上传过程发生错误:', error)
                ElMessage.error(error.message || '图片上传失败')
              }
            }
          }
        }
      }
    }
  }
}

// 添加编辑器实例引用
const quillEditor = ref(null)

// 监听编辑器实例创建
const onEditorReady = (quill) => {
  console.log('编辑器实例已创建:', quill)
  quillEditor.value = quill
}

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
      } else {
        // 如果找不到对应id的文章，显示第一篇文章或清空
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
      // 如果没有id参数，显示第一篇文章
      currentArticle.value = articles.value[0]
      editingContent.value = articles.value[0].content
      router.push(`/manual/${articles.value[0].id}`)
    } else {
      // 如果没有文章，清空当前显示
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
      // 如果没有id参数，显示第一篇文章
      currentArticle.value = articles.value[0]
      editingContent.value = currentArticle.value.content
    }
  },
  { immediate: true }
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
}

const closeNewArticleDialog = () => {
  dialogVisible.value = false
  store.setShowNewArticleDialog(false)
}

const handleArticleSelect = async (id) => {
  // 切换路由，自动触发watch加载文章
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
    // 获取新创建的文章ID
    const newArticleId = res.data?.id
    // 重新获取文章列表
    await fetchArticles()
    // 如果有新文章ID，则跳转到新文章
    if (newArticleId) {
      router.push(`/manual/${newArticleId}`)
    } else if (articles.value.length > 0) {
      // 如果没有获取到新文章ID，则显示第一篇文章
      router.push(`/manual/${articles.value[0].id}`)
    }
    // 清除 query.action
    if (route.query.action === 'new') {
      router.replace({ path: '/manual' })
    }
    // 刷新导航栏
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
    isEditing.value = false
    ElMessage.success('保存成功')
    await fetchArticles()
  } catch (error) {
    ElMessage.error('保存失败')
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
    
    // 先清空当前文章
    currentArticle.value = { id: null, title: '', content: '' }
    editingContent.value = ''
    
    // 重新获取文章列表
    const res = await manualApi.fetchManualList()
    articles.value = res.data || []
    
    // 通知layout刷新文章列表
    await store.fetchArticles()
    
    // 如果还有文章，显示第一篇文章
    if (articles.value.length > 0) {
      const firstArticle = articles.value[0]
      currentArticle.value = firstArticle
      editingContent.value = firstArticle.content
      router.push(`/manual/${firstArticle.id}`)
    } else {
      // 如果没有文章了，停留在当前页面
      router.push('/manual')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error(error.response?.data?.message || '删除失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:deep(.el-drawer__body) {
  padding: 0;
}

:deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
}

.edit-area {
  min-height: 400px;
  height: 60vh;
  display: flex;
  flex-direction: column;
  width: 100%;
}

:deep(.el-form-item.flex-1) {
  flex: 1 1 0%;
  display: flex;
  flex-direction: column;
  width: 100%;
}

:deep(.el-form-item.flex-1 .el-form-item__content) {
  width: 100%;
}

:deep(.ql-container) {
  height: calc(100% - 42px);
  font-size: 16px;
  width: 100%;
}

:deep(.ql-editor) {
  min-height: 300px;
  height: 100%;
  width: 100%;
}

:deep(.ql-toolbar) {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  width: 100%;
}

:deep(.ql-container) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form-item.h-\[500px\]) {
  height: 500px;
  margin-bottom: 0;
  width: 100%;
}

:deep(.el-form-item.h-\[500px\] .el-form-item__content) {
  height: 100%;
  width: 100%;
}

:deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  max-width: 90vw;
}

:deep(.el-dialog__body) {
  flex: 1;
  overflow: auto;
}

.flex.items-center.mb-4 {
  align-items: center;
  gap: 0.5rem;
}
.text-xl.font-bold {
  margin-bottom: 0;
}
</style>
