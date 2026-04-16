<template>
  <div class="register-wrapper">
    <el-card class="register-card" shadow="always">
      <!-- 标题 -->
      <div class="register-title">
        <h2>🏠 账号注册</h2>
        <p style="color: #909399; font-size: 14px; margin-top: 8px;">创建您的租房管理账号</p>
      </div>

      <!-- 表单 -->
      <el-form 
        ref="registerFormRef"
        :model="form" 
        :rules="rules"
        label-position="top" 
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名 (3-12位字符)" 
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号" 
            prefix-icon="Phone"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码 (6-16位)" 
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="checkPassword">
          <el-input 
            v-model="form.checkPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            style="width: 100%; height: 45px; font-size: 16px;" 
            @click="handleRegister"
            :loading="loading"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部链接 -->
      <div class="register-footer">
        <span>已有账号？</span>
        <el-link type="primary" @click="goLogin">去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 假设你有一个 authApi，如果没有请先创建或取消注释下面的模拟
import { authApi } from '@/api/auth' 

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  phone: '',
  password: '',
  checkPassword: ''
})

// 自定义校验规则：确认密码
const validateCheckPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 12, message: '长度在 3 到 12 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  checkPassword: [
    { validator: validateCheckPassword, trigger: 'blur' }
  ]
})

// 注册处理函数
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构造发送给后端的数据，通常不包含 checkPassword
        const registerData = {
          username: form.username,
          phone: form.phone,
          password: form.password
        }

        // 调用注册接口
        // 注意：请确保你的 authApi 中有 register 方法
        const res = await authApi.register(registerData)
        
        if (res.status === 200 || res.success) {
          ElMessage.success('注册成功，请登录')
          // 注册成功后跳转到登录页
          setTimeout(() => {
            router.push('/login')
          }, 1000)
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (err) {
        console.error(err)
        ElMessage.error('网络请求异常，请稍后重试')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写完整的注册信息')
      return false
    }
  })
}

// 跳转登录
const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 全屏居中背景 - 与登录页保持一致 */
.register-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 注册卡片 */
.register-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
  padding: 10px;
}

/* 标题区域 */
.register-title {
  text-align: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.register-title h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

/* 表单区域微调 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  padding: 0 0 8px 0;
}

:deep(.el-input__inner) {
  border-radius: 8px;
}

/* 底部链接 */
.register-footer {
  text-align: center;
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.register-footer .el-link {
  font-weight: 500;
  margin-left: 4px;
}

/* 移动端适配 */
@media (max-width: 480px) {
  .register-card {
    max-width: 100%;
  }
}
</style>