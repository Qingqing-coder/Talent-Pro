<template>
  <div class="user-table-page">
    <el-card class="custom-card" :body-style="{ padding: '24px' }">
      <div class="control-panel">
        <div class="panel-left">
          <h2 class="panel-title">公司管理</h2>
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
        </div>
      </div>

      <el-table 
        :data="userList" 
        v-loading="loading" 
        class="custom-table"
        style="width: 100%; margin-bottom: 24px"
      >
        <el-table-column prop="username" label="公司账号" width="170" align="center" />
        <el-table-column prop="nickname" label="公司名称" width="170" align="center">
          <template #default="scope">
            <span class="company-name-highlight">{{ scope.row.nickname }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="password" label="密码" width="170" align="center" />
        <el-table-column prop="companyIntroduction" label="公司简介" min-width="400" show-overflow-tooltip align="center">
        </el-table-column>
        
        <el-table-column fixed="right" label="操作" width="160" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button class="theme-btn-outline-small" size="small" @click="openEditDialog(scope.row)">
                编辑
              </el-button>
              <el-button
                class="theme-btn-danger-small"
                size="small"
                @click="handleDelete(scope.row)"
                :loading="deleteLoadingId === scope.row.username"
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

    <el-dialog title="条件查询" v-model="conditionDialogVisible" width="500px" class="custom-dialog" destroy-on-close>
      <el-form :model="conditionForm" label-width="90px" class="horizontal-form">
        <el-form-item label="公司账号">
          <el-input v-model="conditionForm.username" clearable class="custom-input" placeholder="输入公司账号" />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="conditionForm.nickname" clearable class="custom-input" placeholder="输入公司名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="theme-btn-outline" @click="conditionDialogVisible = false">取消</el-button>
        <el-button class="theme-btn-primary" type="primary" @click="applyCondition">查询</el-button>
      </template>
    </el-dialog>

    <el-dialog
      :title="dialogMode === 'add' ? '添加公司' : '编辑公司'"
      v-model="dialogVisible"
      width="550px"
      class="custom-dialog"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px" :rules="rules" ref="formRef" class="horizontal-form">
        <el-form-item label="公司账号" prop="username">
          <el-input v-model="form.username" :disabled="dialogMode === 'edit'" class="custom-input" placeholder="输入账号" />
        </el-form-item>
        <el-form-item label="公司名称" prop="nickname">
          <el-input v-model="form.nickname" class="custom-input" placeholder="输入公司名称" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" class="custom-input" placeholder="输入密码" />
        </el-form-item>
        <el-form-item label="公司简介" prop="companyIntroduction">
          <el-input 
            v-model="form.companyIntroduction" 
            type="textarea" 
            :rows="4" 
            class="custom-textarea" 
            placeholder="输入公司简介" 
          />
        </el-form-item>
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
import { getComList, addCom, updateCom, deleteCom, getAllComs, getJobByN } from '@/api/company'
import { showCustomMessage } from '@/utils/message'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, RefreshRight } from '@element-plus/icons-vue' // 引入图标

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
const deleteLoadingId = ref<string | null>(null)
const formRef = ref<FormInstance>()
const postMode = ref(false) // false: GET分页所有，true: GET分页条件
const conditionDialogVisible = ref(false)
const conditionForm: Record<string, any> = reactive({
  username: '',
  nickname: '',
  password: '',
  companyIntroduction : '',
})
let lastCondition: Record<string, any> = {} // 记录上次查询条件

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  companyIntroduction : '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  eduBack: [{ required: true, message: '请输入学历', trigger: 'blur' }],
  salaryWishK: [{ required: true, message: '请输入期望薪资', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  hasExperience: [{ required: true, message: '请选择有无经验', trigger: 'change' }],
}

// GET分页获取所有用户
const fetchComsGet = async () => {
  loading.value = true
  try {
    const response = await getAllComs({ pageNum: pageNum.value, pageSize: pageSize.value })
    userList.value = response.data.list || []
    total.value = response.data.total || response.data.length || 0
    postMode.value = false
    showCustomMessage('GET分页获取所有用户成功', 'success')
  } catch (error: any) {
    showCustomMessage('GET分页获取所有用户失败: ' + error.message, 'error')
  } finally {
    loading.value = false
  }
}

// GET分页条件查询用户
const fetchComsPost = async (condition: Record<string, any> = {}) => {
  loading.value = true
  try {
    // 只传递有值的条件
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...condition,
    }
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === undefined || params[key] === null) {
        delete params[key]
      }
    })
    const response = await getComList(params)
    userList.value = response.data.list || []
    total.value = response.data.total || response.data.length || 0
    postMode.value = true
    showCustomMessage('GET分页条件查询成功', 'success')
  } catch (error: any) {
    showCustomMessage('GET分页条件查询失败: ' + error.message, 'error')
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
  // 记录本次条件
  lastCondition = { ...conditionForm }
  conditionDialogVisible.value = false
  fetchComsPost(lastCondition)
}

const switchToPost = () => {
  pageNum.value = 1
  inputPageNum.value = 1
  openConditionDialog()
}

const switchToGet = () => {
  pageNum.value = 1
  inputPageNum.value = 1
  // 清空条件
  Object.keys(conditionForm).forEach((key) => (conditionForm[key] = undefined))
  lastCondition = {}
  fetchComsGet()
}

const refreshCurrentMode = () => {
  if (postMode.value) {
    fetchComsPost(lastCondition)
  } else {
    fetchComsGet()
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
    username: '',
    nickname: '',
    password: '',
    name: '',
    phone: '',
    email: '',
    age: 18,
    sex: 1,
    eduBack: '',
    salaryWishK: 0,
    address: '',
    introduction: '',
    hasExperience: 0,
  })
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  dialogMode.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 在 handleSubmit 方法中，编辑时如果密码为空则不传递 password 字段
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    dialogLoading.value = true
    try {
      if (dialogMode.value === 'add') {
        await addCom(form)
        showCustomMessage('添加用户成功', 'success')
      } else {
        // 编辑时如果密码为空则不修改密码
        const updateData = { ...form } as Record<string, any>
        if (Object.prototype.hasOwnProperty.call(updateData, 'password') && !updateData.password) {
          delete updateData.password
        }
        await updateCom(updateData)
        showCustomMessage('编辑用户成功', 'success')
      }
      dialogVisible.value = false
      refreshCurrentMode()
    } catch (error: any) {
      showCustomMessage(
        (dialogMode.value === 'add' ? '添加' : '编辑') + '用户失败: ' + error.message,
        'error',
      )
    } finally {
      dialogLoading.value = false
    }
  })
}

const handleDelete = async (row: any) => {
  if (!row.username) return
  if (!confirm(`确定要删除用户 ${row.username} 吗？`)) return
  deleteLoadingId.value = row.username
  try {
    await deleteCom(row.username)
    showCustomMessage('删除用户成功', 'success')
    refreshCurrentMode()
  } catch (error: any) {
    showCustomMessage('删除用户失败: ' + error.message, 'error')
  } finally {
    deleteLoadingId.value = null
  }
}

onMounted(() => {
  fetchComsGet()
})
</script>

<style scoped>
/* ================= 全局底色与布局 ================= */
.user-table-page {
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
.company-name-highlight {
  font-weight: 600;
  color: #111827;
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

/* 输入框深空灰线框，聚焦变活力橙 */
:deep(.custom-input .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner),
:deep(.custom-input-number .el-input__wrapper) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 4px 12px;
}

:deep(.custom-input .el-input__wrapper:hover),
:deep(.custom-textarea .el-textarea__inner:hover),
:deep(.custom-input-number .el-input__wrapper:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-textarea .el-textarea__inner:focus),
:deep(.custom-input-number.is-focus .el-input__wrapper) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

:deep(.custom-input-number) {
  width: 100px;
}
</style>