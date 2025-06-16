import request from '@/utils/request'

export function uploadFile(file) {
  console.log('开始准备上传文件:', file.name)
  const formData = new FormData()
  formData.append('file', file)
  console.log('FormData 已创建，准备发送请求')
  
  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 