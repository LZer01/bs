import axios from 'axios'

const service = axios.create({
  baseURL: '/api', // 配合 vite.config.js 的 proxy
  timeout: 5000
})

// 请求拦截器：自动带 Token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

export default service