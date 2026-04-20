<template>
  <div v-if="hasPermission">
    <slot />
  </div>
  <div v-else>
    <el-empty description="您没有权限访问此功能" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoleStore } from '@/stores/role'

interface Props {
  requiredRole: 'user' | 'company' | 'admin'
}

const props = defineProps<Props>()
const roleStore = useRoleStore()

const hasPermission = computed(() => {
  return roleStore.hasPermission(props.requiredRole)
})
</script>

<style scoped>
.el-empty {
  padding: 40px;
}
</style>
