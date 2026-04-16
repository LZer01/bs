<template>
  <div class="user-center">
    <aside class="left-menu">
      <div class="menu-header">
        <h3>账号中心</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="custom-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="change-password">
          <el-icon><Lock /></el-icon>
          <span>修改密码</span>
        </el-menu-item>

        <el-menu-item index="bind-phone">
          <el-icon><Phone /></el-icon>
          <span>绑定手机号</span>
        </el-menu-item>

        <el-menu-item v-if="userRole >= 1" index="house-upload">
          <el-icon><Upload /></el-icon>
          <span>房源上传</span>
        </el-menu-item>

        <el-menu-item v-if="userRole >= 2" index="admin-setting">
          <el-icon><Setting /></el-icon>
          <span>管理员设置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="right-content">
      <transition name="fade-transform" mode="out-in">
        <div :key="activeMenu" class="content-wrapper">
          
          <ChangePassword v-if="activeMenu === 'change-password'" />

          <BindPhone v-else-if="activeMenu === 'bind-phone'" />

          <HouseUpload v-else-if="activeMenu === 'house-upload'" />

          <div v-else-if="activeMenu === 'admin-setting'" class="admin-view">
            <el-result icon="info" title="超级管理员设置" sub-title="系统参数与权限管理" />
          </div>

        </div>
      </transition>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Lock, Phone, Setting, Upload } from '@element-plus/icons-vue' // 引入 Upload 图标
import ChangePassword from './ChangePassword.vue'
import BindPhone from './BindPhone.vue'
import HouseUpload from './HouseUpload.vue' // 引入新组件

const activeMenu = ref('change-password')
const userRole = ref(0) 

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      // 假设 role: 0=用户, 1=普通管理员(可上传), 2=超级管理员
      userRole.value = user.role || 0
    } catch (e) {
      console.error('解析用户信息失败')
    }
  }
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}
</script>

<style scoped>
/* 样式部分保持不变，参考之前的回答 */
.user-center { display: flex; max-width: 1200px; margin: 0 auto; height: calc(100vh - 80px); padding: 24px; gap: 24px; background-color: #f0f2f5; box-sizing: border-box; }
.left-menu { width: 240px; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05); display: flex; flex-direction: column; }
.menu-header { padding: 24px 20px; border-bottom: 1px solid #f0f0f0; }
.menu-header h3 { margin: 0; font-size: 18px; color: #1f1f1f; }
.custom-menu { border-right: none !important; padding: 10px 0; }
.right-content { flex: 1; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05); overflow-y: auto; display: flex; justify-content: center; padding: 40px; }
.content-wrapper { width: 100%; max-width: 600px; } /* 上传表单可以稍微放宽到 600px */
.fade-transform-enter-active, .fade-transform-leave-active { transition: all 0.3s ease; }
.fade-transform-enter-from { opacity: 0; transform: translateX(10px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(-10px); }
</style>