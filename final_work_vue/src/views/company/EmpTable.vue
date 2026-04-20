<template>
  <div class="emp-table-page">
    <el-card class="custom-card" :body-style="{ padding: '24px' }">
      <div class="control-panel">
        <div class="panel-left">
          <h2 class="panel-title">我的岗位管理</h2>
          <el-tag v-if="postMode" type="warning" class="custom-tag-warning">查询结果模式</el-tag>
        </div>
        <div class="panel-right">
          <el-button class="theme-btn-outline" @click="refreshCurrentMode" :loading="loading">
            <el-icon class="btn-icon"><RefreshRight /></el-icon> 刷新
          </el-button>
          <el-button v-if="!postMode" class="theme-btn-outline" @click="openConditionDialog">
            <el-icon class="btn-icon"><Search /></el-icon> 条件查询
          </el-button>
          <el-button v-if="postMode" class="theme-btn-outline" @click="switchToGet">
            <el-icon class="btn-icon"><Refresh /></el-icon> 重置为全部
          </el-button>
          <el-button class="theme-btn-primary" @click="openAddDialog">
            <el-icon class="btn-icon"><Plus /></el-icon> 添加岗位
          </el-button>
        </div>
      </div>

      <el-table 
        :data="userList" 
        v-loading="loading" 
        class="custom-table"
        style="width: 100%; margin-bottom: 24px"
      >
        <el-table-column prop="id" label="职位id" width="80" align="center" />
        <el-table-column prop="title" label="职位名称" min-width="140" align="center">
          <template #default="scope">
            <span class="job-title-highlight">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="salary" label="薪水" width="120" align="center">
          <template #default="scope">
            <span class="salary-highlight">{{ scope.row.salary }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="公司名称" min-width="160" align="center" />
        <el-table-column prop="companySize" label="公司规模" width="100" align="center">
          <template #default="scope">
            <el-tag size="small" type="info" class="custom-tag">{{ scope.row.companySize }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fullLocation" label="公司所在地" width="120" align="center" />
        <el-table-column prop="experience" label="经验要求" width="100" align="center" />
        <el-table-column prop="education" label="学历要求" width="110" align="center" />
        <el-table-column prop="type" label="职位类型" width="100" align="center" />
        <el-table-column prop="benefits" label="职位待遇福利" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="jobType" label="岗位类型" width="110" align="center" />
        
        <el-table-column fixed="right" label="操作" width="160" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button class="theme-btn-outline-small" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
              <el-button
                class="theme-btn-danger-small"
                size="small"
                @click="handleDelete(scope.row)"
                :loading="deleteLoadingId === scope.row.id"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer-bar">
        <el-pagination
          class="custom-pagination"
          background
          layout="prev, pager, next, jumper, ->, total"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
        <div class="page-size-controller">
          <span class="page-size-label">每页条数：</span>
          <el-input-number
            v-model="inputPageSize"
            :min="1"
            :max="100"
            size="small"
            class="custom-input-number"
            :step="1"
            placeholder="条数"
          />
          <el-button size="small" class="theme-btn-outline-small" @click="applyPageInput">跳转</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog title="条件查询" v-model="conditionDialogVisible" width="650px" class="custom-dialog" destroy-on-close>
      <el-form :model="conditionForm" label-width="110px" class="horizontal-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="职位名称">
              <el-input v-model="conditionForm.title" clearable class="custom-input" placeholder="输入职位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪水">
              <el-input v-model="conditionForm.salary" clearable class="custom-input" placeholder="如 15K" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="公司所在地">
              <el-input v-model="conditionForm.fullLocation" clearable class="custom-input" placeholder="输入城市或地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经验要求">
              <el-select v-model="conditionForm.experience" clearable placeholder="请选择" class="custom-select">
                <el-option label="1年以下" value="1年以下" />
                <el-option label="1年以上" value="1年以上" />
                <el-option label="2年以上" value="2年以上" />
                <el-option label="3年以上" value="3年以上" />
                <el-option label="4年以上" value="4年以上" />
                <el-option label="5年以上" value="5年以上" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="学历要求">
              <el-select v-model="conditionForm.education" clearable placeholder="请选择" class="custom-select">
                <el-option label="初中及以上" value="初中及以上" />
                <el-option label="高中及以上" value="高中及以上" />
                <el-option label="大专及以上" value="大专及以上" />
                <el-option label="本科及以上" value="本科及以上" />
                <el-option label="硕士及以上" value="硕士及以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位类型">
              <el-select v-model="conditionForm.type" clearable placeholder="请选择" class="custom-select">
                <el-option label="兼职" value="兼职" />
                <el-option label="全职" value="全职" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="职位待遇福利">
              <el-input v-model="conditionForm.benefits" clearable class="custom-input" placeholder="输入福利关键词" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位类型">
              <el-select v-model="conditionForm.jobType" clearable placeholder="请选择" class="custom-select">
                <el-option label="开发" value="开发" />
                <el-option label="技术" value="技术" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="医疗" value="医疗" />
                <el-option label="电商" value="电商" />
                <el-option label="物流" value="物流" />
                <el-option label="销售" value="销售" />
                <el-option label="能源" value="能源" />
                <el-option label="游戏" value="游戏" />
                <el-option label="市场" value="市场" />
                <el-option label="房地产" value="房地产" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button class="theme-btn-outline" @click="conditionDialogVisible = false">取消</el-button>
        <el-button class="theme-btn-primary" type="primary" @click="applyCondition">查询</el-button>
      </template>
    </el-dialog>

    <el-dialog
      :title="dialogMode === 'add' ? '添加岗位' : '编辑岗位'"
      v-model="dialogVisible"
      width="700px"
      class="custom-dialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="horizontal-form">
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="职位名称" prop="title">
              <el-input v-model="form.title" clearable class="custom-input" placeholder="请输入职位名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="薪水" required>
              <div class="salary-range-inputs">
                <el-form-item prop="salaryMin" class="inline-form-item">
                  <el-input-number v-model="form.salaryMin" :min="1" class="custom-input-number" placeholder="最低" /> 
                </el-form-item>
                <span class="salary-separator">K -</span>
                <el-form-item prop="salaryMax" class="inline-form-item">
                  <el-input-number v-model="form.salaryMax" :min="1" class="custom-input-number" placeholder="最高" /> 
                </el-form-item>
                <span class="salary-separator">K</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="form.companyName" clearable class="custom-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司规模" prop="companySize">
              <el-select v-model="form.companySize" clearable placeholder="请选择" class="custom-select">
                <el-option label="大型" value="大型" />
                <el-option label="中型" value="中型" />
                <el-option label="小型" value="小型" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="公司所在地" prop="fullLocation">
              <el-input v-model="form.fullLocation" clearable class="custom-input" placeholder="详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经验要求" prop="experience">
              <el-select v-model="form.experience" clearable placeholder="请选择" class="custom-select">
                <el-option label="1年以下" value="1年以下" />
                <el-option label="1年以上" value="1年以上" />
                <el-option label="2年以上" value="2年以上" />
                <el-option label="3年以上" value="3年以上" />
                <el-option label="4年以上" value="4年以上" />
                <el-option label="5年以上" value="5年以上" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="学历要求" prop="education">
              <el-select v-model="form.education" clearable placeholder="请选择" class="custom-select">
                <el-option label="初中及以上" value="初中及以上" />
                <el-option label="高中及以上" value="高中及以上" />
                <el-option label="大专及以上" value="大专及以上" />
                <el-option label="本科及以上" value="本科及以上" />
                <el-option label="硕士及以上" value="硕士及以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位类型" prop="type">
              <el-select v-model="form.type" clearable placeholder="请选择" class="custom-select">
                <el-option label="兼职" value="兼职" />
                <el-option label="全职" value="全职" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="岗位类型" prop="jobType">
              <el-select v-model="form.jobType" clearable placeholder="请选择" class="custom-select">
                <el-option label="开发" value="开发" />
                <el-option label="技术" value="技术" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="医疗" value="医疗" />
                <el-option label="电商" value="电商" />
                <el-option label="物流" value="物流" />
                <el-option label="销售" value="销售" />
                <el-option label="能源" value="能源" />
                <el-option label="游戏" value="游戏" />
                <el-option label="市场" value="市场" />
                <el-option label="房地产" value="房地产" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="待遇福利" prop="benefits">
              <el-input v-model="form.benefits" clearable class="custom-input" placeholder="如：五险一金,双休" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button class="theme-btn-outline" @click="dialogVisible = false">取消</el-button>
        <el-button class="theme-btn-primary" type="primary" @click="handleSubmit" :loading="dialogLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { addEmp, updateEmp, deleteEmp } from '@/api/emp'
import { getJobByN, getComList } from '@/api/company'
import { searchJobs } from '@/api/job' 
import { showCustomMessage } from '@/utils/message'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, RefreshRight, Plus } from '@element-plus/icons-vue' 

const userList = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const inputPageNum = ref(1)
const inputPageSize = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogLoading = ref(false)
const deleteLoadingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const postMode = ref(false)
const conditionDialogVisible = ref(false)
const currentCompany = localStorage.getItem('companyUsername') || ''

// 公司资料信息
const companyProfile = reactive({
  companyName: '',
  companySize: '',
  fullLocation: '',
  jobType: '',
})

const conditionForm: Record<string, any> = reactive({
  title: '',
  salary: '',
  companyName: '',
  companySize: '',
  fullLocation: '',
  experience: '',
  education: '',
  type: '',
  benefits: '',
  jobType: '',
})
let lastCondition: Record<string, any> = {}

const form = reactive({
  id: 0,
  title: '',
  salary: '',
  salaryMin: '',
  salaryMax: '',
  companyName: '',
  companySize: '',
  fullLocation: '',
  experience: '',
  education: '',
  type: '',
  benefits: '',
  jobType: '',
  createTime: '',
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  salaryMin: [
    { required: true, message: '请输入最低薪资', trigger: 'blur' }
  ],
  salaryMax: [
    { required: true, message: '请输入最高薪资', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (form.salaryMin && value && Number(value) < Number(form.salaryMin)) {
          callback(new Error('最高薪资不能小于最低薪资'))
        } else {
          callback()
        }
      }, trigger: 'blur'
    }
  ],
  companySize: [{ required: true, message: '请选择公司规模', trigger: 'blur' }],
  fullLocation: [{ required: true, message: '请输入公司所在地', trigger: 'blur' }],
  experience: [{ required: true, message: '请选择经验要求', trigger: 'blur' }],
  education: [{ required: true, message: '请选择学历要求', trigger: 'blur' }],
  type: [{ required: true, message: '请选择职位类型', trigger: 'blur' }],
  benefits: [{ required: true, message: '请输入职位待遇福利', trigger: 'blur' }],
  jobType: [{ required: true, message: '请选择岗位类型', trigger: 'blur' }],
}

// 获取公司资料信息
const fetchCompanyProfile = async () => {
  try {
    const res = await getComList({ username: currentCompany, pageNum: 1, pageSize: 10 })
    const info = res.data.list && res.data.list[0] ? res.data.list[0] : {}
    companyProfile.companyName = info.nickname || ''
    companyProfile.companySize = info.companySize || ''
    companyProfile.fullLocation = info.fullLocation || ''
    companyProfile.jobType = info.jobType || ''
  } catch (e: any) {
    companyProfile.companyName = ''
    companyProfile.companySize = ''
    companyProfile.fullLocation = ''
    companyProfile.jobType = ''
  }
}

// 获取当前公司所有岗位
const fetchEmpsGet = async () => {
  loading.value = true
  try {
    const response = await getJobByN({ username: currentCompany })
    userList.value = response.data.list || response.data || []
    total.value = response.data.total || userList.value.length || 0
    postMode.value = false
  } catch (error: any) {
  } finally {
    loading.value = false
  }
}

// 条件查询也只查本公司
const fetchEmpsPost = async (condition: Record<string, any> = {}) => {
  loading.value = true
  try {
    // 组装参数，自动带上公司名
    const params: Record<string, any> = {
      ...condition,
      companyName: companyProfile.companyName || currentCompany,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    }
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === undefined || params[key] === null) {
        delete params[key]
      }
    })
    // 使用 searchJobs 进行条件查询
    const response = await searchJobs(params)
    userList.value = response.data?.data?.list || response.data?.list || []
    total.value = response.data?.data?.total || response.data?.total || userList.value.length || 0
    postMode.value = true
  } catch (error: any) {
  } finally {
    loading.value = false
  }
}

const openConditionDialog = () => {
  conditionDialogVisible.value = true
}

const applyCondition = () => {
  pageNum.value = 1
  inputPageNum.value = 1
  lastCondition = { ...conditionForm }
  conditionDialogVisible.value = false
  fetchEmpsPost(lastCondition)
}

const switchToGet = () => {
  pageNum.value = 1
  inputPageNum.value = 1
  Object.keys(conditionForm).forEach((key) => (conditionForm[key] = ''))
  lastCondition = {}
  fetchEmpsGet()
}

const refreshCurrentMode = () => {
  if (postMode.value) {
    fetchEmpsPost(lastCondition)
  } else {
    fetchEmpsGet()
  }
}

const handlePageChange = (val: number) => {
  pageNum.value = val
  inputPageNum.value = val
  refreshCurrentMode()
}

const applyPageInput = () => {
  if (inputPageNum.value < 1) inputPageNum.value = 1
  if (inputPageSize.value < 1) inputPageSize.value = 1
  pageNum.value = inputPageNum.value
  pageSize.value = inputPageSize.value
  refreshCurrentMode()
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  Object.assign(form, {
    id: 0,
    title: '',
    salary: '',
    salaryMin: '',
    salaryMax: '',
    companyName: companyProfile.companyName || currentCompany,
    companySize: companyProfile.companySize,
    fullLocation: companyProfile.fullLocation,
    experience: '',
    education: '',
    type: '',
    benefits: '',
    jobType: companyProfile.jobType,
    createTime: '',
  })
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  dialogMode.value = 'edit'
  Object.assign(form, row)
  // 拆分薪水
  if (row.salary && row.salary.includes('K-')) {
    const [min, max] = row.salary.replace(/K/g, '').split('-')
    form.salaryMin = min
    form.salaryMax = max
  } else {
    form.salaryMin = ''
    form.salaryMax = ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    dialogLoading.value = true
    try {
      if (dialogMode.value === 'add') {
        form.salary = form.salaryMin && form.salaryMax ? `${form.salaryMin}K-${form.salaryMax}K` : ''
        // 组装完整参数
        const jobData = {
          id: 0,
          title: form.title,
          salary: form.salary,
          companyName: form.companyName,
          companySize: form.companySize,
          fullLocation: form.fullLocation,
          experience: form.experience,
          education: form.education,
          type: form.type,
          benefits: form.benefits,
          jobType: form.jobType,
          createTime: '',
        }
        await addEmp(jobData)
        showCustomMessage('添加岗位成功', 'success')
      } else {
        form.salary = form.salaryMin && form.salaryMax ? `${form.salaryMin}K-${form.salaryMax}K` : form.salary
        await updateEmp(form)
        showCustomMessage('编辑岗位成功', 'success')
      }
      dialogVisible.value = false
      refreshCurrentMode()
    } catch (error: any) {
      showCustomMessage(
        (dialogMode.value === 'add' ? '添加' : '编辑') + '岗位失败: ' + error.message,
        'error',
      )
    } finally {
      dialogLoading.value = false
    }
  })
}

const handleDelete = async (row: any) => {
  if (!row.id) return
  if (!confirm(`确定要删除职位 ${row.title} 吗？`)) return
  deleteLoadingId.value = row.id
  try {
    await deleteEmp(row.id)
    showCustomMessage('删除岗位成功', 'success')
    refreshCurrentMode()
  } catch (error: any) {
    showCustomMessage('删除岗位失败: ' + error.message, 'error')
  } finally {
    deleteLoadingId.value = null
  }
}

onMounted(() => {
  fetchCompanyProfile()
  fetchEmpsGet()
})
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.emp-table-page {
  padding: 24px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ================= 独立顶部控制区 ================= */
.control-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
}

.panel-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.panel-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  position: relative;
  padding-left: 14px;
}

/* 活力橙装饰线 */
.panel-title::before {
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

.custom-tag-warning {
  background-color: #fffaf5;
  border-color: #ffedd5;
  color: #ea580c;
}

.panel-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-icon {
  margin-right: 4px;
  font-size: 14px;
}

/* ================= 主体卡片与表格 ================= */
.custom-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  background: #ffffff;
  overflow: hidden;
}

/* 取消原生卡片 header */
:deep(.custom-card .el-card__header) {
  display: none;
}

.custom-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

/* 表头背景与文字颜色 */
:deep(.custom-table th.el-table__cell) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
  padding: 14px 0;
  border-bottom: 1px solid #e2e8f0;
}

/* 单元格边距与底色过渡 */
:deep(.custom-table td.el-table__cell) {
  padding: 14px 0;
  color: #111827;
  border-bottom: 1px solid #f1f5f9;
  transition: background-color 0.3s;
}

:deep(.custom-table .el-table__row:hover > td.el-table__cell) {
  background-color: #fffaf5 !important; /* 悬浮时极淡的橙色 */
}

/* 表格内文字高亮 */
.job-title-highlight {
  font-weight: 600;
  color: #111827;
}

.salary-highlight {
  color: #ea580c;
  font-weight: 700;
  font-size: 14px;
}

.custom-tag {
  background-color: #f1f5f9;
  border-color: #e2e8f0;
  color: #475569;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

/* ================= 按钮样式统一 ================= */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
  padding: 8px 20px;
}
.theme-btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4);
}

.theme-btn-outline {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #4b5563 !important;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
  padding: 8px 16px;
}
.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

.theme-btn-outline-small {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #4b5563 !important;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s;
}
.theme-btn-outline-small:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

.theme-btn-danger-small {
  background: #fff5f5 !important;
  border: 1px solid #fecdd3 !important;
  color: #ef4444 !important;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s;
}
.theme-btn-danger-small:hover {
  background: #ef4444 !important;
  color: #ffffff !important;
  border-color: #ef4444 !important;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

/* ================= 底部底栏与分页 ================= */
.table-footer-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
}

.page-size-controller {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-size-label {
  font-size: 14px;
  color: #64748b;
}

:deep(.custom-pagination.el-pagination.is-background .el-pager li.is-active) {
  background-color: #f97316 !important;
  color: #ffffff !important;
  font-weight: bold;
}
:deep(.custom-pagination.el-pagination.is-background .el-pager li:hover) {
  color: #ea580c !important;
}

/* ================= 弹窗与表单样式 ================= */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
}
:deep(.custom-dialog .el-dialog__header) {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  margin-right: 0;
}
:deep(.custom-dialog .el-dialog__title) {
  font-weight: 700;
  color: #111827;
  font-size: 18px;
}
:deep(.custom-dialog .el-dialog__body) {
  padding: 30px 24px;
}
:deep(.custom-dialog .el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  background-color: #fafbfc;
}

.horizontal-form .el-form-item {
  margin-bottom: 20px;
}

.horizontal-form .el-form-item__label {
  width: 90px !important;
  min-width: 90px !important;
  text-align: right;
  font-weight: 500;
  color: #4b5563;
}

/* 针对薪资区间的布局优化 */
.salary-range-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
}
.inline-form-item {
  margin-bottom: 0 !important;
}
.salary-separator {
  color: #64748b;
  font-weight: 500;
}

/* 输入框深空灰线框，聚焦变活力橙 */
:deep(.custom-input .el-input__wrapper),
:deep(.custom-select .el-input__wrapper),
:deep(.custom-input-number .el-input__wrapper) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 4px 12px;
}

:deep(.custom-input .el-input__wrapper:hover),
:deep(.custom-select .el-input__wrapper:hover),
:deep(.custom-input-number .el-input__wrapper:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-select .el-input.is-focus .el-input__wrapper),
:deep(.custom-input-number.is-focus .el-input__wrapper) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

:deep(.custom-input-number) {
  width: 100px;
}
</style>