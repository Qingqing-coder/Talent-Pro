<script setup lang="ts">
import { ref, watch } from 'vue'
import { deletePre, getEmpPred, getJobPreNum, type Pre } from '@/api/job.ts'
import { StarFilled } from '@element-plus/icons-vue'
import { getAuthStatus } from '@/utils/auth.ts'
import router from '@/router'

interface JobItem {
  id: number;
  title: string;
  salary: string;
  companyName: string;
  companySize: string;
  fullLocation: string;
  experience: string;
  education: string;
  type: string;
  benefits: string[];
  jobType: string;
  createTime: Date;
  preNum: number;
}
const pre = ref<Pre>({
  username: '',
  jobId: 0
})                                          //收藏关系


const favorites = ref<JobItem[]>([]);
const pageNum = ref(1);
const total = ref(0);
const { username } = getAuthStatus();

watch(favorites, (newFavorites) => {
  for (const job of newFavorites) {
    getJobPreNum({jobId: job.id, pageNum: pageNum.value, pageSize: 7}).then((preNum) => {
      job.preNum = preNum.data.total;
    })
  }
}, { immediate: true })

getEmpPred(pageNum.value).then((res) => {
  favorites.value = res.data.list
  total.value = res.data.total
})

/**
 * 规范化福利标签展示
 * @param benefits - 福利字符串或数组
 * @returns 规范化的福利数组
 */
function parseBenefits(benefits: string | string[]): string[] {
  if (Array.isArray(benefits)) return benefits;

  // 兼容中英文逗号分隔
  return benefits.split(/[,，]/)
    .map(b => b.trim())
    .filter(Boolean);
}

/**
 * 处理收藏
 */
const handlePre = async (item: JobItem) => {
  pre.value.username = username;
  pre.value.jobId = item.id;

  try {
    // 取消收藏
    await deletePre(pre.value.username, pre.value.jobId);
    // 更新已收藏工作列表
    getEmpPred(pageNum.value).then((res) => {
      favorites.value = res.data.list
      total.value = res.data.total
    })
  } catch (error) {
    console.error("操作失败:", error);
  }
};

/**
 * 处理分页
 */
const handlePageChange = (page: number) => {
  pageNum.value = page;
  getEmpPred(pageNum.value).then((res) => {
    favorites.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 处理职位详情查看
 * @param item - 选中的职位对象
 */
const handleDetail = (item: JobItem) => {
  // 跳转到职位详情页面
  router.push(`/job-detail/${item.id}`);
};
</script>

<template>
  <div class="favorites-page">
    <div class="favorites-container">
      <div class="header-section">
        <h1 class="favorites-title">我的收藏</h1>
        <p class="favorites-subtitle">管理您感兴趣的职位机会</p>
      </div>

      <div v-if="favorites.length === 0 && total === 0" class="empty-favorites">
        <el-empty description="您还没有收藏任何岗位" />
      </div>

      <div class="job-list" v-else>
        <div
          class="job-item"
          v-for="item in favorites"
          :key="item.id"
          @click="handleDetail(item)"
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
            
            <div class="favorite-icon" @click.stop="handlePre(item)" title="取消收藏">
              <el-icon color="#f97316" :size="20">
                <StarFilled />
              </el-icon>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="10"
            :background="true"
            layout="total, prev, pager, next"
            :total="total"
            @current-change="handlePageChange"
            class="custom-pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ================= 全局页面底色 ================= */
.favorites-page {
  min-height: 100vh;
  background-color: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  padding: 1px 0; /* 防止 margin 塌陷 */
}

/* ================= 主容器 (宽度缩小、居中) ================= */
.favorites-container {
  max-width: 1000px; /* 缩小宽度，不再填满全屏 */
  margin: 40px auto;
  padding: 0 20px;
}

/* ================= 头部标题区 ================= */
.header-section {
  margin-bottom: 24px;
}

.favorites-title {
  font-size: 24px;
  font-weight: 800;
  color: #111827;
  margin: 0 0 8px 0;
  padding-left: 14px;
  border-left: 4px solid #f97316; /* 活力橙装饰线 */
  line-height: 1.2;
  letter-spacing: 0.5px;
}

.favorites-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 0 18px;
}

/* ================= 缺省状态 ================= */
.empty-favorites {
  background: #ffffff;
  border-radius: 16px;
  padding: 60px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  border: 1px solid #f1f5f9;
}

/* ================= 职位列表 ================= */
.job-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 单个职位卡片 */
.job-item {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px 24px 40px 24px; /* 底部留白给绝对定位的收藏星星 */
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  position: relative;
}

.job-item:hover {
  border-color: #fdba74;
  box-shadow: 0 10px 30px rgba(249, 115, 22, 0.08);
  transform: translateY(-3px);
}

/* 职位标题与薪资 */
.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.job-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  flex: 1;
  transition: color 0.3s;
}

.job-item:hover .job-title {
  color: #ea580c;
}

.job-salary {
  color: #ea580c; /* 活力橙 */
  font-weight: 800;
  font-size: 18px;
  margin-left: 16px;
}

/* 公司信息 */
.job-company {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.company-name {
  color: #4b5563;
  font-weight: 600;
  font-size: 15px;
}

.company-size {
  color: #64748b;
  font-size: 12px;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 6px;
}

/* 岗位要求标签 */
.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.job-tag {
  padding: 4px 10px;
  background: #f8fafc;
  color: #64748b;
  border-radius: 6px;
  font-size: 12px;
  border: 1px solid #e2e8f0;
  font-weight: 500;
}

/* 福利标签与收藏量 */
.job-benefits {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.benefit-tag {
  padding: 4px 10px;
  background: rgba(249, 115, 22, 0.06); /* 浅橙色微光背景 */
  color: #ea580c;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid rgba(249, 115, 22, 0.2);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.preNum {
  margin-left: 8px;
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  align-items: center;
}

/* 收藏图标 (右下角绝对定位) */
.favorite-icon {
  position: absolute;
  right: 24px;
  bottom: 24px;
  width: 40px;
  height: 40px;
  background: #fff4ed;
  border: 1px solid #fdba74;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: 0 4px 10px rgba(249, 115, 22, 0.15);
}

.favorite-icon:hover {
  transform: scale(1.15) translateY(-2px);
  background: #f97316;
  border-color: #f97316;
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.3);
}

/* 悬浮时将内部图标颜色反转为白色 */
.favorite-icon:hover :deep(.el-icon) {
  color: #ffffff !important;
}

/* ================= 分页组件 ================= */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  border: 1px solid #f1f5f9;
}

/* 覆盖 Element-Plus 默认分页器的蓝色主题 */
:deep(.custom-pagination.el-pagination.is-background .el-pager li.is-active) {
  background-color: #f97316 !important;
  color: #ffffff !important;
  font-weight: bold;
}
:deep(.custom-pagination.el-pagination.is-background .el-pager li:hover) {
  color: #ea580c !important;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .favorites-container {
    margin: 20px auto;
    padding: 0 16px;
  }
  
  .job-item {
    padding: 20px 20px 60px 20px;
  }
  
  .job-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .job-salary {
    margin-left: 0;
  }
  
  .favorite-icon {
    right: 20px;
    bottom: 20px;
  }
}
</style>