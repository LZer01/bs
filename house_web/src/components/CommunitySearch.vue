<template>
  <div class="search-container">
    <el-card class="search-box">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="位置">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="输入地址/小区名/地铁站"
            clearable
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item label="附近">
          <el-select 
            v-model="searchForm.radius" 
            style="width: 100px"
            filterable
            allow-create
            placeholder="请选择">
            <el-option label="1km" :value="1" />
            <el-option label="3km" :value="3" />
            <el-option label="5km" :value="5" />
            <el-option label="10km" :value="10" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="searchForm.searchType">
            <el-radio-button label="community">小区</el-radio-button>
            <el-radio-button label="house">房源</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <div class="filters">
        <el-tag 
          v-for="tag in filterTags" 
          :key="tag.value"
          :type="searchForm.rentType === tag.value ? 'primary' : 'info'"
          effect="plain"
          class="filter-tag"
          @click="toggleFilter('rentType', tag.value)"
        >
          {{ tag.label }}
        </el-tag>
      </div>
    </el-card>

    <el-card class="result-list">
      <div v-if="loading" class="loading-text">正在为您搜寻周边小区...</div>
      <el-empty v-else-if="communities.length === 0" description="暂无匹配小区" />
      
      <el-row :gutter="20" v-else>
        <el-col 
          v-for="item in communities" 
          :key="item.id" 
          :xs="24" :sm="12" :md="8" :lg="6"
          class="community-item"
        >
          <el-card :body-style="{ padding: '0px' }" shadow="hover" @click="goToDetail(item)">
            <div class="card-img">
              <el-image 
                :src="(item.photos && item.photos.length > 0) ? item.photos[0] : '/default-community.jpg'" 
                fit="cover"
                lazy
              >
                <template #error>
                  <div class="image-placeholder">暂无图片</div>
                </template>
              </el-image>
              <el-tag size="small" class="distance-tag">
                {{ item.distance ? item.distance.toFixed(1) : '0' }}km
              </el-tag>
            </div>
            
            <div class="card-info">
              <h4 class="community-name">{{ item.name }}</h4>
              <p class="community-address">
                <el-icon><Location /></el-icon>
                {{ item.district }} · {{ item.address }}
              </p>

              <div class="community-tags">
                <el-tag v-if="item.district" size="small" effect="plain" type="info">{{ item.district }}</el-tag>
                <el-tag v-if="item.housingCount > 10" size="small" effect="plain" type="success">房源充足</el-tag>
              </div>

              <div class="card-footer">
                <div class="housing-summary">
                  <span v-if="item.housingCount > 0">
                    在租: <b>{{ item.housingCount }}</b> 套
                  </span>
                  <span v-else class="no-house">暂无房源</span>
                </div>

                <el-button 
                  :type="item.isUrged ? 'info' : 'warning'" 
                  size="small" 
                  link
                  :disabled="item.isUrged"
                  @click.stop="handleUrgeCommunity(item)"
                >
                  <el-icon><Bell /></el-icon>
                  {{ item.isUrged ? '已催更' : '催更' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        layout="prev, pager, next"
        class="pagination"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="currentCommunity ? currentCommunity.name + ' - 在租房源' : '房源列表'"
      width="85%"
    >
      <div v-if="loadingHouses" class="loading-text">正在加载房源...</div>
      <el-empty v-else-if="houses.length === 0" description="该小区暂无在租房源" />
      
      <el-row :gutter="20" v-else>
        <el-col v-for="house in houses" :key="house.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="house-card">
            <div class="favorite-btn" @click.stop="toggleFavorite(house)">
              <el-icon :size="20" :color="house.isFavorited ? '#f56c6c' : '#909399'">
                <StarFilled v-if="house.isFavorited" />
                <Star v-else />
              </el-icon>
            </div>

            <div class="house-img">
              <el-image :src="house.photos?.[0] || '/default-house.jpg'" fit="cover" lazy />
            </div>

            <div class="house-info">
              <h5>{{ house.title || house.name }}</h5>
              <p class="price"><b>{{ house.rentPrice || house.price }}</b> 元/月</p>
              <p class="desc">{{ house.roomType }} · {{ house.area }}㎡</p>
              <div class="house-tags">
                <el-tag v-for="(tag, i) in house.tags" :key="i" size="small" class="tag-item">{{ tag }}</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Location, Bell, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// --- 状态数据 ---
const searchForm = reactive({
  keyword: '',
  radius: 3,
  searchType: 'community',
  rentType: ''
})

const filterTags = [
  { label: '全部', value: '' },
  { label: '整租', value: 'whole' },
  { label: '合租', value: 'shared' }
]

const communities = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const currentCommunity = ref(null)
const houses = ref([])
const loadingHouses = ref(false)

// --- 方法逻辑 ---

// 1. 搜索小区/房源
const handleSearch = async () => {
  if (!searchForm.keyword.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  loading.value = true
  try {
    const url = searchForm.searchType === 'community' ? '/api/community/search' : '/api/house/search'
    const { data } = await axios.get(url, {
      params: {
        query: searchForm.keyword,
        radius: searchForm.radius * 1000,
      }
    })
    if (data.code === 200) {
      communities.value = data.data || []
      total.value = communities.value.length
    }
  } catch (err) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 催更小区 (核心改动)
const handleUrgeCommunity = async (community) => {
  if (community.isUrged) return

  // 获取用户ID
  let userId = null;
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try { userId = JSON.parse(userStr).id; } catch (e) { console.error(e) }
  }

  try {
    const res = await axios.post(`/api/suggestion/urge`, {
      user_id: userId || localStorage.getItem('id'),
      community_id: community.id
    });
    
    if (res.data.code === 200) {
      ElMessage.success(`已收到您的请求，我们将尽快更新 ${community.name} 的房源！`);
      community.isUrged = true; // 局部更新状态
    } else {
      ElMessage.error(res.data.message || '催促失败');
    }
  } catch (error) {
    ElMessage.error('网络拥堵，请稍后再试');
  }
}

// 3. 打开小区房源详情
const goToDetail = async (community) => {
  currentCommunity.value = community
  dialogVisible.value = true
  houses.value = []
  loadingHouses.value = true
  try {
    const { data } = await axios.get(`/api/house/community/${community.id}/houses`)
    if (data.code === 200) houses.value = data.data || []
  } catch (err) {
    ElMessage.error('加载房源列表失败')
  } finally {
    loadingHouses.value = false
  }
}

// 4. 收藏房源
const toggleFavorite = async (house) => {
  const isCurrentlyFavorited = !!house.isFavorited
  const apiUrl = isCurrentlyFavorited ? '/api/favorite/cancel' : '/api/favorite/add'
  
  // 处理 Token 和 Header
  const token = localStorage.getItem('token');
  const config = { headers: {} };
  if (token) config.headers['Authorization'] = `Bearer ${token}`;

  try {
    const { data } = await axios.post(apiUrl, { houseId: house.id }, config)
    if (data.code === 200) {
      house.isFavorited = !isCurrentlyFavorited
      ElMessage.success(isCurrentlyFavorited ? '已取消收藏' : '收藏成功')
    }
  } catch (err) {
    ElMessage.error('操作失败，请重试')
  }
}

const toggleFilter = (key, value) => {
  searchForm[key] = searchForm[key] === value ? '' : value
  handleSearch()
}

const handlePageChange = (newPage) => {
  page.value = newPage
  handleSearch()
}

onMounted(() => {
  // 初始化逻辑
})
</script>

<style scoped>
.search-container { max-width: 1200px; margin: 0 auto; padding: 20px; font-family: sans-serif; }
.search-box { margin-bottom: 20px; border-radius: 12px; }
.filters { margin-top: 15px; display: flex; gap: 10px; }
.filter-tag { cursor: pointer; }

.community-item { margin-bottom: 20px; }
.card-img { position: relative; height: 170px; overflow: hidden; }
.card-img .el-image { width: 100%; height: 100%; transition: transform 0.3s; }
.community-item:hover .el-image { transform: scale(1.05); }

.distance-tag { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.6); color: #fff; border: none; }
.card-info { padding: 15px; cursor: pointer; }
.community-name { margin: 0 0 8px; font-size: 17px; color: #303133; }
.community-address { font-size: 13px; color: #909399; margin-bottom: 12px; display: flex; align-items: center; gap: 4px; }
.community-tags { margin-bottom: 12px; height: 24px; }

/* 卡片底部布局 */
.card-footer { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  border-top: 1px solid #f2f6fc; 
  padding-top: 10px; 
}
.housing-summary { font-size: 14px; color: #409EFF; }
.housing-summary b { font-size: 18px; }
.no-house { color: #909399; font-size: 13px; }

.house-card { margin-bottom: 15px; position: relative; border-radius: 8px; }
.favorite-btn { position: absolute; top: 10px; right: 10px; z-index: 10; background: rgba(255,255,255,0.8); padding: 5px; border-radius: 50%; cursor: pointer; display: flex; }
.house-img { height: 140px; }
.house-info { padding: 12px; }
.house-info h5 { margin: 0 0 8px; font-size: 15px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { color: #f56c6c; margin-bottom: 5px; }
.desc { font-size: 12px; color: #606266; margin-bottom: 8px; }
.house-tags { display: flex; gap: 4px; flex-wrap: wrap; }

.loading-text { text-align: center; padding: 40px; color: #909399; }
.pagination { margin-top: 30px; display: flex; justify-content: center; }
</style>