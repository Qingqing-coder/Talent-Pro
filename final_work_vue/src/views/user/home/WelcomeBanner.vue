<template>
  <div class="welcome-section">
    <div class="welcome-content">
      <h2>欢迎来到招聘平台</h2>
      <p>发现您理想的工作机会，开启职业新篇章</p>
      <div class="welcome-stats">
        <div class="stat-item">
          <span class="stat-number">{{ jobTotal }}</span>
          <span class="stat-label">职位数量</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ companyTotal }}</span>
          <span class="stat-label">企业入驻</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ empTotal }}</span>
          <span class="stat-label">求职者</span>
        </div>
      </div>
      <div class="ai-assistant-entry" @click="$emit('go-assistant')">
        <div class="ai-icon">🤖</div>
        <div class="ai-text">
          <span class="ai-title">AI 智能助手</span>
          <span class="ai-desc">求职指导 · 简历优化 · 面试技巧</span>
        </div>
        <div class="ai-arrow">→</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  jobTotal: number;
  companyTotal: number;
  empTotal: number;
}>();

defineEmits(['go-assistant']);
</script>

<style scoped>
.welcome-section { background: linear-gradient(145deg, #242933 0%, #15181e 100%); border-radius: 16px; color: white; margin-bottom: 24px; overflow: hidden; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15); position: relative; }
.welcome-section::after { content: ''; position: absolute; top: -50%; right: -10%; width: 400px; height: 400px; background: radial-gradient(circle, rgba(249,115,22,0.08) 0%, rgba(0,0,0,0) 70%); pointer-events: none; }
.welcome-content { padding: 40px; text-align: center; position: relative; z-index: 2; }
.welcome-content h2 { font-size: 32px; margin-bottom: 12px; font-weight: 800; letter-spacing: 1px; }
.welcome-content p { font-size: 15px; color: #9ca3af; margin-bottom: 30px; }
.welcome-stats { display: flex; justify-content: center; gap: 60px; }
.stat-item { text-align: center; }
.stat-number { display: block; font-size: 28px; font-weight: bold; margin-bottom: 4px; color: #f97316; }
.stat-label { font-size: 13px; color: #d1d5db; }
.ai-assistant-entry { display: flex; align-items: center; gap: 16px; padding: 16px 24px; background: linear-gradient(135deg, #374151 0%, #1f2937 100%); border: 1px solid #4b5563; border-radius: 12px; margin-top: 24px; cursor: pointer; transition: all 0.3s ease; }
.ai-assistant-entry:hover { transform: translateY(-2px); border-color: #f97316; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4), inset 0 0 0 1px rgba(249,115,22,0.2); }
.ai-icon { font-size: 24px; background: rgba(249, 115, 22, 0.15); border: 1px solid rgba(249, 115, 22, 0.3); color: #f97316; border-radius: 50%; width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; }
.ai-text { flex: 1; display: flex; flex-direction: column; gap: 4px; text-align: left; }
.ai-title { color: white; font-size: 16px; font-weight: 600; }
.ai-desc { color: #9ca3af; font-size: 13px; }
.ai-arrow { color: #f97316; font-size: 18px; font-weight: bold; transition: transform 0.3s ease; }
.ai-assistant-entry:hover .ai-arrow { transform: translateX(6px); }
@media (max-width: 768px) { .welcome-content { padding: 30px 20px; } .welcome-content h2 { font-size: 24px; } .welcome-stats { gap: 30px; } .ai-assistant-entry { padding: 12px 16px; margin-top: 16px; } .ai-icon { width: 40px; height: 40px; font-size: 20px; } .ai-title { font-size: 15px; } }
</style>