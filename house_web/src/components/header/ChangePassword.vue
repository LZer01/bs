<template>
  <div>
    <h2>修改密码</h2>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="原密码">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">确认修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const submit = () => {
  if (form.value.newPassword !== form.value.confirmPassword) {
    ElMessage.error('两次新密码输入不一致')
    return
  }
  // 调用修改密码接口
  ElMessage.success('密码修改成功！')
}
</script>