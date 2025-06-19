<template>
  <div class="feedback-container">
    <!-- 上部分：反馈输入区域 -->
    <div class="feedback-input-container">
      <el-input
        v-model="feedbackContent"
        type="textarea"
        :rows="4"
        placeholder="请输入反馈内容"
        class="w-full mb-4"
      />
      
      <div class="feedback-images-container">
        <!-- 已上传图片预览 -->
        <div class="image-preview-list" v-if="uploadFileList.length">
          <div v-for="(file, index) in uploadFileList" :key="index" class="image-preview-item">
            <el-image
              :src="file.url"
              fit="cover"
              class="preview-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <el-icon class="delete-icon" @click="handleRemoveImage(file)"><Close /></el-icon>
          </div>
        </div>

        <div class="upload-actions">
          <el-upload
            v-model:file-list="uploadFileList"
            class="image-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :limit="10"
            accept="image/jpeg,image/png"
          >
            <el-button type="primary" plain>
              <el-icon class="mr-2"><Plus /></el-icon>上传图片
            </el-button>
          </el-upload>
          
          <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
        </div>
      </div>
    </div>

    <!-- 下部分：反馈列表 -->
    <div class="feedback-list-container">
      <el-table 
        :data="feedbackList" 
        style="width: 100%" 
        v-loading="loading"
        :cell-style="{ padding: '16px' }"
        :header-cell-style="{ 
          background: '#f5f7fa',
          color: '#606266',
          fontWeight: 'bold',
          borderRight: '1px solid #ebeef5',
          padding: '12px 16px'
        }"
        border
      >
        <el-table-column prop="content" label="反馈内容" min-width="300">
          <template #default="{ row }">
            <div class="feedback-content">
              <div class="content-text">{{ row.content }}</div>
              <div class="image-list" v-if="row.imageUrls && row.imageUrls.length">
                <el-image
                  v-for="(url, index) in processImageUrls(row.imageUrls)"
                  :key="index"
                  :src="url"
                  :preview-src-list="processImageUrls(row.imageUrls)"
                  :initial-index="index"
                  fit="cover"
                  class="feedback-image"
                  :preview-teleported="true"
                  @click="() => viewImage(processImageUrls(row.imageUrls), index)"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="反馈时间" width="180" align="center">
          <template #default="{ row }">
            <span class="feedback-time">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
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
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
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
import { Plus, Picture, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as feedbackApi from '@/api/feedback'
import { ElImageViewer } from 'element-plus'

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
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return ''
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).replace(/\//g, '-')
  } catch (e) {
    console.error('日期格式化错误:', e)
    return ''
  }
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
  if (uploadFileList.value.length >= 10) {
    ElMessage.error('最多只能上传10张图片!')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 0) {
    console.log('图片上传成功，完整响应：', response)
    const imageUrl = response.data
    if (!feedbackImages.value.includes(imageUrl) && feedbackImages.value.length < 10) {
      feedbackImages.value.push(imageUrl)
      uploadFileList.value = feedbackImages.value.map(url => ({
        url,
        name: url.split('/').pop()
      }))
      ElMessage.success('上传成功')
    } else {
      ElMessage.warning('已达到最大上传数量限制')
    }
    console.log('当前图片列表：', feedbackImages.value)
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
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
    currentPage.value = 1  // 重置到第一页
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
    
    // 如果当前页面删除后没有数据了，且不是第一页，则跳转到前一页
    if (feedbackList.value.length === 1 && currentPage.value > 1) {
      currentPage.value = currentPage.value - 1
    }
    
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

const handlePageSizeChange = async (size) => {
  pageSize.value = size
  currentPage.value = 1  // 重置到第一页
  await fetchFeedbackList()
}

const handleView = (row) => {
  console.log('查看详情，行数据：', row)
  if (row.imageUrls && row.imageUrls.length > 0) {
    row.imageUrls = row.imageUrls.map(url => {
      if (!url.startsWith('http') && !url.startsWith('/uploads')) {
        return `/uploads${url}`
      }
      return url
    })
  }
  console.log('处理后的图片URL列表：', row.imageUrls)
  currentDetailItem.value = row
  detailDialogVisible.value = true
}

const handleRemoveImage = (file) => {
  const index = uploadFileList.value.indexOf(file)
  if (index !== -1) {
    uploadFileList.value.splice(index, 1)
    feedbackImages.value = feedbackImages.value.filter(url => url !== file.url)
  }
}

const handleImageClick = (imageUrls, index) => {
  // 使用 el-image 组件的预览功能，不需要额外处理
}

const processImageUrls = (urls) => {
  if (!urls) return []
  return urls.map(url => {
    if (!url.startsWith('http') && !url.startsWith('/uploads/')) {
      return `/uploads/${url}`
    }
    return url
  })
}

const viewImage = (urls, index) => {
  const imgViewer = ElImageViewer({
    urlList: urls,
    initialIndex: index,
    teleported: true,
    zIndex: 3000,
    onClose: () => {
      imgViewer.close()
    }
  })
}
</script>

<style scoped>
.feedback-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.feedback-input-container {
  background-color: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.feedback-images-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.image-preview-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.image-preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  padding: 4px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.delete-icon:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.upload-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.feedback-list-container {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.feedback-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-text {
  white-space: pre-wrap;
  word-break: break-all;
  color: #303133;
  line-height: 1.6;
}

.feedback-time {
  color: #606266;
  font-size: 14px;
}

.image-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
}

.feedback-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
  object-fit: cover;
}

.feedback-image:hover {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f3f4f6;
  color: #9ca3af;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

:deep(.el-upload-list) {
  display: none;
}

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

:deep(.el-image-viewer__wrapper) {
  position: fixed;
  z-index: 3000;
}

:deep(.el-image-viewer__mask) {
  position: fixed;
}

:deep(.el-image-viewer__close) {
  color: #fff;
}

:deep(.el-image-viewer__actions) {
  z-index: 3001;
}

:deep(.el-image-viewer__canvas) {
  z-index: 3001;
}

:deep(.el-image-viewer__prev, .el-image-viewer__next) {
  z-index: 3001;
}

:deep(.el-table) {
  --el-table-border-color: #ebeef5;
  --el-table-header-bg-color: #f5f7fa;
}

:deep(.el-table::before) {
  display: none;
}

:deep(.el-table__row) {
  transition: background-color 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-table td) {
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-table .cell) {
  padding: 0;
}

:deep(.el-button--small) {
  padding: 8px 16px;
}
</style>
