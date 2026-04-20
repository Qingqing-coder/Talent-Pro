<template>
  <div class="job-detail-page">
    <div class="header-nav">
      <div class="nav-left">
        <div class="brand-logo" @click="goHome">
          <div class="logo-icon">
            <div class="logo-inner"></div>
          </div>
          <span class="logo-text">Talent <span class="text-highlight">Pro</span></span>
        </div>
        <div class="nav-links">
          <span class="nav-link" @click="goHome">首页</span>
        </div>
      </div>
      <div class="nav-center">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索职位、公司"
            class="search-input custom-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch" type="primary" class="search-button">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
      <div class="nav-right">
        <div class="user-actions">
          <div class="message-btn" @click="showDialogue = true">
            <el-icon><Message /></el-icon>
          </div>
          <span class="action-link" @click="goToUpdateUser">简历</span>
          <div class="user-profile" @click="goPersonalCenter" style="cursor:pointer;">
            <el-avatar :size="32" :src="userAvatar || defaultAvatar" />
            <span class="username">{{ userName || '未登录' }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="content-left">
        <div v-if="loading" class="job-overview">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="jobData" class="job-overview">
          <div class="job-status">
            <el-tag type="success" size="small" color="#e6f4ea" style="border-color: #bce3c8; color: #1e8e3e;">招聘中</el-tag>
          </div>
          <h1 class="job-title">{{ jobData.title }}</h1>
          <div class="job-salary">{{ jobData.salary }}</div>
          <div class="job-requirements">
            <span class="requirement-item">{{ jobData.fullLocation }}</span>
            <span class="requirement-item">{{ jobData.experience }}</span>
            <span class="requirement-item">{{ jobData.education }}</span>
          </div>
          <div class="job-actions">
            <el-button
              type="default"
              class="interest-btn"
              @click="handleFavorite"
              :class="{ 'is-favorite': isFavorite }"
            >
              <el-icon v-if="isFavorite" color="#f97316"><StarFilled /></el-icon>
              <el-icon v-else><Star /></el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button type="primary" class="contact-btn theme-btn-primary" @click="handleContact">立即沟通</el-button>
          </div>
          <div class="resume-options">
            <div class="resume-option" @click="goToUpdateUser">
              <el-icon><Document /></el-icon>
              <span>完善在线简历</span>
            </div>
          </div>
        </div>

        <div v-if="jobData" class="job-description">
          <h3>职位描述</h3>
          <div class="job-tags">
            <el-tag size="small" type="info" class="custom-tag">{{ jobData.type }}</el-tag>
            <el-tag size="small" type="warning" class="custom-tag-warning">{{ jobData.jobType }}</el-tag>
            <el-tag size="small" type="success" class="custom-tag-success">接受无相关经验/技能</el-tag>
          </div>
          <div class="description-content">
            <p><strong>职位类型：</strong>{{ jobData.type }}</p>
            <p><strong>职位所属大类：</strong>{{ jobData.jobType }}</p>
            <p><strong>公司名称：</strong>{{ jobData.companyName }}</p>
            <p><strong>公司规模：</strong>{{ jobData.companySize }}</p>
            <p><strong>公司所在地：</strong>{{ jobData.fullLocation }}</p>
            <p><strong>经验要求：</strong>{{ jobData.experience }}</p>
            <p><strong>学历要求：</strong>{{ jobData.education }}</p>
            <p><strong>职位待遇福利：</strong>{{ Array.isArray(jobData.benefits) ? jobData.benefits.join('、') : jobData.benefits }}</p>
            <p><strong>创建时间：</strong>{{ jobData.createTime }}</p>
            <p><strong>收藏量：</strong>{{ jobData.preNum }}</p>
          </div>
        </div>

        <div v-if="jobData" class="recruiter-info">
          <h3>招聘者</h3>
          <div class="recruiter-card">
            <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <div class="recruiter-details">
              <div class="recruiter-name">{{ jobData.companyName }}</div>
              <div class="recruiter-status">刚刚活跃</div>
              <div class="recruiter-role">{{ jobData.companyName }} · 招聘者</div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-right">
        <div v-if="jobData" class="company-info">
          <h3>公司基本信息</h3>
          <div class="company-card">
            <div class="company-logo">
              <el-icon :size="40"><OfficeBuilding /></el-icon>
            </div>
            <div class="company-details">
              <div class="company-name">{{ jobData.companyName }}</div>
              <div class="company-size">{{ jobData.companySize }}</div>
              <div class="company-industry">{{ jobData.jobType }}</div>
            </div>
            <el-button type="text" class="view-jobs-btn">查看全部职位</el-button>
          </div>
        </div>
      </div>
    </div>

    <DialogueComponent
      :visible="showDialogue"
      :job-title="jobData?.title || '测试工程师'"
      :current-job-hr="{
        id: 1,
        name: '招聘者',
        title: 'HR',
        company: jobData?.companyName || '北京中创科软科技有限公司',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }"
      @close="showDialogue = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Search,
  Message,
  Star,
  StarFilled,
  Document,
  Upload,
  OfficeBuilding
} from '@element-plus/icons-vue'
import { searchJobs, isPred, addPre, deletePre } from '@/api/job'
import { getAuthStatus } from '@/utils/auth.ts'
import { ElMessage } from 'element-plus'
import DialogueComponent from './dialogue.vue'
import { getEmpMessage } from '@/api/empMessage'
import { getAvatarPath } from '@/api/user'

const API_BASE_URL = 'http://127.0.0.1:8080'
const userName = ref('')
const userAvatar = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 路由相关
const route = useRoute()
const router = useRouter()

// 响应式数据
const searchKeyword = ref('')
const jobData = ref<any>(null)
const loading = ref(true)
const isFavorite = ref(false)
const showDialogue = ref(false)
const { username } = getAuthStatus()

// 职位数据接口
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

// 获取职位详情
const fetchJobDetail = async () => {
  try {
    const jobId = Number(route.params.id)
    const response = await searchJobs({
      id: jobId,
      pageNum: 1,
      pageSize: 1
    })

    if (response.data.list && response.data.list.length > 0) {
      jobData.value = response.data.list[0]

      // 检查是否已收藏
      if (username) {
        try {
          const favoriteRes = await isPred(jobId)
          isFavorite.value = favoriteRes.data
        } catch (error) {
          console.error('检查收藏状态失败:', error)
          isFavorite.value = false
        }
      } else {
        isFavorite.value = false
      }
    } else {
      ElMessage.error('职位信息不存在')
      router.push('/shouye')
    }
  } catch (error) {
    console.error('获取职位详情失败:', error)
    ElMessage.error('获取职位详情失败')
    router.push('/shouye')
  } finally {
    loading.value = false
  }
}

// 处理收藏
const handleFavorite = async () => {
  if (!username) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const jobId = Number(route.params.id)
    if (isFavorite.value) {
      // 取消收藏
      await deletePre(username, jobId)
      isFavorite.value = false
      ElMessage.success('取消收藏成功')
      // 更新收藏数量
      if (jobData.value) {
        jobData.value.preNum = Math.max(0, jobData.value.preNum - 1)
      }
    } else {
      // 添加收藏
      await addPre({
        username: username,
        jobId: jobId
      })
      isFavorite.value = true
      ElMessage.success('收藏成功')
      // 更新收藏数量
      if (jobData.value) {
        jobData.value.preNum += 1
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 处理沟通
const handleContact = () => {
  if (!username) {
    ElMessage.warning('请先登录')
    return
  }
  showDialogue.value = true
}

// 返回首页
const goHome = () => {
  router.push('/shouye')
}

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/shouye',
      query: {
        search: searchKeyword.value.trim(),
        from: 'jobDetail'
      }
    })
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

// 跳转到完善简历页面
const goToUpdateUser = () => {
  router.push('/updateuser')
}

// 页面加载时获取数据
onMounted(() => {
  window.scrollTo(0, 0);
  fetchJobDetail()
  getEmpMessage().then(res => {
    const user = res.data.list[0]
    userName.value = user?.name || user?.username || '未登录'
    getAvatarPath(user.username).then(res2 => {
      let filePath = res2.data
      if (filePath && !filePath.startsWith('/')) filePath = '/' + filePath
      userAvatar.value = filePath ? API_BASE_URL + filePath : ''
    })
  })
})

const goPersonalCenter = () => {
  router.push('/personalcenter')
}
</script>

<style scoped>
/* ================= 全局与基础 ================= */
.job-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa; 
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ================= 头部导航栏 (同步 Header 样式) ================= */
.header-nav {
  /* 高端深色渐变底色 */
  background: linear-gradient(135deg, #111417 0%, #1a1f24 100%);
  /* 增加现代感毛玻璃效果 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  /* 更精细的分割线 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

/* ========== 同步 Logo 样式 ========== */
.brand-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: opacity 0.3s, transform 0.3s;
}

.brand-logo:hover {
  opacity: 0.95;
  transform: scale(1.02);
}

.logo-icon {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #fb923c 0%, #f97316 60%, #ea580c 100%);
  border-radius: 9px;
  margin-right: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4);
}

.logo-inner {
  width: 11px;
  height: 11px;
  background: #ffffff;
  border-radius: 2px;
  transform: rotate(45deg);
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.5px;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.1);
}

.logo-text .text-highlight {
  color: #f97316;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-link {
  color: #9ca3af;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
  padding: 8px 12px;
  border-radius: 6px;
}

.nav-link:hover {
  color: white;
  background: rgba(255, 255, 255, 0.08);
}

/* ================= 头部搜索区 ================= */
.nav-center {
  flex: 1;
  max-width: 400px;
  margin: 0 48px;
}

.search-box {
  width: 100%;
}

.search-input {
  width: 100%;
}

:deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 20px 0 0 20px;
  box-shadow: none !important;
  transition: all 0.3s;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  background: white;
  box-shadow: inset 0 0 0 1px #f97316 !important;
}

:deep(.custom-input .el-input-group__append) {
  border-radius: 0 20px 20px 0;
  border: none;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  padding: 0 20px;
}

:deep(.custom-input .el-input-group__append .el-button) {
  border: none;
  background: transparent;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input-group__append .el-button:hover) {
  transform: scale(1.05);
}

/* ================= 头部右侧操作 ================= */
.nav-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.message-btn {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #9ca3af;
  transition: all 0.3s;
  padding: 8px;
  border-radius: 6px;
}

.message-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.08);
}

.action-link {
  color: #9ca3af;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
  padding: 8px 12px;
  border-radius: 6px;
}

.action-link:hover {
  color: white;
  background: rgba(255, 255, 255, 0.08);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.3s;
}
.user-profile:hover {
  background: rgba(255, 255, 255, 0.08);
}

.username {
  font-size: 14px;
  color: white;
  font-weight: 500;
}

/* ================= 主要内容区域布局 ================= */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px;
  display: flex;
  gap: 24px;
}

.content-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.content-right {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex-shrink: 0;
}

/* ================= 通用卡片样式 ================= */
.job-overview,
.job-description,
.recruiter-info,
.company-info {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.job-overview:hover,
.job-description:hover,
.recruiter-info:hover,
.company-info:hover {
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.05);
  transform: translateY(-2px);
  border-color: #fdba74;
}

/* ================= 职位概览 ================= */
.job-status {
  margin-bottom: 16px;
}

.job-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.job-salary {
  font-size: 24px;
  font-weight: 800;
  color: #ea580c;
  margin-bottom: 20px;
  display: inline-block;
  padding: 8px 16px;
  background: rgba(249, 115, 22, 0.06);
  border-radius: 8px;
  border: 1px solid rgba(249, 115, 22, 0.2);
}

.job-requirements {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.requirement-item {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  padding: 6px 14px;
  background: #f8fafc;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.job-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.interest-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  border: 1px solid #cbd5e1;
  background: #ffffff;
  color: #4b5563;
  font-weight: 500;
  height: 44px;
  padding: 0 24px;
  transition: all 0.3s;
}

.interest-btn:hover {
  border-color: #f97316;
  color: #ea580c;
  background: #fff4ed;
}

.interest-btn.is-favorite {
  border-color: #f97316;
  background: #fff4ed;
  color: #ea580c;
}

.theme-btn-primary {
  flex: 1;
  height: 44px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  font-size: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}

.theme-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4);
}

.resume-options {
  display: flex;
  gap: 16px;
}

.resume-option {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 12px 16px;
  border-radius: 8px;
  transition: all 0.3s;
  border: 1px dashed #cbd5e1;
  background: #f8fafc;
}

.resume-option:hover {
  border-color: #f97316;
  color: #f97316;
  background: #ffffff;
}

/* ================= 职位描述 ================= */
.job-description h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #111827;
  font-weight: 700;
  position: relative;
  padding-left: 12px;
}

.job-description h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: #f97316;
  border-radius: 2px;
}

.job-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.custom-tag {
  background-color: #f1f5f9;
  border-color: #e2e8f0;
  color: #475569;
}
.custom-tag-warning {
  background-color: #fffaf5;
  border-color: #ffedd5;
  color: #ea580c;
}
.custom-tag-success {
  background-color: #f0fdf4;
  border-color: #dcfce3;
  color: #16a34a;
}

.description-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.description-content p {
  margin: 0;
  line-height: 1.6;
  color: #4b5563;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  font-size: 14px;
}

.description-content p::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: #f97316;
  opacity: 0;
  transition: opacity 0.3s;
}

.description-content p:hover {
  background: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
  border-color: #fdba74;
}
.description-content p:hover::before {
  opacity: 1;
}

.description-content strong {
  color: #111827;
  font-weight: 600;
  margin-right: 8px;
  display: inline-block;
  min-width: 90px;
}

/* ================= 招聘者信息 ================= */
.recruiter-info h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #111827;
  font-weight: 700;
}

.recruiter-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.recruiter-card:hover {
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #cbd5e1;
}

.recruiter-details {
  flex: 1;
}

.recruiter-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.recruiter-status {
  font-size: 12px;
  color: #10b981;
  font-weight: 500;
  margin-bottom: 4px;
}

.recruiter-role {
  font-size: 13px;
  color: #64748b;
}

/* ================= 公司基本信息 ================= */
.company-info {
  padding: 24px;
}

.company-info h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.company-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.company-logo {
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.company-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

.company-size,
.company-industry {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.view-jobs-btn {
  font-size: 14px;
  color: #f97316;
  font-weight: 500;
  align-self: flex-start;
  padding: 0;
  margin-top: 8px;
}
.view-jobs-btn:hover {
  color: #ea580c;
}

/* ================= 响应式设计 ================= */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
  .content-right {
    width: 100%;
  }
  .company-card {
    flex-direction: row;
    align-items: center;
  }
  .view-jobs-btn {
    margin-top: 0;
    margin-left: auto;
  }
}

@media (max-width: 768px) {
  .header-nav {
    padding: 0 16px;
  }
  .nav-links {
    display: none;
  }
  .nav-center {
    margin: 0 16px;
  }
  .main-content {
    padding: 16px;
  }
  .job-actions {
    flex-direction: column;
  }
  .interest-btn {
    width: 100%;
    justify-content: center;
  }
  .resume-options {
    flex-direction: column;
  }
  .description-content {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}
</style>