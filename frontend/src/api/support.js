import request from '@/utils/request'

// 获取技术支持分页
export function fetchSupportPage(data) {
  return request.post('/support/page', data)
}

// 获取全部技术支持
export function fetchSupportList() {
  return request.get('/support/list')
}

// 获取单条技术支持
export function fetchSupportById(id) {
  return request.get(`/support/${id}`)
}

// 新增技术支持
export function addSupport(data) {
  return request.post('/support/add', data)
}

// 更新技术支持
export function updateSupport(data) {
  return request.post('/support/update', data)
}

// 删除技术支持
export function deleteSupport(id) {
  return request.delete(`/support/${id}`)
} 