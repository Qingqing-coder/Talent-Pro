<template>
  <div class="user-table-page">
    <el-card class="custom-card" :body-style="{ padding: '24px' }">
      <div class="control-panel">
        <div class="panel-left">
          <h2 class="panel-title">用户管理</h2>
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
        <el-table-column prop="username" label="用户名" width="130" align="center">
          <template #default="scope">
            <span class="highlight-text">{{ scope.row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="130" align="center" />
        <el-table-column prop="name" label="姓名" width="100" align="center" />
        <el-table-column prop="password" label="密码" width="120" align="center" />
        <el-table-column prop="phone" label="电话" width="130" align="center" />
        <el-table-column prop="email" label="邮箱" width="180" align="center" show-overflow-tooltip />
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="sex" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sex === 1 ? '' : 'danger'" size="small" class="custom-tag">
              {{ scope.row.sex === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="eduBack" label="学历" width="110" align="center" />
        <el-table-column prop="major" label="专业" width="130" align="center" show-overflow-tooltip />
        <el-table-column prop="salaryWishK" label="期望薪资" width="110" align="center">
          <template #default="scope">
            <span class="salary-highlight">{{ scope.row.salaryWishK }}K</span>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" width="180" align="center" show-overflow-tooltip />
        <el-table-column prop="hasExperience" label="有无经验" width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.hasExperience === 1 ? '有' : '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="introduction" label="简介" min-width="160" align="center" show-overflow-tooltip />
        
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

    <el-dialog title="条件查询" v-model="conditionDialogVisible" width="650px" class="custom-dialog" destroy-on-close>
      <el-form :model="conditionForm" label-width="110px" class="horizontal-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="conditionForm.username" clearable class="custom-input" placeholder="输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称">
              <el-input v-model="conditionForm.nickname" clearable class="custom-input" placeholder="输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="conditionForm.name" clearable class="custom-input" placeholder="输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大年龄上限">
              <el-input-number v-model="conditionForm.age" :min="0" :max="150" clearable class="custom-input-number" style="width: 100%" placeholder="输入年龄" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="学历">
              <el-select v-model="conditionForm.eduBack" clearable placeholder="请选择" class="custom-select">
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业">
              <el-select v-model="conditionForm.major" clearable placeholder="请选择" class="custom-select">
                <el-option label="计算机科学" value="计算机科学" />
                <el-option label="软件工程" value="软件工程" />
                <el-option label="电子工程" value="电子工程" />
                <el-option label="市场营销" value="市场营销" />
                <el-option label="人力资源" value="人力资源" />
                <el-option label="会计学" value="会计学" />
                <el-option label="机械设计" value="机械设计" />
                <el-option label="电子信息工程" value="电子信息工程" />
                <el-option label="通信工程" value="通信工程" />
                <el-option label="计算机应用技术" value="计算机应用技术" />
                <el-option label="自动化" value="自动化" />
                <el-option label="信息与计算科学" value="信息与计算科学" />
                <el-option label="数字媒体技术" value="数字媒体技术" />
                <el-option label="网络工程" value="网络工程" />
                <el-option label="计算机技术" value="计算机技术" />
                <el-option label="电子商务" value="电子商务" />
                <el-option label="机械工程" value="机械工程" />
                <el-option label="电气工程及其自动化" value="电气工程及其自动化" />
                <el-option label="通信与信息系统" value="通信与信息系统" />
                <el-option label="电子科学与技术" value="电子科学与技术" />
                <el-option label="人力资源管理" value="人力资源管理" />
                <el-option label="不限专业" value="不限专业" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="有无经验">
              <el-select v-model="conditionForm.hasExperience" clearable placeholder="请选择" class="custom-select">
                <el-option label="有" :value="1" />
                <el-option label="无" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="conditionForm.sex" clearable placeholder="请选择" class="custom-select">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
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
      :title="dialogMode === 'add' ? '添加用户' : '编辑用户'"
      v-model="dialogVisible"
      width="700px"
      class="custom-dialog"
      destroy-on-close
    >
      <el-form :model="form" label-width="110px" :rules="rules" ref="formRef" class="horizontal-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :disabled="dialogMode === 'edit'" class="custom-input" placeholder="输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" class="custom-input" placeholder="输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" autocomplete="new-password" class="custom-input" placeholder="输入密码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" class="custom-input" placeholder="输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" class="custom-input" placeholder="输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" class="custom-input" placeholder="输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="0" class="custom-input-number" style="width: 100%" placeholder="年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="form.sex" class="custom-select">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="学历" prop="eduBack">
              <el-select v-model="form.eduBack" class="custom-select">
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-select v-model="form.major" clearable placeholder="请选择" class="custom-select">
                <el-option label="计算机科学" value="计算机科学" />
                <el-option label="软件工程" value="软件工程" />
                <el-option label="电子工程" value="电子工程" />
                <el-option label="市场营销" value="市场营销" />
                <el-option label="人力资源" value="人力资源" />
                <el-option label="会计学" value="会计学" />
                <el-option label="机械设计" value="机械设计" />
                <el-option label="电子信息工程" value="电子信息工程" />
                <el-option label="通信工程" value="通信工程" />
                <el-option label="计算机应用技术" value="计算机应用技术" />
                <el-option label="自动化" value="自动化" />
                <el-option label="信息与计算科学" value="信息与计算科学" />
                <el-option label="数字媒体技术" value="数字媒体技术" />
                <el-option label="网络工程" value="网络工程" />
                <el-option label="计算机技术" value="计算机技术" />
                <el-option label="电子商务" value="电子商务" />
                <el-option label="机械工程" value="机械工程" />
                <el-option label="电气工程及其自动化" value="电气工程及其自动化" />
                <el-option label="通信与信息系统" value="通信与信息系统" />
                <el-option label="电子科学与技术" value="电子科学与技术" />
                <el-option label="人力资源管理" value="人力资源管理" />
                <el-option label="不限专业" value="不限专业" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="期望薪资" prop="salaryWishK">
              <el-input-number v-model="form.salaryWishK" :min="0" class="custom-input-number" style="width: 100%" placeholder="单位:K" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" class="custom-input" placeholder="输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="有无经验" prop="hasExperience">
              <el-select v-model="form.hasExperience" class="custom-select">
                <el-option label="有" :value="1" />
                <el-option label="无" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="简介" prop="introduction">
              <el-input v-model="form.introduction" type="textarea" :rows="3" class="custom-textarea" placeholder="个人简介" />
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
import { getUserList, addUser, updateUser, deleteUser, getAllUsers } from '@/api/user'
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
  name: '',
  age: undefined,
  hasExperience: undefined,
  sex: undefined,
  major: '',
})
let lastCondition: Record<string, any> = {} // 记录上次查询条件

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  age: 18,
  sex: 1,
  eduBack: '',
  major: '',
  salaryWishK: 0,
  address: '',
  introduction: '',
  hasExperience: 0,
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
const fetchUsersGet = async () => {
  loading.value = true
  try {
    const response = await getAllUsers({ pageNum: pageNum.value, pageSize: pageSize.value })
    userList.value = response.data.list || []
    total.value = response.data.total || response.data.length || 0
    postMode.value = false
    // showCustomMessage('GET分页获取所有用户成功', 'success')
  } catch (error: any) {
    // showCustomMessage('GET分页获取所有用户失败: ' + error.message, 'error')
  } finally {
    loading.value = false
  }
}

// GET分页条件查询用户
const fetchUsersPost = async (condition: Record<string, any> = {}) => {
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
    const response = await getUserList(params)
    userList.value = response.data.list || []
    total.value = response.data.total || response.data.length || 0
    postMode.value = true
    // showCustomMessage('GET分页条件查询成功', 'success')
  } catch (error: any) {
    // showCustomMessage('GET分页条件查询失败: ' + error.message, 'error')
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
  fetchUsersPost(lastCondition)
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
  fetchUsersGet()
}

const refreshCurrentMode = () => {
  if (postMode.value) {
    fetchUsersPost(lastCondition)
  } else {
    fetchUsersGet()
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
        await addUser(form)
        // showCustomMessage('添加用户成功', 'success')
      } else {
        // 编辑时如果密码为空则不修改密码
        const updateData = { ...form } as Record<string, any>
        if (Object.prototype.hasOwnProperty.call(updateData, 'password') && !updateData.password) {
          delete updateData.password
        }
        await updateUser(updateData)
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
    await deleteUser(row.username)
    // showCustomMessage('删除用户成功', 'success')
    refreshCurrentMode()
  } catch (error: any) {
    // showCustomMessage('删除用户失败: ' + error.message, 'error')
  } finally {
    deleteLoadingId.value = null
  }
}

onMounted(() => {
  fetchUsersGet()
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
.highlight-text {
  font-weight: 600;
  color: #111827;
}

.salary-highlight {
  color: #ea580c;
  font-weight: 700;
  font-size: 14px;
}

.custom-tag {
  border-radius: 6px;
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
:deep(.custom-select .el-input__wrapper),
:deep(.custom-input-number .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 4px 12px;
}

:deep(.custom-textarea .el-textarea__inner) {
  padding: 8px 12px;
}

:deep(.custom-input .el-input__wrapper:hover),
:deep(.custom-select .el-input__wrapper:hover),
:deep(.custom-input-number .el-input__wrapper:hover),
:deep(.custom-textarea .el-textarea__inner:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-select .el-input.is-focus .el-input__wrapper),
:deep(.custom-input-number.is-focus .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner:focus) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

:deep(.custom-input-number) {
  width: 100px;
}
</style>