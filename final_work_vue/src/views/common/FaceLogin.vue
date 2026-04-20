<template>
  <div class="login-bg">
    <div class="bg-glow glow-orange"></div>
    <div class="bg-glow glow-blue"></div>

    <div class="login-container">
      <div class="login-header">
        <h2 class="login-title">人脸识别登录</h2>
        <p class="login-subtitle">请正对摄像头，进行身份核验</p>
      </div>

      <el-form
        ref="faceFormRef"
        :model="faceForm"
        :rules="rules"
        class="login-form"
        @submit.prevent
      >
        <el-form-item prop="username">
          <el-input
            v-model="faceForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item>
          <div class="camera-container" :class="{ 'is-active': streamRef }">
            <div class="camera-placeholder" v-show="!streamRef">
              <div class="placeholder-icon">📷</div>
              <span>等待开启摄像头...</span>
            </div>
            
            <video
              ref="videoRef"
              autoplay
              playsinline
              class="camera-preview"
              v-show="streamRef"
            ></video>
            
            <div class="scan-frame" v-show="streamRef">
              <div class="corner top-left"></div>
              <div class="corner top-right"></div>
              <div class="corner bottom-left"></div>
              <div class="corner bottom-right"></div>
            </div>

            <canvas
              ref="canvasRef"
              class="camera-canvas"
              style="display: none"
            ></canvas>
          </div>
        </el-form-item>

        <transition name="fade">
          <el-alert
            v-if="message"
            :title="message"
            :type="messageType"
            show-icon
            class="custom-alert"
            @close="message = ''"
          />
        </transition>

        <transition name="fade">
          <div v-if="isVerifying" class="verifying-status">
            <el-progress 
              :percentage="verifyProgress" 
              :stroke-width="6" 
              color="#f97316"
              :show-text="false"
            />
            <p class="verify-text">正在验证人脸特征... <span class="time-highlight">{{ remainingTime }}s</span></p>
          </div>
        </transition>

        <div class="action-buttons">
          <el-button
            size="large"
            class="btn-secondary"
            @click="startCamera"
            :loading="loading || isVerifying"
          >
            开启摄像头
          </el-button>
          <el-button
            type="primary"
            size="large"
            class="btn-primary"
            @click="captureAndVerify"
            :loading="loading || isVerifying"
          >
            拍摄并验证
          </el-button>
        </div>

        <div class="other-login">
          <div class="divider">
            <span>或</span>
          </div>
          <button @click="gotoNormalLogin" class="back-login-btn" type="button">
            <span>返回普通登录</span>
          </button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { setAuthStatus } from '@/utils/auth'
import { useRoleStore } from '@/stores/role'
import { ElMessage } from 'element-plus'

const router = useRouter()
const faceFormRef = ref<FormInstance>()
const videoRef = ref<HTMLVideoElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
const loading = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const roleStore = useRoleStore()
const streamRef = ref<MediaStream | null>(null)

// 新增验证状态相关变量
const isVerifying = ref(false) // 是否正在验证
const verifyProgress = ref(0) // 验证进度
const remainingTime = ref(5) // 剩余时间（改为5秒）

// 默认用户名设置为1111
interface FaceForm {
  username: string
}

const faceForm = reactive<FaceForm>({
  username: '', // 默认用户名
})

const rules = reactive<FormRules<FaceForm>>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
})

// 开启摄像头
const startCamera = async () => {
  try {
    loading.value = true
    message.value = ''

    if (!faceForm.username) {
      message.value = '请先输入用户名'
      messageType.value = 'error'
      loading.value = false
      return
    }

    const stream = await navigator.mediaDevices.getUserMedia({
      video: { width: 400, height: 300 }
    })

    if (videoRef.value) {
      videoRef.value.srcObject = stream
      streamRef.value = stream
    }
  } catch (error) {
    console.error('摄像头开启失败:', error)
    message.value = '摄像头开启失败，请确保已授予权限'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

// 拍摄并验证人脸
const captureAndVerify = async () => {
  if (!videoRef.value || !canvasRef.value || !streamRef.value) {
    message.value = '请先开启摄像头'
    messageType.value = 'error'
    return
  }

  try {
    loading.value = true
    message.value = ''

    // 绘制视频帧
    const canvas = canvasRef.value
    canvas.width = 400
    canvas.height = 300
    const ctx = canvas.getContext('2d')
    ctx?.drawImage(videoRef.value, 0, 0, canvas.width, canvas.height)

    // 转换为base64
    const faceImage = canvas.toDataURL('image/jpeg')
    console.log('拍摄的人脸图像:', faceImage)

    // 模拟密码验证（实际项目中应调用后端接口）
    const defaultPassword = '123456789' // 默认密码
    console.log(`使用默认密码验证: ${defaultPassword}`)

    // 显示验证中状态
    message.value = '正在进行人脸验证，请稍候...'
    messageType.value = 'success'
    isVerifying.value = true
    verifyProgress.value = 0
    remainingTime.value = 5 // 改为5秒

    // 关闭摄像头
    streamRef.value.getTracks().forEach(track => track.stop())
    streamRef.value = null

    // 5秒倒计时（修改部分）
    const totalSeconds = 5 // 改为5秒
    const interval = setInterval(() => {
      remainingTime.value--
      verifyProgress.value = ((totalSeconds - remainingTime.value) / totalSeconds) * 100

      if (remainingTime.value <= 0) {
        clearInterval(interval)
        verifyProgress.value = 100
      }
    }, 1000)

    // 等待5秒后再执行登录逻辑（修改部分）
    await new Promise(resolve => setTimeout(resolve, 5000))

    // 验证完成后显示结果
    message.value = '人脸验证成功'
    messageType.value = 'success'

    // 设置登录状态
    setAuthStatus(true, faceForm.username)
    // 假设用户角色为普通用户
    await handleRouteByRole(0)
    ElMessage.success('登录成功')

  } catch (error) {
    console.error('操作失败:', error)
    message.value = '操作失败，请重试'
    messageType.value = 'error'
  } finally {
    // 重置验证状态
    isVerifying.value = false
    loading.value = false
  }
}

// 返回普通登录
const gotoNormalLogin = () => {
  if (streamRef.value) {
    streamRef.value.getTracks().forEach(track => track.stop())
    streamRef.value = null
  }
  router.push('/login')
}

// 根据角色跳转路由
const handleRouteByRole = async (role: any) => {
  let roleValue = 'user'
  if (role == 2) {
    roleValue = 'admin'
    await router.push('/admin')
  } else if (role == 1) {
    roleValue = 'company'
    localStorage.setItem('companyUsername', faceForm.username)
    await router.push('/company')
  } else if (role == 0) {
    roleValue = 'user'
    await router.push('/shouye')
  }
  roleStore.setRole(roleValue)
}
</script>

<style scoped>
/* ================= 全局 & 容器样式 ================= */
.login-bg {
  min-height: 100vh;
  /* 同步登录页渐变深空灰背景 */
  background: linear-gradient(180deg, #15181c 0%, #434955 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

/* 背景模糊光晕 */
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
}
.glow-orange {
  width: 700px;
  height: 700px;
  background: rgba(249, 115, 22, 0.05);
  top: -200px;
  left: -100px;
}
.glow-blue {
  width: 800px;
  height: 800px;
  background: rgba(56, 189, 248, 0.03);
  bottom: -300px;
  right: -200px;
}

/* 主面板容器 */
.login-container {
  width: 100%;
  max-width: 440px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 
    0 50px 100px -20px rgba(0, 0, 0, 0.8),
    0 30px 60px -30px rgba(249, 115, 22, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  padding: 50px 45px 40px;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 10;
}

/* ================= 头部样式 ================= */
.login-header {
  text-align: center;
  margin-bottom: 35px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 8px;
  letter-spacing: 0.5px;
  display: flex;
  flex-direction: column;
  align-items: center;
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
  color: #6b7280;
  font-size: 15px;
  margin-top: 10px;
}

/* ================= 输入框定制 (同步) ================= */
.login-form {
  width: 100%;
}

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

/* ================= 摄像头容器定制 ================= */
.camera-container {
  width: 100%;
  height: 260px;
  margin: 10px 0 24px;
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.camera-container.is-active {
  border-style: solid;
  border-color: #f97316;
  background: #000;
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.15);
}

.camera-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #94a3b8;
}

.placeholder-icon {
  font-size: 40px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.camera-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 科技感扫描角标 */
.scan-frame {
  position: absolute;
  top: 15px;
  left: 15px;
  right: 15px;
  bottom: 15px;
  pointer-events: none;
  z-index: 5;
}

.scan-frame .corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 3px solid #f97316;
}
.scan-frame .top-left { top: 0; left: 0; border-right: none; border-bottom: none; }
.scan-frame .top-right { top: 0; right: 0; border-left: none; border-bottom: none; }
.scan-frame .bottom-left { bottom: 0; left: 0; border-right: none; border-top: none; }
.scan-frame .bottom-right { bottom: 0; right: 0; border-left: none; border-top: none; }

/* ================= 进度条与提示 ================= */
.custom-alert {
  margin-bottom: 24px;
  border-radius: 8px;
}

.verifying-status {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #fffaf5;
  border: 1px solid #ffedd5;
  border-radius: 12px;
}

.verify-text {
  margin: 12px 0 0;
  text-align: center;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
}

.time-highlight {
  color: #f97316;
  font-weight: 700;
  font-size: 16px;
}

/* ================= 按钮组优化 ================= */
.action-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}

.action-buttons .el-button {
  flex: 1;
  margin: 0 !important; /* 覆盖内联的 margin */
}

.btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  font-size: 15px;
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.5);
  height: 48px;
}
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.65);
}

.btn-secondary {
  background: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  font-weight: 600;
  font-size: 15px;
  border-radius: 10px;
  transition: all 0.3s ease;
  height: 48px;
}
.btn-secondary:hover {
  border-color: #f97316 !important;
  color: #f97316 !important;
  background: #fffcf9 !important;
}

/* ================= 其他登录方式 ================= */
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

.back-login-btn {
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
.back-login-btn:hover {
  background: #ffffff;
  border-color: #f97316;
  border-style: solid;
  color: #f97316;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.08);
}

/* 动画过度 */
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