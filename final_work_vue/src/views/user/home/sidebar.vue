<template>
  <div class="content-left">
    <div class="left-search-item">
      <h4>岗位分类</h4>
      <div class="category-list">
        <div
          class="category-item"
          v-for="item in categoryData"
          :key="item.id"
          :class="{ active: selectedCategory === item.id }"
          @click="$emit('select-category', item.id)"
        >
          {{ item.title }}
        </div>
      </div>
    </div>
    
    <div class="left-search-item">
      <h4>已收藏工作</h4>
      <div class="favorite-job-list">
        <div
          class="job-item"
          v-for="item in favoriteJobs"
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
          <div class="favorite-icon" @click.stop="$emit('toggle-favorite', item)">
            <el-icon v-if="item.isFavorite" color="#f97316" :size="18">
              <StarFilled />
            </el-icon>
            <el-icon v-else color="#94a3b8" :size="18">
              <Star />
            </el-icon>
          </div>
        </div>
        
        <div class="pagination-wrapper" v-if="favoriteJobs.length > 0">
          <el-pagination
            :current-page="prePageNum"
            :page-size="7"
            :background="true"
            layout="total, prev, pager, next"
            :total="prePageTotal"
            @current-change="(val: any) => $emit('pre-page-change', val)"
          />
        </div>
        <div v-if="favoriteJobs.length === 0" class="no-data">
          <el-empty description="暂无收藏的工作" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Star, StarFilled } from '@element-plus/icons-vue'

defineProps<{
  categoryData: any[];
  selectedCategory: number;
  favoriteJobs: any[];
  prePageNum: number;
  prePageTotal: number;
}>();

defineEmits(['select-category', 'detail', 'toggle-favorite', 'pre-page-change']);
</script>

<style scoped>
.content-left { width: 260px; flex-shrink: 0; }
.left-search-item { background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04); border: 1px solid #f1f5f9; padding: 20px; margin-bottom: 20px; }
.left-search-item h4 { color: #111827; font-weight: 700; font-size: 16px; margin-bottom: 16px; border-bottom: 2px solid #f97316; padding-bottom: 8px; display: inline-block; }
.category-item { cursor: pointer; color: #4b5563; padding: 10px 14px; border-radius: 8px; transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); margin-bottom: 8px; border: 1px solid transparent; font-weight: 500; font-size: 14px; }
.category-item:hover { color: #ea580c; background-color: #fff4ed; border-color: #fdba74; }
.category-item.active { color: #fff; background-color: #f97316; border-color: #f97316; box-shadow: 0 4px 12px rgba(249, 115, 22, 0.25); }
.job-item { padding: 24px; cursor: pointer; transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1); position: relative; padding-bottom: 40px; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04); border: 1px solid #f1f5f9; margin-bottom: 16px; }
.job-item:hover { border-color: #fdba74; box-shadow: 0 10px 25px rgba(249, 115, 22, 0.08); transform: translateY(-2px); }
.job-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.job-title { font-size: 18px; font-weight: 700; color: #111827; margin: 0; flex: 1; }
.job-salary { color: #ea580c; font-weight: 700; font-size: 18px; margin-left: 16px; }
.job-company { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.company-name { color: #4b5563; font-weight: 600; font-size: 15px; }
.company-size { color: #64748b; font-size: 12px; background: #f1f5f9; padding: 4px 10px; border-radius: 4px; }
.job-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.job-tag { padding: 4px 10px; background: #f8fafc; color: #64748b; border-radius: 4px; font-size: 12px; border: 1px solid #e2e8f0; }
.favorite-icon { position: absolute; right: 24px; bottom: 24px; cursor: pointer; transition: all 0.3s ease; width: 36px; height: 36px; background: #ffffff; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); border: 1px solid #f1f5f9; }
.favorite-icon:hover { transform: scale(1.1) translateY(-2px); box-shadow: 0 4px 12px rgba(249, 115, 22, 0.15); border-color: #fdba74; }
.no-data { text-align: center; padding: 60px 0; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; padding: 20px; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background-color: #f97316 !important; color: #fff !important; }
:deep(.el-pagination.is-background .el-pager li:hover) { color: #f97316 !important; }
@media (max-width: 768px) { .content-left { width: 100%; } }
</style>