<template>
  <div class="content">
    
    <LeftSidebar 
      :category-data="categoryData"
      :selected-category="selectedCategory"
      :favorite-jobs="favoriteJobs"
      :pre-page-num="prePageNum"
      :pre-page-total="prePageTotal"
      @select-category="selectCategory"
      @detail="handleDetail"
      @toggle-favorite="handlePre"
      @pre-page-change="handlePreCurrentChange"
    />

    <div class="content-right">
      
      <WelcomeBanner 
        :job-total="jobTotal"
        :company-total="companyTotal"
        :emp-total="empTotal"
        @go-assistant="goToAssistant"
      />

      <div class="search-section">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索职位或公司，宁少勿错！"
            class="search-input custom-input"
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" class="search-btn theme-btn-primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <div class="filter-section">
        <div class="filter-tabs">
          <span
            class="tab"
            :class="selectTabIndex === index ? 'tab-select' : ''"
            v-for="(item, index) in tabData"
            :key="index"
            @click="selectTab(index)"
          >
            {{ item }}
          </span>
        </div>
        <div class="filter-info">
          <span>共找到 <span class="highlight-text">{{ total }}</span> 个职位</span>
        </div>
      </div>

      <JobList 
        :page-job-data="pageJobData"
        :loading="loading"
        :current-page="currentPage"
        :total="total"
        @detail="handleDetail"
        @toggle-favorite="handlePre"
        @page-change="handleCurrentChange"
      />

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { searchJobs, addPre, deletePre, getCompanyTotal, getEmpTotal, getEmpPred, isPred } from '@/api/job'
import { Search } from '@element-plus/icons-vue'
import { getAuthStatus } from '@/utils/auth.ts'
import { ElMessage } from 'element-plus'
import type { Pre } from '@/api/job'

// 导入刚拆分的子组件
import LeftSidebar from './sidebar.vue'
import WelcomeBanner from './WelcomeBanner.vue'
import JobList from './JobList.vue'

const router = useRouter()
const route = useRoute()

// ======================== 数据与状态 ========================
const categoryData = reactive([
  { id: 1, title: '开发' }, { id: 2, title: '技术' }, { id: 3, title: '金融' },
  { id: 4, title: '教育' }, { id: 5, title: '医疗' }, { id: 6, title: '电商' },
  { id: 7, title: '物流' }, { id: 8, title: '销售' }, { id: 9, title: '能源' },
  { id: 10, title: '游戏' }, { id: 11, title: '市场' }, { id: 12, title: '房地产' }
])
const tabData = reactive(['最热推荐', '最新', '高薪'])

const selectedCategory = ref(-1)      
const searchKeyword = ref('')         
const selectTabIndex = ref(0)         
const jobData = ref<any[]>([])    
const pageJobData = ref<any[]>([])
const loading = ref(false)            
const currentPage = ref(1)            
const total = ref(0)                  
const { username } = getAuthStatus() 
const pre = ref<Pre>({ username: '', jobId: 0 })                                          
const companyTotal = ref(0)   
const empTotal = ref(0)     
const jobTotal = ref(0)    
const favoriteJobs = ref<any[]>([]) 
const prePageNum = ref(1)  
const prePageTotal = ref(0) 

// ======================== 核心逻辑 ========================
const goToAssistant = () => {
  sessionStorage.setItem('fromHome', 'true')
  router.push('/assistant')
}

const fetchJobData = async (categoryId: number, sortField: string, sortOrder: string, pageNum: number, pageSize: number) => {
  loading.value = true;
  const categoryTitle = categoryId !== -1 ? categoryData.find(item => item.id === categoryId)?.title : '';

  let [resultA, resultB] = await Promise.allSettled([
    searchJobs({ jobType: categoryTitle, sortField, sortOrder, title: searchKeyword.value, pageNum, pageSize }),
    searchJobs({ jobType: categoryTitle, sortField, sortOrder, companyName: searchKeyword.value, pageNum, pageSize })
  ]);

  let rawTotalCount = 0;
  const successfulResults = [];

  if (resultA.status === 'fulfilled') {
    successfulResults.push(...resultA.value.data.list);
    rawTotalCount += resultA.value.data.total;
  }
  if (resultB.status === 'fulfilled') {
    successfulResults.push(...resultB.value.data.list);
    rawTotalCount += resultB.value.data.total;
  }

  pageJobData.value = [...new Map(successfulResults.map(item => [item.id, item])).values()];
  total.value = rawTotalCount;

  if (username) {
    for (const job of pageJobData.value) {
      try {
        const res = await isPred(job.id);
        job.isFavorite = res.data;
      } catch (error) {
        job.isFavorite = false;
      }
    }
  } else {
    pageJobData.value.forEach(job => job.isFavorite = false);
  }
  loading.value = false;
};

const selectCategory = async (id: number) => {
  selectedCategory.value = selectedCategory.value === id ? -1 : id;
  currentPage.value = 1;
  await fetchJobData(selectedCategory.value, tabData[selectTabIndex.value],'desc', currentPage.value, 10)
};

const handlePre = async (item: any) => {
  pre.value.username = username;
  pre.value.jobId = item.id;
  try {
    if (item.isFavorite) {
      await deletePre(pre.value.username, pre.value.jobId);
      item.isFavorite = false;
    } else {
      await addPre(pre.value);
      item.isFavorite = true;
    }
    await fetchFavoriteJobs();
  } catch (error) {
    console.error("操作失败:", error);
  }
};

const handleShowPred = async () => {
  pageJobData.value.forEach(job => {
    isPred(job.id).then(result => {
      job.isFavorite = result.data;
    })
  })
}
watch(pageJobData, handleShowPred)

const handleSearch = async () => {
  currentPage.value = 1;
  await fetchJobData(selectedCategory.value, tabData[selectTabIndex.value], 'desc', currentPage.value, 10)
  if (route.query.from === 'jobDetail') ElMessage.success(`正在搜索：${searchKeyword.value}`)
};

const handleCurrentChange = async (val: number) => {
  currentPage.value = val;
  await fetchJobData(selectedCategory.value, tabData[selectTabIndex.value], 'desc', currentPage.value, 10);
};

const handlePreCurrentChange = async (val: number) => {
  prePageNum.value = val;
  await fetchFavoriteJobs();
}

const handleDetail = (item: any) => {
  router.push(`/job-detail/${item.id}`);
};

const selectTab = (index: number) => {
  selectTabIndex.value = index;
  if (currentPage.value !== 1) currentPage.value = 1;
  fetchJobData(selectedCategory.value, tabData[selectTabIndex.value], 'desc', 1, 10);
};

const fetchFavoriteJobs = async () => {
  try {
    const res = await getEmpPred(prePageNum.value);
    favoriteJobs.value = res.data.list.map((job: any) => ({ ...job, isFavorite: true }));
    prePageTotal.value = res.data.total;
  } catch (error) {
    console.error("获取已收藏工作失败:", error);
  }
};

const initData = async () => {
  const searchParam = route.query.search as string
  if (searchParam) {
    searchKeyword.value = searchParam
    await handleSearch()
  } else {
    await fetchJobData(-1, '最热推荐', 'desc', 1, 10)
  }
  searchJobs({ pageNum: 1, pageSize: 10 }).then(res => { jobTotal.value = res.data.total; })
  getCompanyTotal().then(res => { companyTotal.value = res.data.total; })
  getEmpTotal().then(res => { empTotal.value = res.data.total; })
  fetchFavoriteJobs();
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
/* ================= 基础布局 ================= */
.content {
  display: flex;
  flex-direction: row;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 16px;
  gap: 24px;
  min-height: calc(100vh - 120px);
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}
.content-right { flex: 1; max-width: none; }

/* ================= 通用卡片样式 ================= */
.search-section, .filter-section {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

/* ================= 搜索与筛选区 ================= */
.search-section { padding: 20px; margin-bottom: 24px; }
.search-box { display: flex; gap: 12px; }
.search-input { flex: 1; }

:deep(.custom-input .el-input__wrapper) { background-color: #f8fafc; box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important; border-radius: 8px; transition: all 0.3s; }
:deep(.custom-input .el-input__wrapper:hover) { box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important; }
:deep(.custom-input .el-input__wrapper.is-focus) { background-color: #ffffff; box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important; }

.theme-btn-primary { width: 120px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important; border: none !important; color: #ffffff !important; font-weight: 600; border-radius: 8px; box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3); transition: all 0.3s ease; }
.theme-btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4); }

.filter-section { padding: 16px 24px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
.filter-tabs { display: flex; gap: 16px; }
.tab { padding: 8px 18px; cursor: pointer; border-radius: 20px; transition: all 0.3s; font-size: 14px; font-weight: 500; color: #64748b; border: 1px solid transparent; }
.tab:hover { color: #111827; background: #f1f5f9; }
.tab.tab-select { background: #f97316; color: white; border-color: #f97316; box-shadow: 0 4px 10px rgba(249, 115, 22, 0.2); }
.filter-info { color: #64748b; font-size: 14px; }
.highlight-text { color: #f97316; font-weight: 600; margin: 0 4px; }

/* ================= 移动端适配 ================= */
@media (max-width: 768px) {
  .content { flex-direction: column; padding: 16px; }
  .search-box { flex-direction: column; }
  .theme-btn-primary { width: 100%; }
  .filter-section { flex-direction: column; gap: 16px; align-items: flex-start; }
}
</style>