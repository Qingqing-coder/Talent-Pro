<template>
  <div class="portal" @click="toLogin">
    <Header />
    <div class="portal-main">
      <Index />
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from './header.vue'
import Footer from './footer.vue'
import Index from '@/views/user/home/index.vue'
import { getAuthStatus } from '@/utils/auth.ts'
import router from '@/router'
const toLogin = async () => {
  const isAuthenticated = getAuthStatus().isAuthenticated
  if (!isAuthenticated) {
    await router.push('/login')
  }
}
</script>

<style scoped>
.portal {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
}

.portal-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  margin: 0;
}
</style>
