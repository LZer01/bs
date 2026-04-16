<!-- components/HouseRecommendation.vue -->
<template>
  <section class="recommend-section">
    <div class="section-container">
      <div class="section-header">
        <h2>精选好房推荐</h2>
        <div class="sort-options">
          <span
            v-for="opt in sortOptions"
            :key="opt.value"
            :class="{ active: currentSort === opt.value }"
            @click="handleSortChange(opt.value)"
          >{{ opt.label }}</span>
        </div>
      </div>

      <!-- 房源卡片网格 -->
      <div class="house-grid" v-if="!loading">
        <div
          class="house-card"
          v-for="house in houses"
          :key="house.id"
          @click="goToDetail(house.id)"
        >
          <div class="card-img">
            <img :src="house.coverImage" :alt="house.title" />
            <span class="house-tag" v-if="house.tag">{{ house.tag }}</span>
            <span class="favorite-btn" @click.stop="toggleFavorite(house)">
              {{ house.isFavorited ? '❤️' : '🤍' }}
            </span>
          </div>
          <div class="card-info">
            <h3 class="house-title">{{ house.title }}</h3>
            <div class="house-desc">
              <span>{{ house.roomInfo }}</span>
              <span class="divider">|</span>
              <span>{{ house.area }}㎡</span>
              <span class="divider">|</span>
              <span>{{ house.orientation }}</span>
            </div>
            <div class="house-location">
              <i>📍</i> {{ house.district }} · {{ house.community }}
              <span class="distance" v-if="house.distance">{{ house.distance }}</span>
            </div>
            <div class="house-price">
              <span class="price-num">{{ house.price }}</span> 元/月
            </div>
          </div>
        </div>
      </div>
      
      <div class="loading-placeholder" v-else>加载中...</div>

      <div class="view-more">
        <router-link to="/find-house" class="more-link">查看更多房源 →</router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  initialSort: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['sort-change', 'favorite-toggle'])

// 状态
const houses = ref([])
const loading = ref(false)
const currentSort = ref(props.initialSort)

// 排序选项
const sortOptions = [
  { value: 'default', label: '默认排序' },
  { value: 'price_asc', label: '价格最低' },
  { value: 'price_desc', label: '价格最高' },
  { value: 'newest', label: '最新发布' }
]

// 方法
const fetchHouses = async () => {
  loading.value = true
  try {
    // 模拟API请求
    const mockData = [
      {
        id: 1,
        title: '国贸CBD精装一居',
        coverImage: 'https://via.placeholder.com/300x180',
        tag: '精选',
        roomInfo: '1室1厅1卫',
        area: 55,
        orientation: '南',
        district: '朝阳',
        community: '建外SOHO',
        distance: '距国贸站500m',
        price: 6500,
        isFavorited: false
      },
      {
        id: 2,
        title: '望京SOHO旁边两居室',
        coverImage: 'https://via.placeholder.com/300x180',
        tag: '新上',
        roomInfo: '2室1厅1卫',
        area: 85,
        orientation: '南北',
        district: '朝阳',
        community: '望京西园',
        distance: '距望京站300m',
        price: 8800,
        isFavorited: true
      },
      // 添加更多房源数据...
    ]
    
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    houses.value = mockData
    
    // 根据排序选项排序
    sortHouses()
  } catch (error) {
    console.error('获取房源失败:', error)
  } finally {
    loading.value = false
  }
}

const sortHouses = () => {
  const sorted = [...houses.value]
  switch (currentSort.value) {
    case 'price_asc':
      sorted.sort((a, b) => a.price - b.price)
      break
    case 'price_desc':
      sorted.sort((a, b) => b.price - a.price)
      break
    case 'newest':
      sorted.sort((a, b) => b.id - a.id)
      break
    default:
      // 默认排序，按id降序
      sorted.sort((a, b) => b.id - a.id)
  }
  houses.value = sorted
}

const handleSortChange = (sortValue) => {
  currentSort.value = sortValue
  sortHouses()
  emit('sort-change', sortValue)
}

const goToDetail = (houseId) => {
  router.push(`/house/${houseId}`)
}

const toggleFavorite = (house) => {
  house.isFavorited = !house.isFavorited
  emit('favorite-toggle', { id: house.id, isFavorited: house.isFavorited })
}

// 暴露方法给父组件
defineExpose({
  refresh: fetchHouses
})

onMounted(() => {
  fetchHouses()
})
</script>

<style scoped>
.recommend-section {
  padding: 40px 0;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-header h2 {
  font-size: 26px;
  font-weight: 600;
}

.sort-options {
  display: flex;
  gap: 20px;
}

.sort-options span {
  color: #666;
  cursor: pointer;
  font-size: 15px;
}

.sort-options span.active {
  color: #2d8cf0;
  font-weight: 500;
}

.house-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.house-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;
}

.house-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  transform: translateY(-4px);
}

.card-img {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.house-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(45,140,240,0.9);
  color: white;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.favorite-btn {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(255,255,255,0.8);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  backdrop-filter: blur(2px);
  cursor: pointer;
}

.card-info {
  padding: 15px;
}

.house-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.house-desc {
  font-size: 13px;
  color: #777;
  margin-bottom: 8px;
}

.house-desc .divider {
  margin: 0 6px;
  color: #ddd;
}

.house-location {
  font-size: 13px;
  color: #555;
  margin-bottom: 10px;
}

.distance {
  margin-left: 8px;
  color: #2d8cf0;
  font-size: 12px;
}

.house-price {
  font-size: 14px;
  font-weight: 400;
  color: #666;
}

.price-num {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.view-more {
  text-align: center;
  margin-top: 40px;
}

.more-link {
  display: inline-block;
  padding: 12px 30px;
  border: 1px solid #2d8cf0;
  border-radius: 40px;
  color: #2d8cf0;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
}

.more-link:hover {
  background: #2d8cf0;
  color: white;
}

.loading-placeholder {
  text-align: center;
  padding: 40px;
  color: #999;
}

@media (max-width: 1024px) {
  .house-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .house-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>