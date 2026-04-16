<template>
  <header class="header">
    <div class="header-container">
      <div class="left-section">
        <router-link to="/" class="logo">宜居租房</router-link>
        <div class="city-selector" @click="showCityPicker = true">
          <i class="icon-location">📍</i>
          <span class="city-name">{{ currentCity }}</span>
          <i class="arrow-down">▼</i>
        </div>
      </div>

      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/find-house">全部房源</router-link>
        <router-link to="/map">地图找房</router-link>
        <router-link to="/guides">租房指南</router-link>
      </nav>

      <div class="right-section">
        <div class="message-icon" @click="goToMessages" title="消息通知">
          <i class="icon-bell">🔔</i>
          <span v-if="unreadMsgCount > 0" class="msg-badge">{{ unreadMsgCount }}</span>
        </div>

        <div class="user-area">
          <!-- 未登录状态 -->
          <div v-if="!isLogin" class="login-reg">
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </div>

          <!-- 已登录状态 -->
          <div
            v-else
            class="user-dropdown"
            @mouseenter="handleMouseEnter"
            @mouseleave="handleMouseLeave"
          >
            <div class="avatar-container">
              <img :src="finalAvatar" alt="头像" class="user-avatar" />
            </div>
            <span class="username">{{ username }}</span>
            <svg 
              class="arrow-icon" 
              :class="{ 'rotate': showMenu }" 
              width="12" height="12" viewBox="0 0 24 24" fill="currentColor"
            >
              <path d="M7 10l5 5 5-5z" />
            </svg>

            <transition name="slide-up">
              <div
                v-show="showMenu"
                class="dropdown-menu"
                @mouseenter="handleMouseEnter"  
                @mouseleave="handleMouseLeave" 
              >
                <router-link to="/user-center">👤 个人中心</router-link>
                <router-link to="/my-contracts">📜 我的合同</router-link>
                <router-link to="/favorites">⭐ 收藏房源</router-link>
                <router-link to="/view-history">🕒 浏览记录</router-link>
                <div class="divider"></div>
                <a href="javascript:void(0)" @click="handleLogout" class="logout-link">
                  🚪 退出登录
                </a>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

    <!-- 城市选择弹窗 -->
    <transition name="fade">
      <div v-if="showCityPicker" class="modal-mask" @click.self="closeCityPicker">
        <div class="city-picker-card">
          <div class="picker-header">
            <h3>选择您所在的城市</h3>
            <button class="close-btn" @click="closeCityPicker">✕</button>
          </div>

          <div class="search-bar">
            <input 
              v-model="citySearch" 
              type="text" 
              placeholder="请输入城市名（如：杭州）" 
              @keyup.enter="confirmCity(citySearch)"
            />
            <button class="confirm-btn" @click="confirmCity(citySearch)">确认修改</button>
          </div>

          <div class="city-body">
            <p class="label">热门城市</p>
            <div class="city-tags">
              <span
                v-for="city in filteredCities"
                :key="city"
                :class="{ active: currentCity === city }"
                @click="confirmCity(city)"
              >
                {{ city }}
              </span>
            </div>
            <div v-if="filteredCities.length === 0" class="empty-hint">
              未匹配到预设城市，点击“确认修改”可直接使用输入内容
            </div>
          </div>
        </div>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const emit = defineEmits(['city-changed'])

// ====================== 用户信息相关 ======================
const userInfo = ref(null)

// 从 localStorage 读取真实登录用户信息
const loadUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      userInfo.value = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败', e)
      userInfo.value = null
    }
  } else {
    userInfo.value = null
  }
}

// 计算属性
const isLogin = computed(() => !!userInfo.value)
const username = computed(() => {
  if (!userInfo.value) return '用户'
  return userInfo.value.username || userInfo.value.nickname || '用户'
})
const finalAvatar = computed(() => {
  if (userInfo.value?.avatar) {
    return userInfo.value.avatar
  }
  // 如果后端没有返回头像，使用 DiceBear 生成
  const seed = userInfo.value?.username || userInfo.value?.id || 'default'
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`
})

// 监听 localStorage 变化（解决登录后 Header 不刷新问题）
const handleStorageChange = (e) => {
  if (e.key === 'user' || e.key === null) {
    loadUserInfo()
  }
}

// ====================== 其他状态 ======================
const showMenu = ref(false)
const showCityPicker = ref(false)
const unreadMsgCount = ref(0)        // 后期可改为从接口获取
const currentCity = ref('南京')
const citySearch = ref('')
const hotCities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '南京', '西安', '重庆', '苏州', '天津', '长沙', '郑州', '东莞', '青岛']

const filteredCities = computed(() => {
  if (!citySearch.value) return hotCities
  return hotCities.filter(city => city.includes(citySearch.value))
})

// ====================== 方法 ======================
const confirmCity = (city) => {
  if (!city || city.trim() === '') return
  currentCity.value = city
  citySearch.value = ''
  showCityPicker.value = false
  emit('city-changed', city)
}

const closeCityPicker = () => {
  showCityPicker.value = false
  citySearch.value = ''
}

// 下拉菜单鼠标事件
let hideTimer = null
const handleMouseEnter = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
    hideTimer = null
  }
  showMenu.value = true
}

const handleMouseLeave = () => {
  hideTimer = setTimeout(() => {
    showMenu.value = false
  }, 200)
}

const goToMessages = () => {
  router.push('/messages')
}

// 退出登录
const handleLogout = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
    hideTimer = null
  }
  // 清除登录信息
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  userInfo.value = null
  showMenu.value = false
  
  router.push('/login')
}

// ====================== 生命周期 ======================
onMounted(() => {
  loadUserInfo();
  window.addEventListener('storage', handleStorageChange);
});

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange);
});
</script>

<style scoped>
/* 以下样式保持你原来的不变（为了完整性我保留了全部） */
.header {
  width: 100%;
  height: 70px;
  background: #fff;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.header-container {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.left-section { display: flex; align-items: center; gap: 24px; }
.logo {
  font-size: 24px;
  font-weight: 800;
  color: #2d8cf0;
  text-decoration: none;
  letter-spacing: 1px;
}
.city-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f3f6f9;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}
.city-selector:hover {
  background: #fff;
  border-color: #2d8cf0;
  color: #2d8cf0;
}
.city-name { font-size: 14px; font-weight: 500; }
.arrow-down { font-size: 10px; opacity: 0.6; }

.nav { display: flex; gap: 35px; }
.nav a {
  text-decoration: none;
  font-size: 16px;
  color: #515a6e;
  font-weight: 400;
  transition: color 0.3s;
}
.nav a:hover, .nav .router-link-active {
  color: #2d8cf0;
  font-weight: 600;
}

.right-section { display: flex; align-items: center; gap: 25px; }
.message-icon {
  position: relative;
  font-size: 20px;
  cursor: pointer;
  color: #515a6e;
}
.msg-badge {
  position: absolute;
  top: -6px;
  right: -8px;
  background: #ff4d4f;
  color: #fff;
  font-size: 11px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  border: 2px solid #fff;
}

.login-reg { display: flex; gap: 12px; }
.btn-login, .btn-register {
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.3s;
}
.btn-login { color: #2d8cf0; border: 1px solid #2d8cf0; }
.btn-login:hover { background: #f0f7ff; }
.btn-register { background: #2d8cf0; color: #fff; }
.btn-register:hover { background: #1b79d8; }

.user-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 8px;
}
.avatar-container {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}
.user-avatar { width: 100%; height: 100%; object-fit: cover; }
.username { font-size: 14px; color: #17233d; font-weight: 600; }
.arrow-icon { color: #808695; transition: transform 0.3s; }
.arrow-icon.rotate { transform: rotate(180deg); }

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 10px;
  width: 170px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  padding: 8px 0;
  border: 1px solid #f0f0f0;
}
.dropdown-menu a {
  display: block;
  padding: 10px 20px;
  color: #515a6e;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.2s;
}
.dropdown-menu a:hover { background: #f5f7fa; color: #2d8cf0; }
.divider { height: 1px; background: #eee; margin: 5px 0; }
.logout-link { color: #ff4d4f !important; }

.modal-mask {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.city-picker-card {
  background: #fff;
  width: 460px;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.3);
}
.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}
.picker-header h3 { margin: 0; font-size: 20px; color: #17233d; }
.close-btn { background: none; border: none; font-size: 20px; cursor: pointer; color: #999; }

.search-bar { display: flex; gap: 10px; margin-bottom: 30px; }
.search-bar input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e8eaec;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.3s;
}
.search-bar input:focus { border-color: #2d8cf0; }
.confirm-btn {
  padding: 0 20px;
  background: #2d8cf0;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.city-body .label { font-size: 14px; color: #808695; margin-bottom: 15px; }
.city-tags { display: flex; flex-wrap: wrap; gap: 10px; }
.city-tags span {
  padding: 6px 15px;
  background: #f5f7fa;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}
.city-tags span:hover { background: #e8f3ff; color: #2d8cf0; }
.city-tags span.active { background: #2d8cf0; color: #fff; }
.empty-hint { font-size: 12px; color: #999; margin-top: 15px; }

/* 动画 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-up-enter-active, .slide-up-leave-active {
  transition: opacity 0.25s, transform 0.25s;
}
.slide-up-enter-from, .slide-up-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
.slide-up-leave-active {
  pointer-events: none;
}
</style>