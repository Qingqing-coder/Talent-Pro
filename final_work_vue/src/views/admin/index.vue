<template>
  <div class="admin-dashboard-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="welcome-title">管理员控制台</h1>
        <p class="welcome-subtitle">您拥有最高权限，可在此全局管理平台用户、企业、岗位及系统运行状态</p>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="24" justify="center" class="admin-row">
        <el-col :xs="24" :sm="12" :md="12" :lg="6">
          <el-card class="admin-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><User /></el-icon>
            </div>
            <h3 class="admin-card-title">用户管理</h3>
            <p class="admin-card-desc">管理平台所有的求职者账号、基础资料、求职意向及履历数据。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleUser">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="12" :lg="6">
          <el-card class="admin-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><OfficeBuilding /></el-icon>
            </div>
            <h3 class="admin-card-title">公司管理</h3>
            <p class="admin-card-desc">审核与维护企业用户资料，管理企业入驻信息与账号状态。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleCompany">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="12" :lg="6">
          <el-card class="admin-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><Briefcase /></el-icon>
            </div>
            <h3 class="admin-card-title">岗位管理</h3>
            <p class="admin-card-desc">统筹全平台的招聘岗位数据，监控职位发布状态及违规信息处理。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handlePosition">
                进入管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="12" :lg="6">
          <el-card class="admin-card" :body-style="{ padding: '40px 24px' }">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon"><Connection /></el-icon>
            </div>
            <h3 class="admin-card-title">系统测试</h3>
            <p class="admin-card-desc">进行后端接口连通性测试及系统运行状态的常规监控。</p>
            <div class="card-btn-box">
              <el-button class="theme-btn-primary" @click="handleTest">
                连接测试
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
import { User, OfficeBuilding, Briefcase, Connection } from '@element-plus/icons-vue'

const router = useRouter()

// 用户管理功能
const handleUser = () => {
  router.push('/admin/user-management')
}

// 公司管理功能
const handleCompany = () => {
  router.push('/admin/com-management')
}

// 岗位管理功能
const handlePosition = () => {
  router.push('/admin/emp-management')
}

// 连接测试功能
const handleTest = () => {
  router.push('/admin/test')
}
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.admin-dashboard-page {
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
  max-width: 1400px;
  margin: -60px auto 0; /* 负边距，使卡片向上穿插在深灰背景中 */
  padding: 0 24px;
  position: relative;
  z-index: 10;
}

.admin-row {
  margin: 0 !important;
}

/* ================= 模块卡片样式 ================= */
.admin-card {
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
.admin-card::before {
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
.admin-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(249, 115, 22, 0.08);
  border-color: #fdba74;
}

.admin-card:hover::before {
  opacity: 1;
}

:deep(.admin-card .el-card__body) {
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
.admin-card:hover .card-icon-wrapper {
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

.admin-card:hover .card-icon {
  color: #ffffff;
}

.admin-card-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px 0;
}

.admin-card-desc {
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
.admin-card:hover .theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
}

.theme-btn-primary:active {
  transform: scale(0.96);
}

/* ================= 响应式 ================= */
@media (max-width: 992px) {
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
  .admin-card {
    margin-bottom: 20px;
  }
  :deep(.admin-card .el-card__body) {
    padding: 30px 20px;
  }
  .theme-btn-primary {
    width: 100%;
  }
}
</style>