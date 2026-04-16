<template>
  <div class="house-upload-container">
    <div class="form-header">
      <h2>新增房源发布</h2>
      <p>请准确填写房源信息，以便租客快速筛选</p>
    </div>

    <el-form :model="form" ref="formRef" :rules="rules" label-position="top" class="upload-form">
      <el-card shadow="never" class="form-section">
        <template #header><span class="section-title">基本信息</span></template>
        
        <el-form-item label="房源标题" prop="title">
          <el-input v-model="form.title" placeholder="例如：精装修近地铁，阳光大三居，拎包入住" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属小区" prop="communityId">
              <el-select v-model="form.communityId" placeholder="请选择小区" style="width: 100%">
                <el-option label="阳光花园" value="1" />
                <el-option label="幸福里" value="2" />
                <el-option label="翡翠城" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月租金 (元)" prop="price">
              <el-input-number v-model="form.price" :min="0" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="建筑面积 (㎡)" prop="area">
              <el-input-number v-model="form.area" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="户型 (室)" prop="roomCount">
              <el-input-number v-model="form.roomCount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="户型 (厅)" prop="hallCount">
              <el-input-number v-model="form.hallCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span class="section-title">详细规格</span></template>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在楼层" prop="floor">
              <el-input-number v-model="form.floor" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input-number v-model="form.totalFloor" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房屋朝向" prop="direction">
              <el-select v-model="form.direction" placeholder="选择朝向" style="width: 100%">
                <el-option label="南" value="南" />
                <el-option label="北" value="北" />
                <el-option label="东" value="东" />
                <el-option label="西" value="西" />
                <el-option label="南北通透" value="南北通透" />
                <el-option label="东南" value="东南" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="房源特色标签">
          <el-checkbox-group v-model="form.tags">
            <el-checkbox label="阳台" />
            <el-checkbox label="独立卫浴" />
            <el-checkbox label="近地铁" />
            <el-checkbox label="精装修" />
            <el-checkbox label="拎包入住" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述房屋配套、周边环境、看房时间等信息..."
          />
        </el-form-item>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span class="section-title">房源图片</span></template>
        <el-upload
            action="/api/house/image"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            multiple
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">建议上传 3-5 张清晰的实拍图</div>
          </template>
        </el-upload>
      </el-card>

      <div class="form-actions">
        <el-button size="large" @click="resetForm">重置</el-button>
        <el-button type="primary" size="large" @click="onSubmit" :loading="submitting">发布房源</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  title: '',
  communityId: '',
  price: 2000,
  area: 90,
  roomCount: 2,
  hallCount: 1,
  floor: 6,
  totalFloor: 12,
  direction: '南',
  description: '',
  tags: [], // 后端 Controller 会处理这个字符串列表转为 tagMask
  photos: []
})

// 校验规则
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  communityId: [{ required: true, message: '请选择小区', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
}

// 上传处理逻辑
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    // 确保这里 response.data 就是返回的 "/uploads/xxx.jpg" 字符串
    form.photos.push(response.data); 
  } else {
    ElMessage.error(response.message || '图片上传失败');
  }
}

const handleRemove = (file) => {
  const url = file.response?.data?.url
  const index = form.photos.indexOf(url)
  if (index !== -1) form.photos.splice(index, 1)
}

const resetForm = () => {
  formRef.value.resetFields()
  form.tags = []
  form.photos = []
}

const onSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 这里的字段结构必须和后端的 HouseUploadDTO 一一对应
      const submitData = {
        title: form.title,
        communityId: form.communityId,
        price: form.price,
        area: form.area,
        roomCount: form.roomCount,
        hallCount: form.hallCount,
        floor: form.floor,
        totalFloor: form.totalFloor,
        direction: form.direction,
        description: form.description,
        tags: form.tags, // 发送数组给后端
        photos: form.photos
      }

      const { data } = await axios.post('/api/house/upload', submitData)
      
      if (data.code === 200) {
        ElMessage.success('发布成功！房源已在架。')
        resetForm()
      } else {
        ElMessage.error(data.message || '发布失败')
      }
    } catch (err) {
      console.error(err)
      ElMessage.error('服务器响应异常，请检查网络')
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.house-upload-container {
  padding-bottom: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.form-header p {
  color: #909399;
  font-size: 14px;
}

.form-section {
  margin-bottom: 20px;
  border-radius: 8px;
}

.section-title {
  font-weight: 600;
  color: #409EFF;
  font-size: 16px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.upload-form :deep(.el-form-item__label) {
  font-weight: 500;
  padding-bottom: 4px;
}

/* 适配移动端 */
@media (max-width: 768px) {
  .el-col {
    width: 100% !important;
  }
}
</style>