<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="auth-logo">
            <div class="logo-icon-glow">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
            <h1 class="auth-title">用户管理系统</h1>
          </div>
          <p class="auth-subtitle">欢迎回来，请登录您的账户</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="auth-form"
          @keyup.enter="handleLogin"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              clearable
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="auth-btn"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <div class="auth-footer">
          <span>还没有账户？</span>
          <router-link to="/register" class="auth-link">立即注册</router-link>
        </div>

        <div class="floating-icons">
          <div class="float-icon" style="top:10%;left:5%;animation-delay:0s">
            <el-icon :size="20"><BellFilled /></el-icon>
          </div>
          <div class="float-icon" style="top:60%;left:3%;animation-delay:-3s">
            <el-icon :size="16"><StarFilled /></el-icon>
          </div>
          <div class="float-icon" style="top:80%;right:8%;animation-delay:-6s">
            <el-icon :size="18"><ChatDotSquare /></el-icon>
          </div>
          <div class="float-icon" style="top:20%;right:4%;animation-delay:-9s">
            <el-icon :size="14"><HelpFilled /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <SliderCaptcha
      :visible="showCaptcha"
      @verified="onCaptchaVerified"
      @close="onCaptchaClose"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../api/user'
import SliderCaptcha from '../components/SliderCaptcha.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const showCaptcha = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '用户名必须是5-16位非空字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '密码必须是5-16位非空字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  showCaptcha.value = true
}

function onCaptchaClose() {
  showCaptcha.value = false
}

async function onCaptchaVerified() {
  showCaptcha.value = false
  loading.value = true
  try {
    const res = await login(form.username, form.password)
    const { token, user } = res.data
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('登录成功！欢迎回来')
    router.push('/dashboard')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow-y: auto;
}

.auth-container {
  width: 100%;
  max-width: 440px;
  perspective: 1000px;
}

.auth-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 40px 36px;
  position: relative;
  overflow: hidden;
  animation: cardAppear 0.6s ease;
}

@keyframes cardAppear {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.auth-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(ellipse at 30% 20%, rgba(108, 99, 255, 0.03) 0%, transparent 60%);
  pointer-events: none;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.logo-icon-glow {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 32px rgba(108, 99, 255, 0.3);
  animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { box-shadow: 0 8px 32px rgba(108, 99, 255, 0.3); }
  50% { box-shadow: 0 8px 48px rgba(108, 99, 255, 0.5); }
}

.auth-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, rgba(255,255,255,0.7));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.auth-subtitle {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 8px;
}

.auth-form {
  position: relative;
  z-index: 1;
}

.auth-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border: none;
  margin-top: 8px;
  transition: all 0.3s;
  letter-spacing: 4px;
}

.auth-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(108, 99, 255, 0.4);
}

.auth-btn.is-disabled {
  opacity: 0.5;
  background: var(--primary-dark);
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-secondary);
}

.auth-link {
  color: var(--primary-light);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: color 0.3s;
}

.auth-link:hover {
  color: var(--accent);
  text-decoration: underline;
}

.floating-icons {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.float-icon {
  position: absolute;
  color: rgba(255, 255, 255, 0.06);
  animation: floatIcon 6s ease-in-out infinite;
}

@keyframes floatIcon {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.06; }
  50% { transform: translateY(-15px) rotate(15deg); opacity: 0.12; }
}
</style>