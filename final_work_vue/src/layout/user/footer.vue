<template>
  <div class="footer-view">
    <div class="footer-content">
      <div class="footer-section">
        <h4>关于我们</h4>
        <p>专业的招聘平台，连接企业与人才</p>
      </div>
      <div class="footer-section">
        <h4>快速链接</h4>
        <div class="footer-links">
          <a href="#" class="footer-link">首页</a>
          <a href="#" class="footer-link">职位搜索</a>
          <a href="#" class="footer-link">企业服务</a>
          <a href="#" class="footer-link">帮助中心</a>
        </div>
      </div>
      <div class="footer-section">
        <h4>联系方式</h4>
        <p>邮箱：contact@example.com</p>
        <p>电话：400-123-4567</p>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="footer-infos">
        <span>
          <a style="color: rgb(174, 174, 174);">京ICP备12345678号</a>
        </span>
        <span>&nbsp;&nbsp;京公网安备123456&nbsp;&nbsp;新出发京零字第东123456号&nbsp;&nbsp;统一社会信用代码 123456789</span>
      </div>
      <div class="address">2025-2026 © 北京代码演示 · All Rights Reserved</div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Vue 3 Composition API
</script>

<style scoped>
.footer-view {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section {
  flex: 1;
}

.footer-section h4 {
  color: #3498db;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.footer-section p {
  color: #bdc3c7;
  line-height: 1.6;
  margin-bottom: 8px;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-link {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #3498db;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  text-align: center;
}

.footer-infos {
  height: 16px;
  line-height: 16px;
  font-size: 12px;
  color: #95a5a6;
  margin-bottom: 8px;
}

.address {
  text-align: center;
  height: 16px;
  line-height: 16px;
  font-size: 12px;
  color: #95a5a6;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .footer-section {
    text-align: center;
  }
}
</style>
