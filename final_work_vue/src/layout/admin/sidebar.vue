<template>
  <div class="sidebar-wrapper">
    <el-aside width="240px" class="custom-sidebar">
      <el-menu
        :default-active="activeMenu"
        class="custom-menu"
        @open="handleOpen"
        @close="handleClose"
        router
      >
        <el-menu-item index="/admin">
          <el-icon><DataBoard /></el-icon>
          <span>管理员概览</span>
        </el-menu-item>

        <el-menu-item index="/admin/test">
          <el-icon><Tools /></el-icon>
          <span>后端测试</span>
        </el-menu-item>

        <el-menu-item index="/admin/user-management">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/com-management">
          <el-icon><OfficeBuilding /></el-icon>
          <span>公司管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/emp-management">
          <el-icon><Briefcase /></el-icon>
          <span>职位管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  User,
  DataBoard,
  Briefcase,
  OfficeBuilding,
  Tools,
} from '@element-plus/icons-vue'

const route = useRoute()

// 当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style scoped>
/* ================= 侧边栏外层容器 ================= */
.sidebar-wrapper {
  height: 100%;
  background-color: #f5f7fa; /* 配合全局浅灰底色 */
}

/* ================= 侧边栏主体 ================= */
.custom-sidebar {
  height: 100%;
  background-color: #ffffff;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.02);
  border-right: 1px solid #e2e8f0;
  transition: width 0.3s ease;
  overflow-x: hidden;
  overflow-y: auto;
}

/* 隐藏原生滚动条但保留滚动功能 */
.custom-sidebar::-webkit-scrollbar {
  width: 4px;
}
.custom-sidebar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 4px;
}
.custom-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

/* ================= 菜单整体样式 ================= */
.custom-menu {
  border-right: none !important; /* 强制去除原生菜单右侧灰线 */
  background: transparent;
  padding: 24px 16px;
}

/* ================= 菜单项胶囊化设计 ================= */
:deep(.el-menu-item) {
  height: 52px;
  line-height: 52px;
  border-radius: 12px;
  margin-bottom: 8px;
  color: #475569;
  font-weight: 500;
  font-size: 15px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 0 20px !important;
}

/* 图标基础样式 */
:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 14px;
  color: #94a3b8;
  transition: all 0.3s ease;
}

/* ================= 悬浮状态 (Hover) ================= */
:deep(.el-menu-item:hover) {
  background-color: #fff4ed !important; /* 极淡的活力橙背景 */
  color: #ea580c !important;
}

:deep(.el-menu-item:hover .el-icon) {
  color: #f97316;
  transform: scale(1.15); /* 图标悬浮微放大 */
}

/* ================= 激活状态 (Active) ================= */
:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #fff4ed 0%, #ffffff 100%) !important;
  color: #ea580c !important;
  font-weight: 600;
  position: relative;
  box-shadow: inset 0 0 0 1px rgba(249, 115, 22, 0.1);
}

/* 左侧活力橙指示条 */
:deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: #f97316;
  border-radius: 0 4px 4px 0;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: #ea580c;
}
</style>