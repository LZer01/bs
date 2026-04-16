import request from './request'

export const authApi = {
  login(data) {
    return request.post('/auth/login', data)
  },
  register(data) {
    return request.post('/auth/register', data)
  },
  getInfo() {
    return request.get('/auth/info', {
      headers: { 'Authorization': localStorage.getItem('token') }
    })
  }
}