<template>
  <div id="app">
    <!-- 只有当当前路由不是 login 或 register 时，才显示 Header -->
    <Header v-if="!isAuthPage" />

    <!-- 路由出口 -->
    <div :class="{ 'has-header': !isAuthPage }">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
// 修改导入路径，因为 Header.vue 在 components/index/ 目录下
import Header from './components/index/Header.vue'  

const route = useRoute()

// 判断当前是否是登录或注册页
const isAuthPage = computed(() => {
  return route.name === 'Login' || route.name === 'Register'
})
</script>

<style>
body { margin: 0; padding: 0; }

/* 如果有 Header，内容下移 */
.has-header {
  margin-top: 65px;
}
</style>