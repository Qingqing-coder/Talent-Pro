<template>
  <div class="common-layout">
    <el-container direction="vertical" class="layout-vertical">
      <Header />
      <el-container class="layout-main-container">
        <Menu />
        <el-main class="layout-main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import Header from './header.vue'
import Menu from './sidebar.vue'

import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style scoped>
/* ================= 全局底色与字体 ================= */
.common-layout {
  min-height: 100vh;
  background: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.layout-vertical {
  min-height: 100vh;
  background: #f5f7fa;
}

/* ================= 布局主容器 ================= */
.layout-main-container {
  display: flex;
  flex-direction: row;
  min-height: calc(100vh - 72px); /* 适配新版 Header 的高度 */
  background: transparent;
}

/* ================= 右侧主内容展示区 ================= */
.layout-main-content {
  background: #ffffff;
  border-radius: 16px;
  /* 调整外边距，使与侧边栏和顶部的距离更协调 */
  margin: 24px 24px 24px 0; 
  padding: 24px;
  /* 替换原有的重阴影，改用高级柔和弥散阴影 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
  flex: 1;
  min-width: 0;
  min-height: 600px;
  overflow-x: hidden;
  /* 平滑过渡效果 */
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
}

/* 悬浮微光效（彻底替换原来的生硬蓝色阴影） */
.layout-main-content:hover {
  box-shadow: 0 10px 30px rgba(249, 115, 22, 0.05); /* 极淡的活力橙弥散阴影 */
  border-color: #ffedd5; /* 浅橙色边框光晕 */
}

/* ================= 响应式适配 ================= */
@media (max-width: 900px) {
  .layout-main-content {
    margin: 16px 12px 16px 0;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .layout-main-container {
    flex-direction: column; /* 移动端可能需要折叠侧边栏 */
  }
  .layout-main-content {
    margin: 16px;
    border-radius: 12px;
  }
}
</style>