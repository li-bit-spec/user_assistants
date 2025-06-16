<template>
  <div class="p-4 bg-white rounded shadow">
    <!-- 顶部反馈输入区域 -->
    <div class="mb-4 pb-4 border-b border-gray-200">
      <el-input
        v-model="feedbackContent"
        type="textarea"
        :rows="4"
        placeholder="请输入反馈内容"
        class="mb-4"
      />
      
      <div class="flex items-center">
        <el-upload
          v-model:file-list="uploadFileList"
          class="mr-4"
          action="/api/upload"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
          :limit="10"
          accept="image/jpeg,image/png"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>

        <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
      </div>
    </div>

    <!-- 底部反馈列表 -->
    <el-table :data="feedbackList" style="width: 100%" v-loading="loading">
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="created_at" label="时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleView(row)"
          >
            详情
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="30%"
    >
      <span>确定要删除这条反馈吗？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="反馈详情"
      width="600px"
      class="feedback-detail-dialog"
      :close-on-click-modal="false"
    >
      <div v-if="currentDetailItem" class="flex gap-8 p-6 items-start">
        <!-- 左侧内容 -->
        <div class="flex-1 min-w-0">
          <div class="text-base font-medium mb-2 text-gray-800">反馈内容：</div>
          <div class="text-gray-500 bg-gray-50 p-4 rounded-lg border border-gray-100 break-words">{{ currentDetailItem.content }}</div>
        </div>
        <!-- 右侧图片 -->
        <div class="w-48">
          <div class="text-base font-medium mb-2 text-gray-700">图片列表：</div>
          <div v-if="currentDetailItem.imageUrls && currentDetailItem.imageUrls.length > 0"
               class="flex flex-wrap gap-2">
            <el-image
              v-for="(url, index) in currentDetailItem.imageUrls"
              :key="index"
              :src="url"
              :preview-src-list="currentDetailItem.imageUrls"
              fit="cover"
              style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px; border: 1px solid #e5e7eb;"
              :initial-index="index"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
          <div v-else class="text-gray-400 bg-gray-50 p-4 rounded-lg text-center border border-gray-100">
            暂无图片
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as feedbackApi from '@/api/feedback'

const feedbackContent = ref('')
const feedbackImages = ref([])
const uploadFileList = ref([])
const feedbackList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const deleteDialogVisible = ref(false)
const currentDeleteItem = ref(null)
const detailDialogVisible = ref(false)
const currentDetailItem = ref(null)

onMounted(() => {
  fetchFeedbackList()
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const fetchFeedbackList = async () => {
  loading.value = true
  try {
    console.log('开始获取反馈列表，页码：', currentPage.value)
    const res = await feedbackApi.fetchFeedbackPage({ pageNum: currentPage.value, pageSize: pageSize.value })
    console.log('获取反馈列表响应：', res)
    if (res.code === 0 && res.data) {
      feedbackList.value = res.data.list || []
      total.value = res.data.total || 0
      console.log('设置反馈列表数据：', feedbackList.value)
    } else {
      console.error('获取反馈列表失败：', res)
      ElMessage.error('获取反馈列表失败')
    }
  } catch (error) {
    console.error('获取反馈列表异常：', error)
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 0 && response.data && response.data.code === 0) {
    console.log('图片上传成功，完整响应：', response)
    feedbackImages.value.push(response.data.data)
    console.log('当前图片列表：', feedbackImages.value)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const submitFeedback = async () => {
  if (!feedbackContent.value) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  loading.value = true
  try {
    console.log('提交反馈，图片数量：', feedbackImages.value.length)
    console.log('图片URL列表：', feedbackImages.value)
    const requestData = {
      content: feedbackContent.value,
      imageUrls: [...feedbackImages.value]
    }
    console.log('发送到后端的数据：', JSON.stringify(requestData))
    const res = await feedbackApi.addFeedback(requestData)
    ElMessage.success('提交成功')
    feedbackContent.value = ''
    feedbackImages.value = []
    uploadFileList.value = []
    await fetchFeedbackList()
  } catch (error) {
    console.error('提交反馈失败：', error)
    ElMessage.error('提交失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  currentDeleteItem.value = row
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!currentDeleteItem.value) return
  loading.value = true
  try {
    await feedbackApi.deleteFeedback(currentDeleteItem.value.id)
    ElMessage.success('删除成功')
    await fetchFeedbackList()
  } catch (error) {
    ElMessage.error('删除失败')
  } finally {
    loading.value = false
    deleteDialogVisible.value = false
  }
}

const handlePageChange = async (page) => {
  currentPage.value = page
  await fetchFeedbackList()
}

const handleView = (row) => {
  console.log('查看详情，行数据：', row)
  // 处理图片URL，添加基础路径
  if (row.imageUrls && row.imageUrls.length > 0) {
    row.imageUrls = row.imageUrls.map(url => {
      // 如果URL不是以http开头，添加基础URL
      if (!url.startsWith('http')) {
        // 确保URL以/api开头
        return url.startsWith('/api') ? url : `/api${url}`
      }
      return url
    })
  }
  console.log('处理后的图片URL列表：', row.imageUrls)
  currentDetailItem.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped>
.feedback-detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.feedback-detail-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.feedback-detail-dialog :deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  font-size: 12px;
}

.image-error .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.el-image {
  transition: all 0.2s ease;
}

.el-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
