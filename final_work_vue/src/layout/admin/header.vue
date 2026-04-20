<template>
  <div class="header-portal">
    <div class="header-left">
      <div class="brand-logo">
        <div class="logo-icon">
          <div class="logo-inner"></div>
        </div>
        <span class="brand-name">
          Talent Pro <span class="sub-name">管理系统</span>
        </span>
      </div>
    </div>
    
    <div class="header-center">
      <h1 class="page-main-title">管理员界面</h1>
    </div>
    
    <div class="header-right">
      <el-button class="logout-btn" @click="logout">
        <el-icon class="btn-icon"><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { clearAuth } from '@/utils/auth.ts'
import { SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const logout = () => {
  clearAuth()
  router.push('/login')
}

// 初始化认证
onMounted(() => {
  authStore.initAuth()
})
</script>

<style scoped>
/* ================= 全局直角通栏容器 (深空灰) ================= */
.header-portal {
  width: 100%;
  height: 64px;
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  border-bottom: 1px solid #1f232c;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  border-radius: 0; /* 强制直角 */
  position: relative;
  z-index: 1000;
}

/* ================= 左侧：Logo 与 品牌名 ================= */
.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.brand-logo {
  display: flex;
  align-items: center;
}

/* Talent Pro 活力橙图标 */
.logo-icon {
  width: 26px;
  height: 26px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 6px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.4);
}

.logo-inner {
  width: 10px;
  height: 10px;
  background: #ffffff;
  border-radius: 2px;
  transform: rotate(45deg);
}

/* 品牌文字 (适配暗色背景) */
.brand-name {
  font-size: 18px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
}

.sub-name {
  font-size: 13px;
  font-weight: 600;
  color: #9ca3af;
  margin-left: 10px;
  padding-left: 10px;
  border-left: 2px solid #4b5563; /* 深色分割线 */
}

/* ================= 中间：页面大标题 ================= */
.header-center {
  display: flex;
  justify-content: center;
  flex: 1;
}

/* 沉稳大气的大标题 */
.page-main-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

/* ================= 右侧：浅橙色退出按钮 ================= */
.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 1;
}

/* 浅橙色透明线框，完美融入深空灰背景 */
.logout-btn {
  background: rgba(253, 186, 116, 0.1) !important;
  border: 1px solid rgba(253, 186, 116, 0.3) !important;
  color: #fdba74 !important; /* 浅橙色文字 */
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 6px 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 悬浮时变为亮眼的活力橙，提升点击反馈 */
.logout-btn:hover {
  background: #f97316 !important;
  color: #ffffff !important;
  border-color: #f97316 !important;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

.btn-icon {
  font-size: 15px;
}

/* ================= 响应式适配 ================= */
@media (max-width: 768px) {
  .header-portal {
    padding: 0 16px;
  }
  
  .brand-name {
    font-size: 16px;
  }
  
  .sub-name {
    display: none; /* 移动端隐藏副标题以节省空间 */
  }
  
  .page-main-title {
    font-size: 16px;
  }
}
</style>