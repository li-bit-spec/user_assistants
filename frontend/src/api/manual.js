import request from '@/utils/request'

// 获取文章分页
export function fetchManualPage(data) {
  return request.post('/manual/page', data)
}

// 获取全部文章
export function fetchManualList() {
  return request({
    url: '/manual/list',
    method: 'get'
  })
}

// 获取单篇文章
export function fetchManualById(id) {
  return request({
    url: `/manual/${id}`,
    method: 'get'
  })
}

// 新增文章
export function addManual(data) {
  return request({
    url: '/manual/add',
    method: 'post',
    data
  })
}

// 更新文章
export function updateManual(data) {
  return request({
    url: '/manual/update',
    method: 'post',
    data
  })
}

// 删除文章
export function deleteManual(id) {
  return request({
    url: `/manual/${id}`,
    method: 'delete'
  })
} 