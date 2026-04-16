<template>
  <div class="home">
    <!-- 主体内容 -->
    <main class="main-content">
      <!-- 1. 搜索横幅区 -->
      <section class="hero-section">
        <div class="hero-container">
          <h1 class="hero-title">找到你理想的<span class="highlight">家</span></h1>
          <p class="hero-subtitle">真实房源 · 无中介费 · 线上签约</p>

          <!-- 搜索组件 -->
          <SearchBar 
            :current-city="currentCity"
            @search="handleSearch"
            @city-change="handleCityChange"
          />
        </div>
      </section>

      <!-- 2. 快捷入口图标区 -->
      <section class="quick-entry">
        <div class="section-container">
          <div class="entry-grid">
            <div class="entry-item" @click="goToFindHouse('entire')">
              <div class="entry-icon">🏘️</div>
              <span>整租</span>
            </div>
            <div class="entry-item" @click="goToFindHouse('share')">
              <div class="entry-icon">👥</div>
              <span>合租</span>
            </div>
            <div class="entry-item" @click="goToFindHouse('brand')">
              <div class="entry-icon">🏢</div>
              <span>品牌公寓</span>
            </div>
            <div class="entry-item" @click="$router.push('/map')">
              <div class="entry-icon">🗺️</div>
              <span>地图找房</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 3. 推荐房源组件 -->
      <HouseRecommendation 
        ref="recommendationRef"
        @sort-change="handleSortChange"
        @favorite-toggle="handleFavoriteToggle"
      />

      <!-- 4. 底部信息栏 -->
      <footer class="footer">
        <div class="footer-container">
          <div class="footer-links">
            <a href="#">关于我们</a>
            <a href="#">帮助中心</a>
            <a href="#">隐私政策</a>
            <a href="#">用户协议</a>
          </div>
          <p class="copyright">© 2026 宜居租房 版权所有</p>
        </div>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
// 注意导入路径，因为组件在 components/index/ 目录下
import SearchBar from '@/components/index/SearchBar.vue'
import HouseRecommendation from '@/components/index/HouseRecommendation.vue'

const router = useRouter()
const currentCity = ref('北京')
const recommendationRef = ref()

// 处理城市变更
const handleCityChange = (city) => {
  currentCity.value = city
  console.log('城市已变更为:', city)
  recommendationRef.value?.refresh()
}

// 处理搜索
const handleSearch = (searchParams) => {
  console.log('搜索参数:', searchParams)
}

// 处理排序变更
const handleSortChange = (sortValue) => {
  console.log('排序方式:', sortValue)
}

// 处理收藏切换
const handleFavoriteToggle = ({ id, isFavorited }) => {
  console.log(`房源 ${id} 收藏状态: ${isFavorited}`)
}

// 跳转到找房页面
const goToFindHouse = (type) => {
  router.push({
    path: '/find-house',
    query: { rentType: type }
  })
}
</script>

<style scoped>
.home {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #f8f9fa;
}

.main-content {
  margin-top: 70px;
}

/* 搜索横幅区 */
.hero-section {
  background: linear-gradient(135deg, #e8f4ff 0%, #d4e8ff 100%);
  padding: 40px 0 50px;
}

.hero-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 10px;
}

.highlight {
  color: #2d8cf0;
}

.hero-subtitle {
  font-size: 18px;
  color: #555;
  margin-bottom: 30px;
}

/* 快捷入口 */
.quick-entry {
  background: #fff;
  padding: 30px 0;
  border-bottom: 1px solid #eee;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.entry-grid {
  display: flex;
  justify-content: space-around;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.entry-item:hover {
  transform: translateY(-4px);
}

.entry-icon {
  font-size: 36px;
  background: #f0f7ff;
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
}

.entry-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 底部 */
.footer {
  background: #fff;
  border-top: 1px solid #eee;
  padding: 30px 0;
  margin-top: 20px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 15px;
}

.footer-links a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.copyright {
  color: #aaa;
  font-size: 13px;
}
</style>