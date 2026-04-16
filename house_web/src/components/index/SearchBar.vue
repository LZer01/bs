<!-- components/SearchBar.vue -->
<template>
  <div class="search-card">
    <!-- 选项卡：整租 / 合租 / 品牌公寓 -->
    <div class="search-tabs">
      <span
        :class="{ active: searchType === 'rent' }"
        @click="searchType = 'rent'"
      >整租</span>
      <span
        :class="{ active: searchType === 'share' }"
        @click="searchType = 'share'"
      >合租</span>
      <span
        :class="{ active: searchType === 'brand' }"
        @click="searchType = 'brand'"
      >品牌公寓</span>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <div class="form-item city-selector">
        <i class="form-icon">📍</i>
        <div class="city-input-wrapper">
          <input
            type="text"
            :value="currentCity"
            placeholder="输入城市"
            @focus="showCitySuggestions = true"
            @input="onCityInput"
            @blur="closeSuggestions"
          />
          <ul v-if="showCitySuggestions && filteredCities.length" class="city-suggestions">
            <li v-for="city in filteredCities" :key="city" @mousedown.prevent="selectCity(city)">
              {{ city }}
            </li>
          </ul>
        </div>
      </div>
      
      <div class="form-item location-item">
        <input
          type="text"
          placeholder="区域、地铁、商圈或小区名"
          v-model="searchParams.keyword"
          @keyup.enter="handleSearch"
        />
      </div>

      <div class="form-item price-item">
        <i class="form-icon">💰</i>
        <input
          type="text"
          placeholder="价格区间，如 2000-4000"
          v-model="searchParams.priceRange"
        />
      </div>

      <div class="form-item room-item">
        <i class="form-icon">🏠</i>
        <select v-model="searchParams.roomType">
          <option value="">户型</option>
          <option value="1">一居室</option>
          <option value="2">二居室</option>
          <option value="3">三居室</option>
          <option value="4">四居室+</option>
        </select>
      </div>

      <button class="search-btn" @click="handleSearch">搜索房源</button>
    </div>

    <!-- 热门搜索标签 -->
    <div class="hot-tags">
      <span>热门搜索：</span>
      <a
        v-for="tag in hotTags"
        :key="tag"
        href="javascript:void(0)"
        @click="searchParams.keyword = tag; handleSearch()"
      >{{ tag }}</a>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  currentCity: {
    type: String,
    default: '南京'
  }
})

const emit = defineEmits(['search', 'city-change'])

// 搜索类型
const searchType = ref('rent')

// 搜索参数
const searchParams = reactive({
  keyword: '',
  priceRange: '',
  roomType: ''
})

// 城市建议
const showCitySuggestions = ref(false)
const cityInput = ref(props.currentCity)
const allCities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '南京', '西安', '重庆', '苏州', '天津', '长沙', '郑州', '东莞', '青岛']

const filteredCities = computed(() => {
  if (!cityInput.value) return allCities
  const lowerInput = cityInput.value.toLowerCase()
  return allCities.filter(city => city.toLowerCase().includes(lowerInput))
})

const hotTags = ['国贸', '望京', '西二旗', '五道口', '回龙观', '中关村']

const onCityInput = (e) => {
  cityInput.value = e.target.value
  showCitySuggestions.value = true
}

const selectCity = (city) => {
  cityInput.value = city
  showCitySuggestions.value = false
  emit('city-change', city)
}

const closeSuggestions = () => {
  setTimeout(() => {
    showCitySuggestions.value = false
  }, 150)
}

const handleSearch = () => {
  const queryParams = {
    keyword: searchParams.keyword || '',
    priceRange: searchParams.priceRange || '',
    roomType: searchParams.roomType || '',
    rentType: searchType.value,
    city: cityInput.value
  }
  
  Object.keys(queryParams).forEach(key => {
    if (!queryParams[key]) {
      delete queryParams[key]
    }
  })
  
  emit('search', queryParams)
  
  router.push({
    path: '/search',
    query: queryParams
  })
}
</script>

<style scoped>
.search-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px 30px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.search-tabs {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.search-tabs span {
  font-size: 18px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  padding-bottom: 6px;
  border-bottom: 2px solid transparent;
}

.search-tabs span.active {
  color: #2d8cf0;
  border-bottom-color: #2d8cf0;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.form-item {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 40px;
  padding: 6px 15px;
  position: relative;
}

.form-icon {
  margin-right: 8px;
  font-size: 18px;
}

.form-item input,
.form-item select {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  width: 100%;
}

.city-input-wrapper {
  flex: 1;
  position: relative;
}

.city-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  list-style: none;
  padding: 0;
  margin: 5px 0 0;
}

.city-suggestions li {
  padding: 8px 12px;
  cursor: pointer;
}

.city-suggestions li:hover {
  background: #f0f7ff;
}

.search-btn {
  background: #2d8cf0;
  color: white;
  border: none;
  border-radius: 40px;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
}

.search-btn:hover {
  background: #1b79d8;
}

.hot-tags {
  margin-top: 15px;
  text-align: left;
  color: #888;
  font-size: 14px;
}

.hot-tags a {
  color: #2d8cf0;
  margin-left: 12px;
  text-decoration: none;
}

.hot-tags a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
  }
  
  .form-item {
    width: 100%;
  }
  
  .search-btn {
    width: 100%;
  }
}
</style>