import request from '@/utils/request'

// 获取反馈分页
export function fetchFeedbackPage(data) {
  console.log('调用获取反馈分页API，参数：', data)
  return request.post('/feedback/page', data)
}

// 新增反馈
export function addFeedback(data) {
  console.log('调用新增反馈API，参数：', data)
  return request.post('/feedback/add', {
    content: data.content,
    imageUrls: data.imageUrls || []
  })
}

// 删除反馈
export function deleteFeedback(id) {
  console.log('调用删除反馈API，ID：', id)
  return request.delete(`/feedback/${id}`)
}

// 上传图片
export function uploadImage(file) {
  console.log('调用上传图片API，文件名：', file.name)
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 