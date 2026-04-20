<template>
  <div class="job-list">
    <div
      class="job-item"
      v-for="item in pageJobData"
      :key="item.id"
      @click="$emit('detail', item)"
    >
      <div class="job-header">
        <h3 class="job-title">{{ item.title }}</h3>
        <span class="job-salary">{{ item.salary }}</span>
      </div>
      <div class="job-company">
        <span class="company-name">{{ item.companyName }}</span>
        <span class="company-size">{{ item.companySize }}</span>
      </div>
      <div class="job-tags">
        <span class="job-tag">{{ item.fullLocation }}</span>
        <span class="job-tag">{{ item.experience }}</span>
        <span class="job-tag">{{ item.education }}</span>
        <span class="job-tag">{{ item.type }}</span>
      </div>
      <div class="job-benefits">
        <span
          class="benefit-tag"
          v-for="benefit in parseBenefits(item.benefits)"
          :key="benefit"
        >
          {{ benefit }}
        </span>
        <div class="preNum">{{ item.preNum }} 个收藏</div>
        <div class="favorite-icon" @click.stop="$emit('toggle-favorite', item)">
          <el-icon v-if="item.isFavorite" color="#f97316" :size="18">
            <StarFilled />
          </el-icon>
          <el-icon v-else color="#94a3b8" :size="18">
            <Star />
          </el-icon>
        </div>
      </div>
    </div>
    
    <div v-if="pageJobData.length === 0 && !loading" class="no-data">
      <el-empty description="暂无相关职位" />
    </div>
    <div v-if="loading" class="loading-data">
      <el-skeleton :rows="3" animated />
    </div>

    <div class="pagination-wrapper" v-if="pageJobData.length > 0">
      <el-pagination
        :current-page="currentPage"
        :page-size="10"
        :background="true"
        layout="total, prev, pager, next"
        :total="total"
        @current-change="(val: any) => $emit('page-change', val)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Star, StarFilled } from '@element-plus/icons-vue'

defineProps<{
  pageJobData: any[];
  loading: boolean;
  currentPage: number;
  total: number;
}>();

defineEmits(['detail', 'toggle-favorite', 'page-change']);

// 规范化福利标签展示
const parseBenefits = (benefits: string | string[]): string[] => {
  if (Array.isArray(benefits)) return benefits;
  return benefits.split(/[,，]/).map(b => b.trim()).filter(Boolean);
};
</script>

<style scoped>
.job-list { display: flex; flex-direction: column; gap: 16px; }
.job-item { background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04); border: 1px solid #f1f5f9; padding: 24px; cursor: pointer; transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); position: relative; padding-bottom: 40px; }
.job-item:hover { border-color: #fdba74; box-shadow: 0 10px 25px rgba(249, 115, 22, 0.08); transform: translateY(-2px); }
.job-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.job-title { font-size: 18px; font-weight: 700; color: #111827; margin: 0; flex: 1; }
.job-salary { color: #ea580c; font-weight: 700; font-size: 18px; margin-left: 16px; }
.job-company { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.company-name { color: #4b5563; font-weight: 600; font-size: 15px; }
.company-size { color: #64748b; font-size: 12px; background: #f1f5f9; padding: 4px 10px; border-radius: 4px; }
.job-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.job-tag { padding: 4px 10px; background: #f8fafc; color: #64748b; border-radius: 4px; font-size: 12px; border: 1px solid #e2e8f0; }
.job-benefits { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.benefit-tag { padding: 4px 10px; background: rgba(249, 115, 22, 0.06); color: #ea580c; border-radius: 6px; font-size: 12px; font-weight: 500; border: 1px solid rgba(249, 115, 22, 0.2); display: inline-flex; align-items: center; justify-content: center; line-height: 1.2; transition: all 0.3s ease; }
.benefit-tag:hover { background: rgba(249, 115, 22, 0.1); }
.preNum { margin-top: 12px; font-size: 12px; color: #94a3b8; display: flex; align-items: center; width: 100%; }
.favorite-icon { position: absolute; right: 24px; bottom: 24px; cursor: pointer; transition: all 0.3s ease; width: 36px; height: 36px; background: #ffffff; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); border: 1px solid #f1f5f9; }
.favorite-icon:hover { transform: scale(1.1) translateY(-2px); box-shadow: 0 4px 12px rgba(249, 115, 22, 0.15); border-color: #fdba74; }
.no-data, .loading-data { text-align: center; padding: 60px 0; }
.pagination-wrapper { background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04); border: 1px solid #f1f5f9; display: flex; justify-content: center; margin-top: 24px; padding: 20px; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background-color: #f97316 !important; color: #fff !important; }
:deep(.el-pagination.is-background .el-pager li:hover) { color: #f97316 !important; }
</style>