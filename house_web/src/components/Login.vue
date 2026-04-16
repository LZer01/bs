<template>
  <div class="login-wrapper">
    <el-card class="login-card" shadow="always">
      <!-- 标题 -->
      <div class="login-title">
        <h2>🏠 租房管理系统</h2>
        <p style="color: #909399; font-size: 14px; margin-top: 8px;">欢迎登录，请先输入账号密码</p>
      </div>

      <!-- 错误次数提示 -->
      <el-alert
        v-if="loginAttempts >= 5"
        title="账号已锁定"
        type="error"
        description="密码错误次数过多，账号已被锁定30分钟，请稍后再试"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />
      <el-alert
        v-else-if="loginAttempts > 0 && loginAttempts < 5"
        :title="`密码错误，还剩${5 - loginAttempts}次尝试机会`"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <!-- 表单 -->
      <el-form :model="form" label-position="top" size="large">
        <el-form-item label="账号">
          <el-input 
            v-model="form.username" 
            placeholder="请输入账号" 
            prefix-icon="el-icon-user"
            @keyup.enter.native="toHome"
            :disabled="isLocked"
          />
        </el-form-item>
        
        <el-form-item label="密码">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="el-icon-lock"
            @keyup.enter.native="toHome"
            :disabled="isLocked"
          />
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item label="验证码">
          <div class="captcha-container">
            <el-input 
              v-model="form.captcha" 
              placeholder="请输入验证码" 
              style="flex: 1;"
              @keyup.enter.native="toHome"
              :disabled="isLocked"
            />
            <div class="captcha-code" @click="refreshCaptcha">
              <canvas ref="captchaCanvas" width="120" height="40"></canvas>
              <span class="refresh-icon" title="点击刷新">🔄</span>
            </div>
          </div>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            style="width: 100%; height: 45px; font-size: 16px;" 
            @click="toHome"
            :loading="loading"
            :disabled="isLocked"
          >
            {{ loading ? '登录中...' : (isLocked ? '账号已锁定' : '立即登录') }}
          </el-button>
        </el-form-item>

        <!-- 解锁倒计时 -->
        <div v-if="isLocked && lockRemainTime > 0" class="lock-timer">
          <i class="el-icon-time"></i>
          账号将在 {{ formatLockTime(lockRemainTime) }} 后自动解锁
        </div>
      </el-form>

      <!-- 底部链接 -->
      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="goRegister" :disabled="isLocked">立即注册</el-link>
        <el-link type="info" @click="resetPassword" style="margin-left: 10px;" :disabled="isLocked">忘记密码？</el-link>
      </div>
    </el-card>
  </div>
</template>

<script>
// 导入API
import { authApi } from '@/api/auth' 

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: '',
        captcha: '' // 添加验证码字段
      },
      loading: false,
      currentCaptcha: '', // 存储当前验证码的值
      loginAttempts: 0, // 当前登录尝试次数
      lockUntil: null, // 锁定结束时间戳
      lockTimer: null, // 倒计时定时器
      lockRemainTime: 0 // 剩余锁定时间（秒）
    }
  },
  computed: {
    // 是否被锁定
    isLocked() {
      if (!this.lockUntil) return false
      const now = Date.now()
      if (now >= this.lockUntil) {
        // 锁定时间已过，清除锁定状态
        this.clearLock()
        return false
      }
      return true
    }
  },
  mounted() {
    this.refreshCaptcha() // 组件挂载时生成验证码
    this.checkLockStatus() // 检查锁定状态
  },
  beforeDestroy() {
    // 清除定时器
    if (this.lockTimer) {
      clearInterval(this.lockTimer)
    }
  },
  methods: {
    // 检查锁定状态
    checkLockStatus() {
      const username = this.form.username
      if (!username) return
      
      // 从 localStorage 获取该账号的锁定信息
      const lockKey = `login_lock_${username}`
      const lockData = localStorage.getItem(lockKey)
      
      if (lockData) {
        const { lockUntil, attempts } = JSON.parse(lockData)
        const now = Date.now()
        
        if (now < lockUntil) {
          // 还在锁定期间
          this.lockUntil = lockUntil
          this.loginAttempts = attempts
          this.startLockTimer()
        } else {
          // 锁定已过期，清除记录
          localStorage.removeItem(lockKey)
          this.loginAttempts = 0
          this.lockUntil = null
        }
      } else {
        // 获取该账号的尝试次数
        const attemptsKey = `login_attempts_${username}`
        const attempts = localStorage.getItem(attemptsKey)
        this.loginAttempts = attempts ? parseInt(attempts) : 0
      }
    },
    
    // 记录登录失败
    recordFailedAttempt() {
      const username = this.form.username
      if (!username) return
      
      // 增加尝试次数
      this.loginAttempts++
      
      // 保存尝试次数
      const attemptsKey = `login_attempts_${username}`
      localStorage.setItem(attemptsKey, this.loginAttempts.toString())
      
      // 如果达到5次，锁定账号30分钟
      if (this.loginAttempts >= 5) {
        const lockUntil = Date.now() + 30 * 60 * 1000 // 30分钟
        const lockKey = `login_lock_${username}`
        localStorage.setItem(lockKey, JSON.stringify({
          lockUntil: lockUntil,
          attempts: this.loginAttempts
        }))
        
        this.lockUntil = lockUntil
        this.startLockTimer()
        
        // 清除尝试次数记录
        localStorage.removeItem(attemptsKey)
        
        this.$message.error('密码错误次数过多，账号已被锁定30分钟！')
      } else {
        this.$message.error(`密码错误，还剩${5 - this.loginAttempts}次尝试机会`)
      }
    },
    
    // 清除锁定状态
    clearLock() {
      const username = this.form.username
      if (username) {
        const lockKey = `login_lock_${username}`
        const attemptsKey = `login_attempts_${username}`
        localStorage.removeItem(lockKey)
        localStorage.removeItem(attemptsKey)
      }
      
      this.loginAttempts = 0
      this.lockUntil = null
      this.lockRemainTime = 0
      
      if (this.lockTimer) {
        clearInterval(this.lockTimer)
        this.lockTimer = null
      }
    },
    
    // 登录成功时清除错误记录
    clearFailedAttempts() {
      const username = this.form.username
      if (username) {
        const lockKey = `login_lock_${username}`
        const attemptsKey = `login_attempts_${username}`
        localStorage.removeItem(lockKey)
        localStorage.removeItem(attemptsKey)
      }
      this.loginAttempts = 0
      this.lockUntil = null
      if (this.lockTimer) {
        clearInterval(this.lockTimer)
        this.lockTimer = null
      }
    },
    
    // 启动锁定倒计时
    startLockTimer() {
      if (this.lockTimer) {
        clearInterval(this.lockTimer)
      }
      
      this.updateLockRemainTime()
      
      this.lockTimer = setInterval(() => {
        this.updateLockRemainTime()
        if (this.lockRemainTime <= 0) {
          // 倒计时结束，清除锁定
          this.clearLock()
          this.$message.success('账号已解锁，请重新尝试登录')
        }
      }, 1000)
    },
    
    // 更新剩余锁定时间
    updateLockRemainTime() {
      if (this.lockUntil) {
        const remain = Math.max(0, Math.floor((this.lockUntil - Date.now()) / 1000))
        this.lockRemainTime = remain
        if (remain <= 0) {
          this.clearLock()
        }
      } else {
        this.lockRemainTime = 0
      }
    },
    
    // 格式化锁定时间显示
    formatLockTime(seconds) {
      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      if (minutes > 0) {
        return `${minutes}分${secs}秒`
      }
      return `${secs}秒`
    },
    
    // 生成随机验证码（算术表达式）
    generateCaptcha() {
      const num1 = Math.floor(Math.random() * 10) + 1 // 1-10
      const num2 = Math.floor(Math.random() * 10) + 1 // 1-10
      const operators = ['+', '-']
      const operator = operators[Math.floor(Math.random() * operators.length)]
      
      let expression = `${num1} ${operator} ${num2}`
      let result
      
      if (operator === '+') {
        result = num1 + num2
      } else {
        // 确保结果不为负数
        if (num1 >= num2) {
          result = num1 - num2
        } else {
          // 如果num1 < num2，交换位置
          expression = `${num2} - ${num1}`
          result = num2 - num1
        }
      }
      
      return { expression, result: result.toString() }
    },
    
    // 绘制验证码到canvas
    drawCaptcha(expression) {
      const canvas = this.$refs.captchaCanvas
      if (!canvas) return
      
      const ctx = canvas.getContext('2d')
      const width = canvas.width
      const height = canvas.height
      
      // 清空画布
      ctx.clearRect(0, 0, width, height)
      
      // 设置背景色
      ctx.fillStyle = '#f0f0f0'
      ctx.fillRect(0, 0, width, height)
      
      // 绘制干扰线
      for (let i = 0; i < 5; i++) {
        ctx.beginPath()
        ctx.moveTo(Math.random() * width, Math.random() * height)
        ctx.lineTo(Math.random() * width, Math.random() * height)
        ctx.strokeStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.5)`
        ctx.stroke()
      }
      
      // 绘制干扰点
      for (let i = 0; i < 50; i++) {
        ctx.fillStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.5)`
        ctx.fillRect(Math.random() * width, Math.random() * height, 2, 2)
      }
      
      // 绘制验证码文字
      ctx.font = 'bold 20px Arial'
      ctx.fillStyle = '#333'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      
      // 添加文字阴影效果
      ctx.shadowBlur = 2
      ctx.shadowColor = 'rgba(0,0,0,0.3)'
      ctx.fillText(expression, width / 2, height / 2)
      ctx.shadowBlur = 0
      
      // 添加轻微的旋转扭曲效果
      ctx.save()
      ctx.translate(width / 2, height / 2)
      ctx.rotate((Math.random() - 0.5) * 0.1)
      ctx.fillText(expression, 0, 0)
      ctx.restore()
    },
    
    // 刷新验证码
    refreshCaptcha() {
      const { expression, result } = this.generateCaptcha()
      this.currentCaptcha = result
      this.drawCaptcha(expression)
    },
    
    // 验证验证码
    validateCaptcha() {
      if (!this.form.captcha) {
        this.$message.warning('请输入验证码')
        return false
      }
      
      if (this.form.captcha !== this.currentCaptcha) {
        this.$message.error('验证码错误')
        this.form.captcha = '' // 清空输入
        this.refreshCaptcha() // 刷新验证码
        return false
      }
      
      return true
    },
    
    async toHome() {
      const { username, password } = this.form
      
      // 检查是否被锁定
      if (this.isLocked) {
        this.$message.warning(`账号已被锁定，请在${this.formatLockTime(this.lockRemainTime)}后重试`)
        return
      }
      
      // 验证表单
      if (!username || !password) {
        this.$message.warning('请输入账号和密码')
        return
      }
      
      // 验证验证码
      if (!this.validateCaptcha()) {
        return
      }
      
      this.loading = true
      try {
        // 调用后端接口进行账号密码校验
        const res = await authApi.login({ 
          username: username, 
          password: password
        })

        // console.log('登录接口返回值:', res.data.code)
        // 根据后端返回的状态码判断
        if (res.data.code === 200) {
          // 登录成功
          const data = res.data.data

          localStorage.setItem('token', data.token)
          localStorage.setItem('user', JSON.stringify(data.user))
          localStorage.setItem('id', data.id)
          window.dispatchEvent(new Event('storage'))
          // 登录成功，清除失败记录
          this.clearFailedAttempts()
          this.$message.success('登录成功')
          this.$router.push('/')
          setTimeout(() => {
          window.location.href = '/'   // 或 this.$router.go(0)
        }, 800)
        } else {
          // 登录失败（账号或密码错误）
          // 记录失败次数
          this.recordFailedAttempt()
          
          // 显示后端返回的错误信息
          const errorMsg = res.message || '账号或密码错误'
          this.$message.error(errorMsg)
          
          // 刷新验证码
          this.refreshCaptcha()
          this.form.captcha = ''
          this.form.password = '' // 清空密码框（可选）
        }
      } catch (err) {
        // 网络请求失败或其他错误
        console.error('登录请求失败:', err)
        
        // 根据错误码判断是否是账号密码错误
        if (err.response) {
          const { status, data } = err.response
          if (status === 401) {
            // 未授权，账号或密码错误
            this.recordFailedAttempt()
            this.$message.error(data.message || '账号或密码错误')
          } else if (status === 403) {
            // 账号被锁定
            this.$message.error(data.message || '账号已被锁定，请稍后再试')
          } else {
            this.$message.error('网络请求失败，请稍后重试')
          }
        } else {
          this.$message.error('网络连接失败，请检查网络')
        }
        
        // 刷新验证码
        this.refreshCaptcha()
        this.form.captcha = ''
      } finally {
        this.loading = false
      }
    },
    
    goRegister() {
      if (this.isLocked) {
        this.$message.warning('账号已锁定，无法注册')
        return
      }
      this.$router.push('/register')
    },
    
    resetPassword() {
      if (this.isLocked) {
        this.$message.warning('账号已锁定，请解锁后再试')
        return
      }
      // 跳转到找回密码页面
      this.$router.push('/reset-password')
    }
  },
  watch: {
    'form.username': {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          // 切换账号时重新检查锁定状态
          this.checkLockStatus()
        }
      }
    }
  }
}
</script>

<style scoped>
/* 全屏居中背景 */
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
  padding: 10px;
}

/* 标题区域 */
.login-title {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.login-title h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

/* 表单区域 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  padding: 0 0 8px 0;
}

:deep(.el-input__inner) {
  border-radius: 8px;
}

/* 验证码容器样式 */
.captcha-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-code {
  display: flex;
  align-items: center;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;
  transition: all 0.3s;
}

.captcha-code:hover {
  border-color: #409eff;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.3);
}

.captcha-code canvas {
  display: block;
  cursor: pointer;
}

.refresh-icon {
  padding: 0 8px;
  font-size: 18px;
  transition: transform 0.3s;
  user-select: none;
}

.captcha-code:hover .refresh-icon {
  transform: rotate(180deg);
}

/* 锁定倒计时样式 */
.lock-timer {
  text-align: center;
  margin-top: -10px;
  margin-bottom: 15px;
  color: #f56c6c;
  font-size: 13px;
}

.lock-timer i {
  margin-right: 5px;
}

/* 底部链接 */
.login-footer {
  text-align: center;
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.login-footer .el-link {
  font-weight: 500;
  margin-left: 4px;
}

/* 移动端适配 */
@media (max-width: 480px) {
  .login-card {
    max-width: 100%;
  }
  
  .captcha-container {
    flex-direction: column;
  }
  
  .captcha-code {
    width: 100%;
    justify-content: center;
  }
}
</style>