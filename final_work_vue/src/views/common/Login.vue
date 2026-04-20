<template>
  <ParticleBackground>
    <div class="login-wrapper">
      
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-logo">
            <div class="logo-icon">
              <div class="logo-inner"></div>
            </div>
            <span>Talent Pro</span>
          </div>
          <h2 class="brand-title">连接顶尖人才<br/><span class="text-highlight">与卓越企业</span></h2>
          <p class="brand-desc">智能化的高效招聘服务平台，提供精准匹配与安全可靠的数据保障，开启您的职业新篇章。</p>
          
          <div class="brand-features">
            <div class="feature-item">
              <div class="feature-icon"><el-icon><Check /></el-icon></div>
              <span>高效协同沟通</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><el-icon><Check /></el-icon></div>
              <span>AI 智能精准匹配</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><el-icon><Check /></el-icon></div>
              <span>企业级数据隐私保护</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-container">
        <div class="login-header">
          <div class="login-title">欢迎登录</div>
          <p class="login-subtitle">请输入您的账户信息进入平台</p>
        </div>
        
        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          :rules="rules"
          class="login-form"
          @submit.prevent
        >
          <el-form-item prop="username">
            <el-input
              v-model="ruleForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              class="custom-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="ruleForm.password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
              type="password"
              class="custom-input"
            />
          </el-form-item>

          <transition name="fade">
            <el-alert
              v-if="errorMessage"
              :title="errorMessage"
              type="error"
              show-icon
              class="custom-alert"
              @close="errorMessage = ''"
            />
          </transition>

          <el-form-item class="button-group">
            <el-button
              type="primary"
              size="large"
              class="btn-primary"
              @click="submitForm(ruleFormRef)"
              :loading="loading"
            >
              登 录
            </el-button>
            <el-button
              size="large"
              class="btn-secondary"
              @click="registerBtn"
            >
              注册账号
            </el-button>
          </el-form-item>

          <div class="other-login">
            <div class="divider">
              <span>或通过以下方式</span>
            </div>
            <button @click="gotoFaceLogin" class="face-login-btn" type="button">
              <div class="icon-wrapper">
                <el-icon class="link-icon"><Camera /></el-icon>
              </div>
              <span>人脸识别极速登录</span>
            </button>
          </div>
        </el-form>
      </div>

    </div>
  </ParticleBackground>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { Camera, User, Lock, Check } from '@element-plus/icons-vue' 

import { getLoginByUsername } from '@/api/login.ts'
import { setAuthStatus } from '@/utils/auth'
import { useRoleStore } from '@/stores/role'
import { useAuthStore } from '@/stores/auth'
// 导入抽离出去的背景组件
import ParticleBackground from '@/components/background.vue'

const router = useRouter()
const ruleFormRef = ref<FormInstance>()
const loading = ref(false)
const errorMessage = ref('')
const roleStore = useRoleStore()
const authStore = useAuthStore()

interface RuleForm {
  username: string
  password: string
}

const ruleForm = reactive<RuleForm>({
  username: '',
  password: '',
})

const rules = reactive<FormRules<RuleForm>>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
})

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  try {
    loading.value = true
    errorMessage.value = ''
    await formEl.validate()
    await login()
  } catch (error) {
    console.log('表单验证失败', error)
  } finally {
    loading.value = false
  }
}

const login = async () => {
  try {
    const res = await getLoginByUsername(ruleForm.username)

    console.log('登录结果:', res.data)
    const { role } = res.data
    if (ruleForm.password === res.data.password) {
      setAuthStatus(true, ruleForm.username)
      await handleRouteByRole(role)
    } else {
      errorMessage.value = '用户名或密码错误'
    }
  } catch (error) {
    console.error('登录失败:', error)
    errorMessage.value = '用户名或密码错误'
  }
}

const registerBtn = () => {
  console.log('即将进入注册页面')
  router.push('/register')
}

const handleRouteByRole = async (role:any) => {
  let roleValue = 'user'
  if (role == 2) {
    roleValue = 'admin'
    await router.push('/admin')
  } else if (role == 1) {
    roleValue = 'company'
    localStorage.setItem('companyUsername', ruleForm.username)
    await router.push('/company')
  } else if (role == 0) {
    roleValue = 'user'
    await router.push('/shouye')
  }
  roleStore.setRole(roleValue)
}

const gotoFaceLogin = () => {
  console.log('触发了人脸识别登录跳转');
  router.push('/face-login')
}

// 生命周期：初始化 Store
onMounted(() => {
  roleStore.initRole()
  authStore.initAuth()
})
</script>

<style scoped>
/* 字体统一 */
* {
  box-sizing: border-box;
}

/* 主卡片容器 */
.login-wrapper {
  position: relative;
  z-index: 10;
  display: flex;
  width: 1000px;
  height: 580px;
  background: transparent;
  border-radius: 24px;
  box-shadow: 
    0 50px 100px -20px rgba(0, 0, 0, 0.8),
    0 30px 60px -30px rgba(249, 115, 22, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

/* ========== 左侧：品牌展示区 ========== */
.login-brand {
  flex: 1.1;
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  padding: 60px 50px;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 2;
}

.brand-logo {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 50px;
  letter-spacing: 1px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 10px;
  margin-right: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4);
}

.logo-inner {
  width: 12px;
  height: 12px;
  background: #ffffff;
  border-radius: 3px;
  transform: rotate(45deg);
}

.brand-title {
  font-size: 36px;
  line-height: 1.4;
  margin-bottom: 20px;
  font-weight: 700;
  letter-spacing: 1px;
}

.text-highlight {
  color: #f97316;
  position: relative;
}

.brand-desc {
  font-size: 15px;
  color: #9ca3af;
  line-height: 1.8;
  margin-bottom: 40px;
  max-width: 90%;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  font-size: 15px;
  color: #e5e7eb;
  font-weight: 500;
}

.feature-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: rgba(249, 115, 22, 0.15);
  color: #f97316;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  font-size: 16px;
  border: 1px solid rgba(249, 115, 22, 0.3);
}

/* ========== 右侧：登录表单区 ========== */
.login-container {
  flex: 1;
  padding: 60px 55px;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
}

.login-header {
  margin-bottom: 45px;
}

.login-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
}

.login-title::after {
  content: "";
  display: block;
  width: 30px;
  height: 4px;
  background: #f97316;
  margin-top: 12px;
  border-radius: 2px;
}

.login-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.login-form {
  width: 100%;
}

/* 高级定制 Input */
:deep(.custom-input .el-input__wrapper) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 10px 16px;
}

:deep(.custom-input .el-input__wrapper:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  background-color: #ffffff;
  box-shadow: 
    0 0 0 1px #f97316, 
    0 0 0 4px rgba(249, 115, 22, 0.15), 
    inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

:deep(.custom-input .el-input__inner) {
  color: #1f2937;
  font-weight: 500;
  font-size: 15px;
  height: 26px;
}

:deep(.custom-input .el-input__prefix) {
  color: #94a3b8;
  font-size: 18px;
  margin-right: 8px;
}

.custom-alert {
  margin-bottom: 24px;
  border-radius: 8px;
}

/* 按钮组 */
.button-group {
  margin-top: 40px;
  margin-bottom: 30px;
}

:deep(.button-group .el-form-item__content) {
  display: flex;
  gap: 16px;
}

/* 主操作按钮 */
.btn-primary {
  flex: 1.5;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  font-size: 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.5);
  height: 48px;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.65);
}

.btn-primary:active {
  transform: translateY(0);
}

/* 次级操作按钮 */
.btn-secondary {
  flex: 1;
  background: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  font-weight: 600;
  font-size: 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  height: 48px;
}

.btn-secondary:hover {
  border-color: #f97316 !important;
  color: #f97316 !important;
  background: #fffcf9 !important;
}

/* 其他登录方式 */
.other-login {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.divider {
  width: 100%;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.divider::before, .divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.divider span {
  padding: 0 15px;
  color: #94a3b8;
  font-size: 13px;
}

.face-login-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 10px;
  padding: 12px;
  color: #475569;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.face-login-btn .icon-wrapper {
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.face-login-btn .link-icon {
  font-size: 20px;
  color: #64748b;
  transition: all 0.3s ease;
}

.face-login-btn:hover {
  background: #ffffff;
  border-color: #f97316;
  border-style: solid;
  color: #f97316;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.08);
}

.face-login-btn:hover .link-icon {
  color: #f97316;
}

/* 提示动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>