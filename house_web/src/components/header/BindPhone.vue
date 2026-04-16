<template>
  <div>
    <h2>绑定手机号</h2>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="当前手机号">
        <span>{{ currentPhone || '未绑定' }}</span>
      </el-form-item>
      <el-form-item label="新手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="验证码">
        <el-input v-model="form.code" style="width: 200px" />
        <el-button @click="sendCode" :disabled="countdown > 0" style="margin-left: 10px">
          {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="bindPhone">绑定手机号</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const currentPhone = ref('138****1234') // 从接口获取

const form = ref({
  phone: '',
  code: ''
})

const countdown = ref(0)
let timer = null

const sendCode = () => {
  if (!form.value.phone) {
    ElMessage.error('请输入手机号')
    return
  }
  countdown.value = 60
  // 调用发送验证码接口

  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const bindPhone = () => {
  // 调用绑定接口
  ElMessage.success('手机号绑定成功！')
}
</script>