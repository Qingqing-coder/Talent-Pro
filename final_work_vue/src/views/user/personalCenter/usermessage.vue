<template>
  <div class="personal-info-container">
    <div class="page-nav">
      <el-button link class="back-btn" @click="handleBack">
        <el-icon><ArrowLeft /></el-icon> 返回个人中心
      </el-button>
    </div>
    <el-card class="info-card" :body-style="{ padding: '0px' }">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <h2 class="profile-title">个人资料</h2>
          <el-avatar :size="100" class="user-avatar">
            <img :src="userInfo.avatar" alt="用户头像" @error="userInfo.avatar = '/默认头像路径.jpg'" />
          </el-avatar>
          <div class="avatar-upload">
            <el-button type="primary" text class="update-avatar-btn" @click="handleEdit">更新头像</el-button>
          </div>
        </div>
        <div class="user-name">{{ userInfo.name }}</div>
      </div>

      <div class="info-section">
        <el-card class="inner-card">
          <div class="form-group">
            <div class="group-header">
              <h4 class="form-section-title">基本信息</h4>
            </div>
            <el-descriptions :column="2" border class="custom-descriptions">
              <el-descriptions-item>
                <template #label>昵称</template>
                <span>{{ userInfo.nickname }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>用户名</template>
                <span>{{ userInfo.username }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>真实姓名</template>
                <span>{{ userInfo.name }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>性别</template>
                <span>{{ userInfo.sex === 1 ? '男' : userInfo.sex === 0 ? '女' : '保密' }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>年龄</template>
                <span>{{ userInfo.age }}岁</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>手机号</template>
                <span>{{ userInfo.phone }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>邮箱</template>
                <span>{{ userInfo.email }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>地址</template>
                <span>{{ userInfo.address }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="form-group">
            <div class="group-header">
              <h4 class="form-section-title">求职相关信息</h4>
            </div>
            <el-descriptions :column="2" border class="custom-descriptions">
              <el-descriptions-item>
                <template #label>学历</template>
                <span>{{ userInfo.eduBack }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>工作经验</template>
                <span>{{ userInfo.hasExperience === 1 ? '有工作经验' : '无工作经验' }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>期望薪资</template>
                <span class="highlight">{{ userInfo.salaryWishK }}K/月</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>专业</template>
                <span>{{ userInfo.major }}</span>
              </el-descriptions-item>
              <el-descriptions-item :span="2">
                <template #label>个人介绍</template>
                <div class="introduction-content">{{ userInfo.introduction }}</div>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </div>

      <div class="action-buttons">
        <el-button type="primary" class="theme-btn-primary" @click="handleEdit">编辑信息</el-button>
        <el-button class="theme-btn-outline" @click="handlePrint">打印信息</el-button>
        <el-button class="theme-btn-outline" @click="exportUserInfo">
          <el-icon><Download /></el-icon>
          导出信息
        </el-button>
        <el-button type="success" class="theme-btn-primary" @click="handleExportPDF">导出PDF</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Download, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'
import { getEmpMessage } from '@/api/empMessage'
import { getAvatarPath } from '@/api/user'
const API_BASE_URL = 'http://127.0.0.1:8080'

getEmpMessage().then((res)=>{
  userInfo.value = res.data.list[0]
  // 获取头像路径
  getAvatarPath(userInfo.value.username).then(res2 => {
    let filePath = res2.data
    if (filePath && !filePath.startsWith('/')) {
      filePath = '/' + filePath
    }
    userInfo.value.avatar = filePath ? API_BASE_URL + filePath : ''
  })
})

const router = useRouter()
const userInfo = ref({
  username: '张三',
  nickname: '小张',
  name: '张三丰',
  sex: 1,
  age: 25,
  eduBack: '本科',
  salaryWishK: 15,
  phone: '13800138000',
  email: 'zhangsan@example.com',
  address: '北京市朝阳区',
  hasExperience: 1,
  jobStatus: '在职找工作',
  introduction: '热爱编程，对新技术充满热情。有3年Web开发经验，熟悉Vue.js和Node.js技术栈。',
  avatar: '', // 添加头像base64
  major: '计算机科学与技术' // 新增专业字段
})

// 返回首页
const handleBack = () => {
  router.push('/personalcenter')
}

// 编辑信息
const handleEdit = () => {
  router.push('/updateuser')
}

// 打印信息
const handlePrint = () => {
  window.print()
}

// 导出JSON
const exportUserInfo = () => {
  const dataStr = JSON.stringify(userInfo.value, null, 2)
  const blob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `个人信息_${userInfo.value.username}_${new Date().toLocaleDateString()}.json`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  //ElMessage.success('信息导出成功')
}

// 导出PDF
const handleExportPDF = async () => {
  const card = document.querySelector('.info-card') as HTMLElement
  const actionBtns = card?.querySelector('.action-buttons') as HTMLElement
  //await waitForImagesToLoad(card)
  if (!card) {
    ElMessage.error('未找到导出区域')
    return
  }
  // 临时隐藏按钮
  if (actionBtns) actionBtns.style.display = 'none'
  // 先用html2canvas截图
  const canvas = await html2canvas(card, { scale: 2 })
  // 恢复按钮显示
  if (actionBtns) actionBtns.style.display = ''
  const imgData = canvas.toDataURL('image/png')
  const pdf = new jsPDF({
    orientation: 'p',
    unit: 'mm',
    format: 'a4'
  })
//
// // 辅助函数：等待元素内所有图片加载完成
// function waitForImagesToLoad(element: HTMLElement): Promise<void[]> {
//   const images = element.getElementsByTagName('img')
//   const imagePromises: Promise<void>[] = []
//
//   for (let i = 0; i < images.length; i++) {
//     const img = images[i]
//     if (!img.complete) {
//       const promise = new Promise<void>((resolve) => {
//         img.onload = () => resolve()
//         img.onerror = () => resolve() // 即使加载失败也继续
//       })
//       imagePromises.push(promise)
//     }
//   }
//
//   return Promise.all(imagePromises)
// }

  // 计算图片宽高自适应A4纸
  const pageWidth = pdf.internal.pageSize.getWidth()
  const pageHeight = pdf.internal.pageSize.getHeight()
  const imgProps = pdf.getImageProperties(imgData)
  let pdfWidth = pageWidth - 20
  let pdfHeight = (imgProps.height * pdfWidth) / imgProps.width
  let position = 10
  if (pdfHeight > pageHeight - 20) {
    pdfHeight = pageHeight - 20
    pdfWidth = (imgProps.width * pdfHeight) / imgProps.height
    position = 10
  }
  pdf.addImage(imgData, 'PNG', 10, position, pdfWidth, pdfHeight)
  pdf.save(`个人信息_${userInfo.value.username}.pdf`)
  ElMessage.success('PDF导出成功')
}

</script>

<style scoped>
/* ================= 全局与容器 ================= */
.personal-info-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px 60px;
  /* 统一极简浅灰背景 */
  background: #f5f7fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ================= 顶部导航 ================= */
.page-nav {
  padding: 0 0 20px 0;
  display: flex;
  align-items: center;
}

.back-btn {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 8px 12px;
  border-radius: 8px;
}

.back-btn:hover {
  color: #ea580c;
  background-color: #fff4ed; /* 浅橙色背景反馈 */
}

/* ================= 主卡片 ================= */
.info-card {
  background-color: #fff;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;
  border: none;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.info-card:hover {
  box-shadow: 0 15px 40px rgba(249, 115, 22, 0.08);
}

/* ================= 头像区域 (深空灰渐变) ================= */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0 40px;
  /* 系统统一的高级深空灰渐变 */
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  color: #fff;
  position: relative;
}

/* 科技感暗纹点缀 */
.avatar-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(249,115,22,0.08) 0%, rgba(0,0,0,0) 70%);
  pointer-events: none;
}

.profile-title {
  margin: 0 0 20px 0;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #ffffff;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
  background: #f8fafc;
}

.user-avatar:hover {
  transform: scale(1.05);
  border-color: #f97316; /* 悬浮时边框变橙 */
}

.avatar-upload {
  margin-top: 16px;
  text-align: center;
}

.update-avatar-btn {
  color: #fdba74 !important; /* 浅橙色 */
  font-weight: 500;
  transition: all 0.3s;
}
.update-avatar-btn:hover {
  color: #f97316 !important;
  background: rgba(249, 115, 22, 0.1) !important;
  border-radius: 20px;
}

.user-name {
  font-size: 26px;
  font-weight: 800;
  margin-bottom: 8px;
  letter-spacing: 1px;
  z-index: 2;
}

/* ================= 信息列表区 ================= */
.info-section {
  padding: 32px;
  background: #ffffff;
}

.inner-card {
  border: none;
  box-shadow: none;
  background: transparent;
}

.form-group {
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
  transition: all 0.3s;
}

.form-group:hover {
  border-color: #fdba74;
  box-shadow: 0 4px 16px rgba(249, 115, 22, 0.05);
}

.group-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.form-section-title {
  font-size: 18px;
  color: #111827;
  font-weight: 700;
  margin: 0;
  padding-left: 12px;
  /* 活力橙竖线装饰 */
  border-left: 4px solid #f97316;
  background: none;
}

/* ================= Descriptions 样式覆盖 ================= */
:deep(.custom-descriptions .el-descriptions__body) {
  background-color: transparent;
}

:deep(.custom-descriptions .el-descriptions__label) {
  background-color: #f8fafc;
  padding: 16px 20px;
  text-align: right;
  font-weight: 600;
  color: #4b5563;
  width: 20%;
  min-width: 100px;
  border-color: #e2e8f0;
}

:deep(.custom-descriptions .el-descriptions__content) {
  padding: 16px 20px;
  color: #111827;
  font-weight: 500;
  border-color: #e2e8f0;
  background-color: #ffffff;
}

.highlight {
  /* 活力橙高亮薪资 */
  color: #ea580c;
  font-weight: 800;
  font-size: 16px;
}

.introduction-content {
  line-height: 1.8;
  padding: 16px;
  background-color: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  color: #4b5563;
  min-height: 80px;
  white-space: pre-wrap;
  font-size: 14px;
}

/* ================= 操作按钮区 ================= */
.action-buttons {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 0 32px 40px;
  background: #ffffff;
}

/* 统一的主按钮 (编辑信息、导出PDF) */
.theme-btn-primary {
  min-width: 140px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  border-radius: 24px !important;
  color: #ffffff !important;
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}
.theme-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(234, 88, 12, 0.4);
}

/* 统一的线框副按钮 (打印、导出JSON) */
.theme-btn-outline {
  min-width: 140px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  border-radius: 24px !important;
  transition: all 0.3s;
}
.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .personal-info-container {
    padding: 16px 10px;
    margin: 0;
  }
  
  .avatar-section {
    padding: 40px 0 30px;
  }
  
  .user-name {
    font-size: 22px;
  }
  
  .info-section {
    padding: 16px;
  }
  
  .form-group {
    padding: 16px;
  }
  
  :deep(.custom-descriptions .el-descriptions__label) {
    width: 30%;
    padding: 12px;
  }
  
  :deep(.custom-descriptions .el-descriptions__content) {
    padding: 12px;
  }
  
  .action-buttons {
    flex-direction: column;
    padding: 0 16px 30px;
  }
  
  .action-buttons .el-button {
    width: 100%;
    margin-left: 0 !important;
  }
}

/* ================= 打印模式优化 ================= */
@media print {
  .personal-info-container {
    background: none;
    padding: 0;
    margin: 0;
  }
  .page-nav,
  .action-buttons,
  .avatar-upload {
    display: none !important;
  }
  .info-card {
    box-shadow: none !important;
    border: none !important;
  }
  .avatar-section {
    background: none !important;
    color: #000 !important;
    padding: 20px 0 !important;
  }
  .avatar-section::before {
    display: none;
  }
  .user-name {
    text-shadow: none !important;
    color: #000 !important;
  }
  .user-avatar {
    border: 2px solid #ddd !important;
    box-shadow: none !important;
  }
  .form-group {
    box-shadow: none !important;
    border: 1px solid #ddd !important;
  }
}
</style>