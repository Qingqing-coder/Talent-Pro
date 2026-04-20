<template>
  <div class="personal-center">
    <el-button type="primary" class="back-home-btn-fixed" @click="handleBackHome">
      返回首页
    </el-button>
    <div class="hero-section">
      <div class="hero-content">
        <div class="user-info">
          <div class="avatar-container">
            <el-avatar :size="120" :src="userAvatar || defaultAvatar" class="user-avatar">
            </el-avatar>
            <div class="avatar-badge">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <div class="user-details">
            <h1 class="welcome-title">欢迎回来，{{ userInfo.name || '求职者' }}！</h1>
            <p class="welcome-subtitle">祝您求职顺利，早日找到理想工作</p>
            </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="content-container">
        <div class="quick-actions">
          <h2 class="section-title">快捷操作</h2>
          <div class="action-grid">
            <div class="action-card primary" @click="handleProfile">
              <div class="action-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="action-content">
                <h3>个人资料</h3>
                <p>查看个人信息，获取个人简历</p>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>

            <div class="action-card success" @click="handleResume">
              <div class="action-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="action-content">
                <h3>修改信息</h3>
                <p>修改和完善您的个人信息</p>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>

            <div class="action-card warning" style="display: none;">
              </div>

            <div class="action-card info" @click="handleJobSearch">
              <div class="action-icon">
                <el-icon><Search /></el-icon>
              </div>
              <div class="action-content">
                <h3>职位搜索</h3>
                <p>发现更多优质工作机会</p>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="service-section">
          <h2 class="section-title">功能服务</h2>
          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8">
              <div class="service-card">
                <div class="service-header">
                  <el-icon class="service-icon"><Star /></el-icon>
                  <h3>收藏职位</h3>
                </div>
                <p class="service-desc">管理您感兴趣的职位</p>
                <el-button type="primary" text class="custom-text-btn" @click="handleFavorites">
                  查看收藏 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </el-col>

            <el-col :xs="24" :sm="12" :md="8">
              <div class="service-card">
                <div class="service-header">
                  <el-icon class="service-icon"><Bell /></el-icon>
                  <h3>消息通知</h3>
                </div>
                <p class="service-desc">及时接收重要消息提醒</p>
                <el-button type="primary" text class="custom-text-btn" @click="handleMessages">
                  查看消息 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </el-col>

            <el-col :xs="24" :sm="12" :md="8" >
              <div class="service-card">
                <div class="service-header">
                  <el-icon class="service-icon"><Setting /></el-icon>
                  <h3>更新个人照片</h3>
                </div>
                <p class="service-desc">定期更新个人证件照片确保人脸识别功能的准确性</p>
                <el-button type="primary" text class="custom-text-btn" @click="handleIdCard">
                  更新照片 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="activity-section" style="display: none;"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  UserFilled,
  Document,
  List,
  Search,
  ArrowRight,
  Star,
  Bell,
  Setting,
} from '@element-plus/icons-vue'
import { showCustomMessage } from '@/utils/message'
import defaultAvatar from './headphoto1.jpg'
import { getEmpPred } from '@/api/job'
import { getEmpMessage } from '@/api/empMessage'
import { getAvatarPath } from '@/api/user'
const API_BASE_URL = 'http://127.0.0.1:8080'

onMounted(() => {
  getEmpMessage().then(res => {
    const user = res.data.list[0]
    if(user && user.name) {
      userInfo.value.name = user.name
    }
    getAvatarPath(user.username).then(res2 => {
      let filePath = res2.data
      if (filePath && !filePath.startsWith('/')) {
        filePath = '/' + filePath
      }
      userAvatar.value = filePath ? API_BASE_URL + filePath : ''
    })
  })
})

interface userInfo_m{
  name: string
}
// 用户信息
const userInfo = ref<userInfo_m>({
  name: '求职达人'
})

// 用户头像
const userAvatar = ref('')

// 加载用户头像
onMounted(() => {
  const savedAvatar = localStorage.getItem('userAvatar')
  if (savedAvatar) {
    userAvatar.value = savedAvatar
  }
})

// 功能处理函数
const router = useRouter()
const handleProfile = () => {
  router.push('/usermassage')
}

const handleResume = () => {
  router.push('/updateuser')
}

const handleJobSearch = () => {
  window.location.href = '/shouye'                       //记得换成首页的地址
}

const handleFavorites = () => {
  router.push('/predJobs')
}

const handleMessages = () => {
  router.push('/talk')
  // showCustomMessage('消息中心功能', 'info')
}
const handleIdCard = () => {
  router.push('/card')
  // showCustomMessage('消息中心功能', 'info')
}

const handleBackHome = () => {
  window.location.href = '/shouye'
}
</script>

<style scoped>
.personal-center {
  min-height: 100vh;
  /* 统一底层浅灰背景 */
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ================= 顶部英雄区域 ================= */
.hero-section {
  /* 替换为系统统一的高级深空灰渐变 */
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  padding: 60px 0 40px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

/* 增加科技感暗纹点缀 */
.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(249,115,22,0.08) 0%, rgba(0,0,0,0) 70%);
  pointer-events: none;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 40px;
  color: white;
}

.avatar-container {
  position: relative;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  background: white;
}

.avatar-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 36px;
  height: 36px;
  /* 替换为活力橙 */
  background: #f97316;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  border: 4px solid #1a1c22;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

.user-details {
  flex: 1;
}

.welcome-title {
  font-size: 32px;
  margin-bottom: 12px;
  letter-spacing: 1px;
  font-weight: 800;
  color: #ffffff;
}

.welcome-subtitle {
  margin-bottom: 0;
  color: #9ca3af;
  font-size: 15px;
}

/* ================= 悬浮返回按钮 ================= */
.back-home-btn-fixed {
  position: fixed;
  top: 32px;
  left: 32px;
  z-index: 1000;
  font-size: 15px;
  font-weight: 600;
  padding: 10px 28px;
  border-radius: 24px;
  /* 替换为活力橙渐变 */
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 16px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}
.back-home-btn-fixed:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(234, 88, 12, 0.45);
}

/* ================= 主要内容区域 ================= */
.main-content {
  background: transparent; /* 使用父级的 f5f7fa */
  padding: 40px 0 100px 0;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 24px 0;
  position: relative;
  padding-left: 14px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  /* 替换为橙色竖线 */
  background: #f97316;
  border-radius: 2px;
}

/* ================= 快捷操作区域 ================= */
.quick-actions {
  margin-bottom: 40px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.action-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  position: relative;
  overflow: hidden;
}

/* 顶部悬浮亮色条 */
.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--card-color), var(--card-color-light));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.08);
  border-color: #fdba74;
}

.action-card:hover::before {
  opacity: 1;
}

/* 统一为橙色/深灰色调高级感搭配 */
.action-card.primary {
  --card-color: #f97316;
  --card-color-light: #fb923c;
}

.action-card.success {
  --card-color: #ea580c;
  --card-color-light: #f97316;
}

.action-card.warning {
  --card-color: #c2410c;
  --card-color-light: #ea580c;
}

.action-card.info {
  /* 职位搜索使用高级深空灰做点缀 */
  --card-color: #4b5563;
  --card-color-light: #64748b;
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: linear-gradient(135deg, var(--card-color), var(--card-color-light));
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.action-content {
  flex: 1;
}

.action-content h3 {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px 0;
}

.action-content p {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.action-arrow {
  color: #cbd5e1;
  font-size: 18px;
  transition: all 0.3s ease;
}

.action-card:hover .action-arrow {
  transform: translateX(4px);
  color: var(--card-color);
}

/* ================= 功能服务区域 ================= */
.service-section {
  margin-bottom: 48px;
}

.service-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  height: 100%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.08);
  border-color: #fdba74;
}

.service-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.service-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #f97316;
  background: rgba(249, 115, 22, 0.1); /* 浅橙色背景 */
  border: 1px solid rgba(249, 115, 22, 0.2);
}

.service-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.service-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 20px 0;
  line-height: 1.5;
  flex: 1;
}

/* 覆盖 el-button text 默认的蓝色 */
.custom-text-btn {
  color: #f97316 !important;
  font-weight: 500;
  padding: 0;
  align-self: flex-start;
  transition: color 0.3s;
}
.custom-text-btn:hover {
  color: #ea580c !important;
  background: transparent !important;
}

/* ================= 最近活动区域 (防空占位) ================= */
.activity-section {
  margin-bottom: 48px;
}

/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .hero-section {
    padding: 80px 0 30px; /* 留出返回按钮空间 */
  }

  .user-info {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }

  .action-card {
    padding: 20px;
  }

  .content-container {
    padding: 0 16px;
  }

  .section-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .back-home-btn-fixed {
    top: 16px;
    left: 16px;
    padding: 8px 20px;
    font-size: 13px;
  }

  .hero-section {
    padding: 70px 0 20px;
  }

  .welcome-title {
    font-size: 20px;
  }

  .action-card {
    padding: 16px;
  }

  .action-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .action-content h3 {
    font-size: 15px;
  }
}
</style>