<template>
  <div class="test-page-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="welcome-title">后端连接测试</h1>
        <p class="welcome-subtitle">监控与测试系统后端 API 服务的连通性与响应状态</p>
      </div>
    </div>

    <div class="main-content">
      <el-card class="custom-card status-card" :body-style="{ padding: '32px' }">
        <div class="card-header-title">
          <h3 class="title-text">连接状态监控</h3>
        </div>

        <div class="status-dashboard">
          <div class="dashboard-item">
            <span class="item-label">目标后端服务地址</span>
            <span class="item-value server-url">http://127.0.0.1:8080</span>
          </div>
          
          <div class="dashboard-divider"></div>

          <div class="dashboard-item">
            <span class="item-label">当前实时连接状态</span>
            <div class="status-indicator">
              <div class="status-dot" :class="connectionStatus">
                <div v-if="testing" class="ping-animation"></div>
              </div>
              <span class="status-text" :class="connectionStatus">
                {{
                  connectionStatus === 'success'
                    ? '连通正常'
                    : connectionStatus === 'error'
                      ? '连接异常断开'
                      : '等待发起测试...'
                }}
              </span>
            </div>
          </div>
        </div>

        <div class="action-box">
          <el-button class="theme-btn-primary" @click="testConnection" :loading="testing">
            <el-icon class="btn-icon" v-if="!testing"><Connection /></el-icon>
            {{ testing ? '正在深度检测中...' : '发起连接测试' }}
          </el-button>
        </div>
      </el-card>

      <transition name="fade-up">
        <el-card v-if="testResult" class="custom-card result-card" :body-style="{ padding: '32px' }">
          <div class="card-header-title">
            <h3 class="title-text">响应报文详情</h3>
          </div>
          
          <div class="code-terminal">
            <div class="terminal-header">
              <span class="mac-dot red"></span>
              <span class="mac-dot yellow"></span>
              <span class="mac-dot green"></span>
              <span class="terminal-title">Response JSON</span>
            </div>
            <pre class="code-content">{{ JSON.stringify(testResult, null, 2) }}</pre>
          </div>
        </el-card>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showCustomMessage } from '@/utils/message'
import { Connection } from '@element-plus/icons-vue' // 引入连接图标

const connectionStatus = ref('pending')
const testing = ref(false)
const testResult = ref<any>(null)

// 测试连接 - 尝试多个可能的路径
const testConnection = async () => {
  testing.value = true
  connectionStatus.value = 'pending'

  const testPaths = [
    '/api/doc.html', // Swagger UI
    '/api/swagger-ui.html', // 另一种 Swagger UI 路径
    '/api/v2/api-docs', // OpenAPI 文档
    '/api/health', // 健康检查
    '/api/emp/all', // 直接测试API
  ]

  for (const path of testPaths) {
    try {
      const response = await fetch(path)
      if (response.ok) {
        connectionStatus.value = 'success'
        showCustomMessage(`后端连接成功 (${path})`, 'success')
        return
      }
    } catch (error: any) {
      // 跳过
    }
  }

  connectionStatus.value = 'error'
  showCustomMessage('后端连接失败，请检查后端服务是否启动', 'error')
  testing.value = false
}
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.test-page-container {
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
  max-width: 900px;
  margin: -60px auto 0; /* 负边距，使卡片向上穿插在深灰背景中 */
  padding: 0 24px;
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ================= 卡片基础样式 ================= */
.custom-card {
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  background: #ffffff;
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.custom-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(249, 115, 22, 0.08);
  border-color: #fdba74;
}

/* 卡片标题 */
.card-header-title {
  margin-bottom: 24px;
}

.title-text {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  position: relative;
  padding-left: 14px;
}

.title-text::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: #f97316; /* 活力橙装饰线 */
  border-radius: 2px;
}

/* ================= 状态数据仪表盘 ================= */
.status-dashboard {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 24px 32px;
  margin-bottom: 32px;
}

.dashboard-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dashboard-divider {
  width: 1px;
  height: 50px;
  background-color: #e2e8f0;
  margin: 0 32px;
}

.item-label {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

.item-value {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}

.server-url {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  color: #4b5563;
  background: #ffffff;
  padding: 4px 12px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  display: inline-block;
  width: fit-content;
}

/* ================= 状态指示灯 ================= */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  position: relative;
  transition: all 0.3s;
}

.status-dot.pending {
  background-color: #94a3b8;
  box-shadow: 0 0 8px rgba(148, 163, 184, 0.5);
}

.status-dot.success {
  background-color: #10b981; /* 成功绿 */
  box-shadow: 0 0 12px rgba(16, 185, 129, 0.6);
}

.status-dot.error {
  background-color: #ef4444; /* 警示红 */
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.6);
}

/* 测试中脉冲动画 */
.ping-animation {
  position: absolute;
  top: -4px;
  left: -4px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background-color: #f97316;
  opacity: 0.6;
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
}

@keyframes ping {
  75%, 100% {
    transform: scale(2);
    opacity: 0;
  }
}

.status-text {
  font-size: 16px;
  font-weight: 700;
}

.status-text.pending { color: #64748b; }
.status-text.success { color: #10b981; }
.status-text.error { color: #ef4444; }

/* ================= 动作按钮 ================= */
.action-box {
  display: flex;
  justify-content: center;
}

.theme-btn-primary {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  border-radius: 24px !important;
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}

.theme-btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(234, 88, 12, 0.45);
}

.btn-icon {
  margin-right: 6px;
  font-size: 18px;
}

/* ================= 极客风代码终端 ================= */
.code-terminal {
  background-color: #1e293b;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.terminal-header {
  background-color: #334155;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.mac-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
.mac-dot.red { background-color: #ef4444; }
.mac-dot.yellow { background-color: #f59e0b; }
.mac-dot.green { background-color: #10b981; }

.terminal-title {
  margin-left: 12px;
  color: #94a3b8;
  font-size: 13px;
  font-family: ui-monospace, monospace;
}

.code-content {
  margin: 0;
  padding: 24px;
  color: #a7f3d0; /* 薄荷绿字体，终端极客感 */
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 14px;
  line-height: 1.6;
  overflow-x: auto;
}

/* ================= 动画与响应式 ================= */
.fade-up-enter-active,
.fade-up-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.fade-up-enter-from,
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: 768px) {
  .hero-section {
    padding: 60px 20px 80px 20px;
  }
  .welcome-title {
    font-size: 28px;
  }
  .status-dashboard {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
  }
  .dashboard-divider {
    width: 100%;
    height: 1px;
    margin: 16px 0;
  }
  .theme-btn-primary {
    width: 100%;
  }
}
</style>