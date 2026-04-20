<template>
  <div class="company-profile-page">
    <div class="profile-container">
      <el-card class="profile-card" :body-style="{ padding: '0' }">
        <template #header>
          <div class="card-header">
            <span class="header-title">企业个人中心</span>
            <el-button class="theme-btn-primary" @click="openEditDialog">
              编辑资料
            </el-button>
          </div>
        </template>
        
        <div class="profile-content">
          <el-descriptions :column="1" border class="custom-descriptions">
            <el-descriptions-item>
              <template #label>
                <div class="desc-label">公司账号</div>
              </template>
              <span class="desc-value">{{ companyInfo.username }}</span>
            </el-descriptions-item>
            
            <el-descriptions-item>
              <template #label>
                <div class="desc-label">公司名称</div>
              </template>
              <span class="desc-value highlight-text">{{ companyInfo.nickname }}</span>
            </el-descriptions-item>
            
            <el-descriptions-item>
              <template #label>
                <div class="desc-label">密 码</div>
              </template>
              <span class="desc-value">{{ companyInfo.password }}</span>
            </el-descriptions-item>
            
            <el-descriptions-item>
              <template #label>
                <div class="desc-label">公司简介</div>
              </template>
              <div class="intro-content">{{ companyInfo.companyIntroduction || '暂无简介' }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>
    </div>

    <el-dialog 
      title="编辑企业资料" 
      v-model="dialogVisible" 
      width="550px" 
      class="custom-dialog"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px" :rules="rules" ref="formRef" class="custom-form">
        <el-form-item label="公司账号" prop="username">
          <el-input v-model="form.username" disabled class="custom-input" />
        </el-form-item>
        
        <el-form-item label="公司名称" prop="nickname">
          <el-input v-model="form.nickname" class="custom-input" placeholder="请输入公司名称" />
        </el-form-item>
        
        <el-form-item label="密 码" prop="password">
          <el-input v-model="form.password" class="custom-input" placeholder="请输入密码" />
        </el-form-item>
        
        <el-form-item label="公司简介" prop="companyIntroduction">
          <el-input 
            v-model="form.companyIntroduction" 
            type="textarea" 
            :rows="5"
            class="custom-textarea" 
            placeholder="请输入公司简介" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="theme-btn-outline" @click="dialogVisible = false">取消</el-button>
        <el-button class="theme-btn-primary" type="primary" @click="handleSubmit" :loading="dialogLoading">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getComList, updateCom } from '@/api/company'
import { showCustomMessage } from '@/utils/message'
import type { FormInstance, FormRules } from 'element-plus'

const companyInfo = reactive({
  username: '',
  nickname: '',
  companyIntroduction: '',
  password:'',
})

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  username: '',
  nickname: '',
  companyIntroduction: '',
  password:'',

})

const rules: FormRules = {
  nickname: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  companyIntroduction: [{ required: true, message: '请输入公司简介', trigger: 'blur' }],
}

// 获取当前登录企业的username
const currentUsername = localStorage.getItem('companyUsername') || ''

const fetchCompanyInfo = async () => {
  try {
    const res = await getComList({ username: currentUsername, pageNum: 1, pageSize: 10 })
    const info = res.data.list && res.data.list[0] ? res.data.list[0] : {}
    Object.assign(companyInfo, info)
    // showCustomMessage('获取企业信息成功', 'success')
  } catch (e: any) {
    // showCustomMessage('获取企业信息失败: ' + e.message, 'error')
  }
}

const openEditDialog = () => {
  Object.assign(form, companyInfo)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    dialogLoading.value = true
    try {
      await updateCom(form)
      showCustomMessage('企业资料更新成功', 'success')
      dialogVisible.value = false
      fetchCompanyInfo()
    } catch (e: any) {
      showCustomMessage('企业资料更新失败: ' + e.message, 'error')
    } finally {
      dialogLoading.value = false
    }
  })
}

onMounted(() => {
  fetchCompanyInfo()
})
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.company-profile-page {
  padding: 40px 24px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  display: flex;
  justify-content: center;
}

.profile-container {
  width: 100%;
  max-width: 800px; /* 居中限宽，更显高级 */
}

/* ================= 卡片样式 ================= */
.profile-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  background: #ffffff;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.profile-card:hover {
  box-shadow: 0 10px 30px rgba(249, 115, 22, 0.06);
}

/* 修改卡片头部 */
:deep(.profile-card .el-card__header) {
  padding: 24px 32px;
  border-bottom: 1px solid #f1f5f9;
  background-color: #ffffff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  position: relative;
  padding-left: 16px;
  letter-spacing: 0.5px;
}

/* 活力橙装饰线 */
.header-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: #f97316;
  border-radius: 2px;
}

/* ================= 资料内容展示区 ================= */
.profile-content {
  padding: 32px;
}

/* Descriptions 样式覆盖 */
:deep(.custom-descriptions .el-descriptions__body) {
  background-color: transparent;
}

:deep(.custom-descriptions .el-descriptions__table) {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

:deep(.custom-descriptions .el-descriptions__cell) {
  border-color: #e2e8f0 !important;
}

/* 左侧 Label 区域 */
:deep(.custom-descriptions .el-descriptions__label) {
  background-color: #f8fafc !important;
  width: 140px;
  text-align: right;
  padding: 20px 24px !important;
  vertical-align: middle;
}

.desc-label {
  font-weight: 600;
  color: #4b5563;
  font-size: 15px;
  letter-spacing: 1px;
}

/* 右侧 Content 区域 */
:deep(.custom-descriptions .el-descriptions__content) {
  background-color: #ffffff !important;
  padding: 20px 24px !important;
}

.desc-value {
  font-size: 15px;
  color: #111827;
  font-weight: 500;
}

.highlight-text {
  color: #ea580c;
  font-weight: 700;
  font-size: 16px;
}

/* 公司简介文本域 */
.intro-content {
  line-height: 1.8;
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
  color: #4b5563;
  min-height: 100px;
  white-space: pre-wrap;
  font-size: 14px;
}

/* ================= 按钮高级态 ================= */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
  padding: 10px 24px;
  letter-spacing: 1px;
}

.theme-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4);
}

.theme-btn-outline {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #4b5563 !important;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
  padding: 10px 24px;
}

.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 弹窗与表单样式覆盖 ================= */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.custom-dialog .el-dialog__header) {
  padding: 24px 32px;
  border-bottom: 1px solid #f1f5f9;
  margin-right: 0;
}

:deep(.custom-dialog .el-dialog__title) {
  font-weight: 700;
  color: #111827;
  font-size: 18px;
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 30px 32px;
}

:deep(.custom-dialog .el-dialog__footer) {
  padding: 16px 32px;
  border-top: 1px solid #f1f5f9;
  background-color: #fafbfc;
}

.custom-form .el-form-item {
  margin-bottom: 24px;
}

.custom-form .el-form-item__label {
  font-weight: 500;
  color: #4b5563;
}

/* 输入框深空灰线框，聚焦变活力橙 */
:deep(.custom-input .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 8px 12px;
  font-size: 14px;
  color: #111827;
}

:deep(.custom-input .el-input__wrapper.is-disabled) {
  background-color: #f1f5f9;
  color: #94a3b8;
}

:deep(.custom-input .el-input__wrapper:hover:not(.is-disabled)),
:deep(.custom-textarea .el-textarea__inner:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-textarea .el-textarea__inner:focus) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .company-profile-page {
    padding: 16px;
  }
  
  :deep(.profile-card .el-card__header) {
    padding: 20px;
  }

  .profile-content {
    padding: 20px;
  }

  :deep(.custom-descriptions .el-descriptions__label) {
    width: 100px;
    padding: 16px !important;
  }

  :deep(.custom-descriptions .el-descriptions__content) {
    padding: 16px !important;
  }

  :deep(.custom-dialog) {
    width: 90% !important;
  }
}
</style>