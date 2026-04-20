<script setup lang="ts">
import { clearAuth, getAuthStatus } from '@/utils/auth.ts'
import router from '@/router'
import { ElMessageBox } from 'element-plus'

const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 清除登录信息
    clearAuth()
    // 跳转到登录页
    window.location.href = '/login'
  })
}

const toPersonanCenter = async () => {
  await router.push('/personalcenter')
}

const { isAuthenticated } = getAuthStatus()

const handleLogin = async () => {
  await router.push('/login')
}
</script>

<template>
  <el-header class="custom-header">
    <div class="header-content">
      
      <div class="brand-logo">
        <div class="logo-icon">
          <div class="logo-inner"></div>
        </div>
        <span class="logo-text">Talent <span class="text-highlight">Pro</span></span>
      </div>

      <div class="header-actions">
        <el-button link class="nav-btn" @click="toPersonanCenter">个人中心</el-button>
        <el-button link class="nav-btn logout-btn" v-if="isAuthenticated" @click="handleLogout">退出登录</el-button>
        <el-button link class="nav-btn login-btn" v-else @click="handleLogin">登录</el-button>
      </div>

    </div>
  </el-header>
</template>

<style scoped>
/* 核心：顶部导航栏整体风格 - 现代化渐变与毛玻璃效果 */
.custom-header {
  /* 高端深色渐变底色 */
  background: linear-gradient(135deg, #111417 0%, #1a1f24 100%);
  /* 增加现代感毛玻璃效果 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  /* 更精细的分割线 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  /* 稍微增加高度，更有呼吸感 */
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0; /* 由 content 容器控制左右填充 */
  position: sticky; /* 粘性顶部，防止滑动时遮挡 */
  top: 0;
  z-index: 1000;
}

/* 内容区域容器 - 修改这里可以增大填充范围 */
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  /* 增加最大宽度以适配宽屏填充 */
  max-width: 1800px;
  margin: 0 auto;
  /* 左右增加内边距防止贴边 */
  padding: 0 40px;
}

/* ========== Logo 样式 ========== */
.brand-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: opacity 0.3s, transform 0.3s;
}

.brand-logo:hover {
  opacity: 0.95;
  transform: scale(1.02); /* 悬浮时微小缩放 */
}

.logo-icon {
  width: 30px;
  height: 30px;
  /* 增强 logo 渐变对比度 */
  background: linear-gradient(135deg, #fb923c 0%, #f97316 60%, #ea580c 100%);
  border-radius: 9px;
  margin-right: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4);
}

.logo-inner {
  width: 11px;
  height: 11px;
  background: #ffffff;
  border-radius: 2px;
  transform: rotate(45deg);
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.5px;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.1); /* 文字轻微光晕 */
}

/* Logo 文本高亮处理 */
.logo-text .text-highlight {
  color: #f97316;
}

/* ========== 按钮组样式 ========== */
.header-actions {
  display: flex;
  align-items: center;
  gap: 15px; /* 减小间距，更紧凑 */
}

/* 自定义导航按钮风格 */
:deep(.nav-btn) {
  /* 默认白色字体，降低亮度以突出品牌橙 */
  color: rgba(255, 255, 255, 0.85) !important;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1); /* 高级动效曲线 */
  padding: 8px 16px;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

:deep(.nav-btn:not(.login-btn):hover) {
  color: #f97316 !important; /* 悬浮变为主题橙色 */
  background: rgba(249, 115, 22, 0.08); /* 增加极其细微的背景 */
  transform: translateY(-2px); /* 悬浮上移 */
  text-shadow: 0 0 10px rgba(249, 115, 22, 0.2);
}

:deep(.nav-btn:active) {
  transform: translateY(-1px);
}

/* 退出登录特殊样式 */
.logout-btn:hover {
  background: rgba(239, 68, 68, 0.08) !important; /* 悬浮红色背景 */
  color: #f87171 !important; /* 字体变红 */
}

/* 登录按钮：科技感填充效果 */
.login-btn {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  color: #ffffff !important; /* 强制白色字体 */
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

.login-btn:hover {
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.5) !important;
  transform: translateY(-2px);
}

/* ========== 响应式适配 ========== */
@media (max-width: 1024px) {
  .header-content {
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .custom-header {
    height: 60px;
  }
  
  .header-content {
    padding: 0 15px;
  }
  
  .logo-text {
    font-size: 19px;
  }

  .logo-icon {
    width: 26px;
    height: 26px;
    margin-right: 10px;
  }

  /* 移动端减小按钮大小 */
  :deep(.nav-btn) {
    font-size: 13px;
    padding: 6px 12px;
  }
  
  .header-actions {
    gap: 8px;
  }
}
</style>