import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'  // 导入首页组件

// 定义路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage,  // 使用新的首页组件
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    // 确保路径指向正确的 Login 页面文件
    component: () => import('@/components/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    // 确保路径指向正确的 Register 页面文件
    component: () => import('@/components/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/components/header/UserCenter.vue'),
    meta: { title: '个人中心' }
  },
  
  {
    path: '/search',
    name: 'Search',
    // 确保路径指向正确的 Search 页面文件
    component: () => import('@/components/CommunitySearch.vue'),
    meta: { title: '搜索' }
  }

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 可选：全局前置守卫，用于设置页面标题或后续添加权限验证
router.beforeEach((to, from, next) => {
  // 如果路由配置中有 meta.title，则设置文档标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router