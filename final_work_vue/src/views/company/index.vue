<template>
  <div class="company-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="welcome-title">企业管理平台</h1>
        <p class="welcome-subtitle">欢迎回来，您可以高效管理招聘流程与公司信息</p>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="32" justify="center" class="company-row">
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="company-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><OfficeBuilding /></el-icon>
            </div>
            <h3 class="company-card-title">公司资料</h3>
            <p class="company-card-desc">完善企业基础信息与发展历程，展示企业风采，吸引更多优质人才投递。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleCompanyProfile">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="company-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><Briefcase /></el-icon>
            </div>
            <h3 class="company-card-title">岗位管理</h3>
            <p class="company-card-desc">发布与维护招聘岗位，精准设置要求与薪资条件，高效筛选目标简历。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleEmpTable">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="company-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><ChatDotRound /></el-icon>
            </div>
            <h3 class="company-card-title">消息管理</h3>
            <p class="company-card-desc">实时接收求职者咨询，在线即时沟通交流，不错过任何一位潜力人才。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleMessageManagement">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { OfficeBuilding, Briefcase, ChatDotRound } from '@element-plus/icons-vue' // 引入补充的图标

const router = useRouter()

const handleCompanyProfile = () => {
  router.push('/company/comidv')
}

const handleEmpTable = () => {
  router.push('/company/comemp')
}

const handleMessageManagement = () => {
  router.push('/company/message')
}
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.company-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  padding-bottom: 60px;
}

/* ================= 顶部英雄区域 (深空灰) ================= */
.hero-section {
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  padding: 80px 0 100px 0; /* 底部留足空间给卡片悬浮 */
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  text-align: center;
}

/* 科技感暗纹点缀 */
.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(249,115,22,0.08) 0%, rgba(0,0,0,0) 70%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 2;
  padding: 0 20px;
}

.welcome-title {
  font-size: 36px;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 16px 0;
  letter-spacing: 2px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #9ca3af;
  margin: 0;
  font-weight: 400;
}

/* ================= 主要内容区域 (负边距悬浮) ================= */
.main-content {
  max-width: 1200px;
  margin: -60px auto 0; /* 负边距，使卡片向上穿插在深灰背景中 */
  padding: 0 24px;
  position: relative;
  z-index: 10;
}

.company-row {
  margin: 0 !important;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

/* ================= 模块卡片样式 ================= */
.company-card {
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  background: #ffffff;
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  text-align: center;
  height: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

/* 顶部隐藏的亮色指示条 */
.company-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #f97316, #fb923c);
  opacity: 0;
  transition: opacity 0.4s ease;
}

/* 卡片悬浮状态 */
.company-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(249, 115, 22, 0.08);
  border-color: #fdba74;
}

.company-card:hover::before {
  opacity: 1;
}

:deep(.company-card .el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 图标容器 */
.card-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(249, 115, 22, 0.06);
  border: 1px solid rgba(249, 115, 22, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  transition: all 0.4s ease;
}

/* 悬浮时图标变色 */
.company-card:hover .card-icon-wrapper {
  transform: scale(1.1) rotate(5deg);
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-color: transparent;
  box-shadow: 0 8px 20px rgba(234, 88, 12, 0.3);
}

.card-icon {
  font-size: 36px;
  color: #f97316;
  transition: color 0.4s ease;
}

.company-card:hover .card-icon {
  color: #ffffff;
}

.company-card-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px 0;
}

.company-card-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 32px 0;
  flex: 1;
}

.card-btn-box {
  width: 100%;
  margin-top: auto;
}

/* ================= 按钮高级态 ================= */
.theme-btn-primary {
  width: 85%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  /* 默认线框灰状态，不抢夺视线 */
  background: #f8fafc !important;
  border: 1px solid #e2e8f0 !important;
  color: #64748b !important;
  border-radius: 24px !important;
  transition: all 0.4s ease;
}

/* 悬浮整个卡片时，按钮激活为活力橙 */
.company-card:hover .theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
}

.theme-btn-primary:active {
  transform: scale(0.96);
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 20px 80px 20px;
  }
  .welcome-title {
    font-size: 28px;
  }
  .welcome-subtitle {
    font-size: 14px;
  }
  .main-content {
    margin-top: -40px;
    padding: 0 16px;
  }
  .company-card {
    margin-bottom: 20px;
  }
  :deep(.company-card .el-card__body) {
    padding: 30px 20px;
  }
  .theme-btn-primary {
    width: 100%;
  }
}
</style>