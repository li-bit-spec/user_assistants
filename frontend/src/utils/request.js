import axios from 'axios'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 使用相对路径，自动适配当前域名和端口
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果返回的状态码不是0，说明接口有问题，直接抛出错误让业务层处理
    if (res.code !== 0) {
      // 不在这里显示错误消息，让业务层处理
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    // 不在这里显示错误消息，让业务层统一处理
    // 保留原始错误信息供业务层判断
    return Promise.reject(error)
  }
)

export const get = (url, params) => {
  return service.get(url, { params })
}

export const post = (url, data) => {
  return service.post(url, data)
}

export default service
