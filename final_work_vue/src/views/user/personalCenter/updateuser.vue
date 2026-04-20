<template>
  <div class="edit-profile-container">
    <el-card class="edit-profile-box" :body-style="{ padding: '0px' }">
      <div class="page-nav">
        <el-button link class="back-btn" @click="handleBack">
          <el-icon><ArrowLeft /></el-icon> 返回个人中心
        </el-button>
      </div>

      <div class="edit-header">
        <h2>个人信息编辑</h2>
        <p class="edit-description">完善您的个人信息，提高求职成功率</p>
        <div class="profile-completion">
          <span>信息完整度</span>
          <el-progress :percentage="calculateCompletion" :format="format" color="#f97316" />
        </div>
      </div>

      <el-form
        ref="empFormRef"
        :model="empForm"
        :rules="empRules"
        label-position="top"
        class="edit-form"
        @change="handleFormChange"
      >
        <div class="form-section">
          <div class="form-group">
            <div class="group-header">
              <h4 class="form-section-title">基本信息</h4>
              <el-tag size="small" type="warning" class="custom-tag-warning">必填</el-tag>
            </div>

            <div class="avatar-section">
              <el-avatar :size="100" :src="avatarUrl" class="user-avatar">
                <img v-if="!avatarUrl" :src="avatarUrl" alt="用户头像" />
              </el-avatar>
              <div class="avatar-upload">
                <el-button type="primary" text class="update-avatar-btn" @click="handleAvatarUpload">更换头像</el-button>
<input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
              </div>
              <div class="upload-tip">支持 jpg、png 格式，大小不超过 2MB</div>
            </div>
            <div class="form-row">
              <el-form-item label="用户名" prop="username" class="form-field">
                <el-input v-model="empForm.username" disabled class="custom-input" />
                <div class="form-hint">用户名不可修改</div>
              </el-form-item>
              <el-form-item label="昵称" prop="nickname" class="form-field">
                <el-input v-model="empForm.nickname" maxlength="20" show-word-limit class="custom-input" />
                <div class="form-hint">昵称将显示在您的个人主页</div>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="真实姓名" prop="name" class="form-field">
                <el-input v-model="empForm.name" class="custom-input">
                  <template #append>
                    <el-tooltip content="仅在与企业私聊时可见" placement="top">
                      <el-icon><InfoFilled /></el-icon>
                    </el-tooltip>
                  </template>
                </el-input>
                <div class="form-hint">请填写真实姓名，方便企业联系</div>
              </el-form-item>
              <el-form-item label="性别" prop="sex" class="form-field">
                <el-radio-group v-model="empForm.sex" class="custom-radio">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="0">女</el-radio>
                  <el-radio :label="2">保密</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="年龄" prop="age" class="form-field">
                <el-input-number v-model="empForm.age" :min="18" :max="60" class="custom-input-number" />
                <div class="form-hint">年龄范围：18-60岁</div>
              </el-form-item>
              <el-form-item label="手机号码" prop="phone" class="form-field">
                <el-input v-model="empForm.phone" maxlength="11" class="custom-input" />
                <div class="form-hint">用于接收面试通知和重要消息</div>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="电子邮箱" prop="email" class="form-field">
                <el-input v-model="empForm.email" type="email" class="custom-input" />
                <div class="form-hint">用于接收简历投递状态更新</div>
              </el-form-item>
              <el-form-item label="居住地址" prop="address" class="form-field">
                <el-input v-model="empForm.address" class="custom-input" />
                <div class="form-hint">请填写详细的居住地址</div>
              </el-form-item>
            </div>
          </div>

          <div class="form-group">
            <div class="group-header">
              <h4 class="form-section-title">求职相关信息</h4>
              <el-tag size="small" type="success" class="custom-tag-success">提升亮点</el-tag>
            </div>

            <div class="form-row">
              <el-form-item label="学历" prop="eduBack" class="form-field">
                <el-select v-model="empForm.eduBack" placeholder="请选择" class="custom-select">
                  <el-option label="初中" value="middle" />
                  <el-option label="高中" value="高中" />
                  <el-option label="专科" value="专科" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
                <div class="form-hint">选择您的最高学历</div>
              </el-form-item>
              <el-form-item label="工作经验" prop="hasExperience" class="form-field">
                <el-radio-group v-model="empForm.hasExperience" class="custom-radio">
                  <el-radio :label="1">有工作经验</el-radio>
                  <el-radio :label="0">无工作经验</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="期望薪资" prop="salaryWishK" class="form-field">
                <div class="salary-range">
                  <el-input-number v-model="empForm.salaryWishK" :min="0" :max="100" class="custom-input-number" />
                  <span class="salary-unit">K/月</span>
                </div>
                <div class="form-hint">请根据行业水平合理填写期望薪资</div>
              </el-form-item>
              <el-form-item label="专业" prop="major" class="form-field">
                <el-select v-model="empForm.major" placeholder="请选择专业" @change="onMajorChange" class="custom-select">
                  <el-option v-for="item in majorOptions" :key="item" :label="item" :value="item" />
                </el-select>
                <el-input
                  v-if="showOtherMajor"
                  v-model="otherMajor"
                  placeholder="请输入专业"
                  style="margin-top: 8px"
                  class="custom-input"
                  @input="empForm.major = otherMajor"
                />
              </el-form-item>
            </div>

            <el-form-item label="个人介绍" prop="introduction" class="form-field">
              <el-input
                v-model="empForm.introduction"
                type="textarea"
                :rows="4"
                :maxlength="1000"
                show-word-limit
                class="custom-textarea"
                placeholder="请简要介绍您的专业技能、工作经历和求职意向"
              />
              <div class="form-hint">
                <el-icon><InfoFilled /></el-icon>
                建议包含以下内容：
                <ul class="hint-list">
                  <li>您的专业技能和技术栈</li>
                  <li>过往工作经历和主要职责</li>
                  <li>获得的证书和荣誉</li>
                  <li>期望的工作方向和职位</li>
                </ul>
              </div>
            </el-form-item>
          </div>
        </div>

        <div class="form-actions">
          <el-button @click="handleCancel" class="cancel-button">取消修改</el-button>
          <el-button type="primary" class="submit-button" :loading="isSubmitting" @click="handleSave">
            保存修改
          </el-button>
        </div>

        <div class="form-tips">
          <el-alert
            title="温馨提示"
            type="info"
            :closable="false"
            show-icon
            class="custom-alert"
          >
            <p>1. 请确保填写的信息真实有效，虚假信息可能影响您的求职</p>
            <p>2. 个人信息仅用于求职，我们会严格保护您的隐私</p>
          </el-alert>
        </div>
      </el-form>
    </el-card>
  </div>
  <el-dialog v-model="showValidateDialog" title="提示" width="350px" center class="custom-dialog">
    <span>{{ validateMsg }}</span>
    <template #footer>
      <el-button type="primary" class="theme-btn-primary" @click="showValidateDialog = false">确定</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="showCancelDialog" title="提示" width="350px" center class="custom-dialog">
    <span>确定要取消修改并返回吗？未保存的更改将丢失。</span>
    <template #footer>
      <el-button class="theme-btn-outline" @click="showCancelDialog = false">取消</el-button>
      <el-button type="primary" class="theme-btn-primary" @click="doCancel">确定</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="showSaveDialog" title="提示" width="350px" center class="custom-dialog">
    <span>确定要保存修改吗？</span>
    <template #footer>
      <el-button class="theme-btn-outline" @click="showSaveDialog = false">取消</el-button>
      <el-button type="primary" class="theme-btn-primary" @click="updateData">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { InfoFilled, ArrowLeft } from '@element-plus/icons-vue'
import { updateUser } from '@/api/user'
import type { EmpData } from '@/api/register'
import { getEmpMessage } from '@/api/empMessage'
import { uploadAvatar, getAvatarPath } from '@/api/user'

const updateData = () => {
  updateUser(empForm.value).then(res => {
    console.log(res);
    location.reload();
  })
}

// 初始化表单数据
const initialFormData = ref<EmpData>({
  username: '',
  nickname: '',
  password: '',
  name: '',
  age: 18,
  sex: 1,
  eduBack: '',
  salaryWishK: 0,
  address: '',
  hasExperience: 0,
  phone: '',
  email: '',
  major: '',
  introduction: ''
})

// 返回上一页
const handleBack = () => {
  router.push('/personalcenter')
}

const format = (percentage: number) => {
  return percentage === 100 ? '完整' : `${percentage}%`
}

// 计算信息完整度
const calculateCompletion = computed(() => {
  const requiredFields = [
    'username',
    'nickname',
    'name',
    'age',
    'sex',
    'eduBack',
    'salaryWishK',
    'address',
    'hasExperience',
    'phone',
    'email',
    'major',
    'introduction',

  ] as const

  const filledFields = requiredFields.filter(field => {
    const value = empForm.value[field]
    return value !== '' && value !== null && value !== undefined
  })
  return Math.round((filledFields.length / requiredFields.length) * 100)
})

const empFormRef = ref<FormInstance>()
const empForm = ref<EmpData>({ ...initialFormData.value })

getEmpMessage().then(res => {
  initialFormData.value = res.data.list[0]
  empForm.value = res.data.list[0]
  // 只有这里 username 才有值
  getAvatarPathAndSetUrl()
})

function getAvatarPathAndSetUrl() {
  if (!empForm.value || !empForm.value.username) return
  getAvatarPath(empForm.value.username).then(res => {
    console.log('getAvatarPath返回：', res)
    let filePath = res.data
    console.log('filePath:', filePath)
    // 如果 filePath 没有 / 开头，补上
    if (filePath && !filePath.startsWith('/')) {
      filePath = '/' + filePath
    }
    avatarUrl.value = filePath ? API_BASE_URL + filePath : ''
    console.log('最终avatarUrl:', avatarUrl.value)
  })
}

const router = useRouter()

// 表单验证规则
const empRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 18, max: 60, message: '年龄需在18到60之间', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] },
  ],
  eduBack: [{ required: true, message: '请选择学历', trigger: 'change' }],
  salaryWishK: [
    { required: true, message: '请输入期望薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '期望薪资不能为负数', trigger: 'blur' },
  ],
  address: [{ required: true, message: '请输入居住地址', trigger: 'blur' }],
  introduction: [
    { required: true, message: '请输入个人介绍', trigger: 'blur' },
    { min: 50, message: '个人介绍至少50个字符', trigger: 'blur' },
  ],
}

// 监听表单变化
const handleFormChange = () => {
  isFormModified.value = true
}

const avatarInput = ref<HTMLInputElement>()
const avatarUrl = ref<string>('')

const API_BASE_URL = 'http://127.0.0.1:8080'

onMounted(async () => {
  try {
    const res = await getAvatarPath(empForm.value.username)
    let filePath = res.data.filePath || ''
    // 去掉 /upload/avatar/ 前缀
    if (filePath.startsWith('/upload/avatar/')) {
      filePath = filePath.replace('/upload/avatar/', '/')
    }
    avatarUrl.value = filePath ? API_BASE_URL + filePath : ''
    console.log('头像URL:', avatarUrl.value)
  } catch {
    loadDefaultAvatar()
  }
})

const handleAvatarUpload = () => {
  avatarInput.value?.click()
}

const resetAvatar = () => {
  loadDefaultAvatar()
  location.reload()
  ElMessage.success('头像已重置为默认')
}

const onAvatarChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  // 构造 FormData
  const formData = new FormData()
  formData.append('username', empForm.value.username)
  formData.append('file', file)

  try {
    await uploadAvatar(formData)
    const res = await getAvatarPath(empForm.value.username)
    let filePath = res.data.filePath || ''
    if (filePath.startsWith('/upload/avatar/')) {
      filePath = filePath.replace('/upload/avatar/', '/')
    }
    avatarUrl.value = filePath ? API_BASE_URL + filePath : ''
    ElMessage.success('头像更新成功')
  } catch (e) {
    ElMessage.error('头像上传失败')
  }
  location.reload()
}

//生成默认头像
function loadDefaultAvatar() {
  const defaultAvatarSVG = `<svg width=100 height=100 viewBox=0000 fill=none" xmlns=http://www.w3.org/2000/svg><rect width=100 height=100 fill='#F5F5F5'/><circle cx=50 cy=35 r=15 fill='#3498db'/><path d='M20 75C20 65.5763 28.0576 57 38 57H62C71.9424 57 80 65.5763 80 75' fill='#CCCCCC'/></svg>`
  const svgBlob = new Blob([defaultAvatarSVG], { type: 'image/svg+xml' })
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target?.result as string
    avatarUrl.value = result
    localStorage.setItem('userAvatar', result)
  }
  reader.readAsDataURL(svgBlob)
}

const handleCancel = () => {
  showCancelDialog.value = true
}

const handleSave = async () => {
  if (!empFormRef.value) return
  try {
    isSubmitting.value = true
    await empFormRef.value.validate()
    // 校验通过，弹出确认保存弹窗
    showSaveDialog.value = true
  } catch (error) {
    // 校验未通过，弹出提示弹窗
    showValidateDialog.value = true
  } finally {
    isSubmitting.value = false
  }
}

// 检查表单是否被修改
const checkFormModified = () => {
  const formKeys = Object.keys(empForm.value) as Array<keyof EmpData>
  return formKeys.some(key => {
    return empForm.value[key] !== initialFormData.value[key]
  })
}

const isSubmitting = ref(false)

// 提示信息
const validateMsg = ref('请完善必填信息后再提交')
// 校验未通过，弹出提示弹窗
const showValidateDialog = ref(false)

const doCancel = () => {
  showCancelDialog.value = false
  router.push('/personalcenter')
}

// 取消修改，弹出提示弹窗
const showCancelDialog = ref(false)
// 确认保存，弹出提示弹窗
const showSaveDialog = ref(false)

// 返回确认
const confirmBack = () => {
  router.push('/personalCenter/employee/usermassage')
}

const isFormModified = ref(false)

// 生命周期钩子：组件挂载后加载用户信息
const loadUserInfo = async () => {
  try {
    // 模拟数据
    const userData: EmpData = {
      username: 'jobseeker123',
      nickname: '求职达人',
      password: '', // ← 添加这一行
      name: '张三',
      age: 25,
      sex: 1,
      eduBack: 'bachelor',
      salaryWishK: 15,
      address: '北京市朝阳区',
      hasExperience: 1,
      phone: '13800138000',
      email: 'example@mail.com',
      major: '',
      introduction: '5年互联网产品经理经验，擅长用户需求分析和产品设计...'
    }
    empForm.value = userData
    initialFormData.value = {...userData}
    isFormModified.value = false
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 组件挂载后加载用户信息
loadUserInfo()

//专业下拉框
const majorOptions = [
  '计算机科学', '软件工程', '电子工程', '市场营销', '人力资源', '会计学', '机械设计', '电子信息工程',
  '通信工程', '计算机应用技术', '自动化', '信息与计算科学', '数字媒体技术', '网络工程', '计算机技术',
  '电子商务', '机械工程', '电气工程及其自动化', '通信与信息系统', '电子科学与技术', '人力资源管理', '不限专业', '其他'
]
const showOtherMajor = ref(false)
const otherMajor = ref('')

const onMajorChange = (val: string) => {
  showOtherMajor.value = (val === '其他')
  if (!showOtherMajor.value) {
    otherMajor.value = ''
  }
}

</script>

<style>
/* 添加全局样式，不使用 scoped */
.el-message-box__wrapper {
  position: fixed !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
  margin: 0 !important;
}

.el-overlay-dialog {
  position: fixed !important;
  top: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  left: 0 !important;
  overflow: auto !important;
  margin: 0 !important;
}
</style>

<style scoped>
/* ================= 全局与容器 ================= */
.edit-profile-container {
  min-height: 100vh;
  background: #f5f7fa; /* 统一浅灰色背景 */
  padding: 40px 0;
  display: flex;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.edit-profile-box {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  background: #ffffff;
  border: none;
  overflow: hidden;
}

/* ================= 全局弹窗样式覆盖 ================= */
:deep(.el-message-box__wrapper) {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2001;
}

:deep(.el-overlay) {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2000;
  background-color: rgba(0, 0, 0, 0.5);
}

:deep(.el-message-box) {
  margin-top: 0 !important;
  padding: 20px;
  min-width: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}

:deep(.custom-dialog .el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

/* ================= 顶部导航 ================= */
.page-nav {
  padding: 20px 32px;
  margin-bottom: 0;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
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
  background-color: #fff4ed;
}

/* ================= 页面头部 (深空灰渐变) ================= */
.edit-header {
  text-align: center;
  padding: 40px 0;
  /* 统一高级深空灰渐变 */
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

/* 增加科技感暗纹点缀 */
.edit-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(249,115,22,0.08) 0%, rgba(0,0,0,0) 70%);
  pointer-events: none;
}

.edit-header h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 10px 0;
  letter-spacing: 1px;
  position: relative;
  z-index: 2;
}

.edit-description {
  font-size: 15px;
  color: #9ca3af;
  margin: 0 0 24px 0;
  position: relative;
  z-index: 2;
}

.profile-completion {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  max-width: 300px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  color: #d1d5db;
  font-size: 13px;
}

.profile-completion :deep(.el-progress) {
  width: 100%;
}
/* 覆盖进度条文字颜色以适应深色背景 */
.profile-completion :deep(.el-progress__text) {
  color: #ffffff;
  font-weight: 600;
}

/* ================= 表单主体 ================= */
.edit-form {
  padding: 32px 40px 40px;
  background: #ffffff;
}

.form-section {
  margin-bottom: 32px;
}

/* 内层卡片组 */
.form-group {
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.form-group:hover {
  border-color: #fdba74;
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.06);
}

.group-header {
  display: flex;
  align-items: center;
  gap: 12px;
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

/* 标签样式 */
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

/* ================= 头像上传 ================= */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #e2e8f0;
}

.user-avatar {
  border: 4px solid #ffffff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
  background: #f8fafc;
  transition: transform 0.3s;
}
.user-avatar:hover {
  transform: scale(1.05);
  border-color: #f97316;
}

.avatar-upload {
  text-align: center;
}

.update-avatar-btn {
  color: #f97316 !important;
  font-weight: 500;
  transition: all 0.3s;
}
.update-avatar-btn:hover {
  color: #ea580c !important;
  background: #fff4ed !important;
  border-radius: 20px;
}

.upload-tip {
  display: block;
  color: #94a3b8;
  font-size: 12px;
  margin-top: 8px;
  text-align: center;
}

/* ================= 表单项及控件覆盖 ================= */
.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.form-field {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #4b5563;
  font-size: 14px;
  padding-bottom: 8px;
}

/* 统一输入框状态：深空灰线框，聚焦变活力橙 */
:deep(.custom-input .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner),
:deep(.custom-select .el-input__wrapper),
:deep(.custom-input-number .el-input__wrapper) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  background-color: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 8px 12px;
}

:deep(.custom-input .el-input__wrapper:hover),
:deep(.custom-textarea .el-textarea__inner:hover),
:deep(.custom-select .el-input__wrapper:hover),
:deep(.custom-input-number .el-input__wrapper:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-textarea .el-textarea__inner:focus),
:deep(.custom-select .el-input.is-focus .el-input__wrapper),
:deep(.custom-input-number.is-focus .el-input__wrapper) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

/* 单选框橙色主题 */
:deep(.custom-radio .el-radio__input.is-checked .el-radio__inner) {
  border-color: #f97316;
  background: #f97316;
}
:deep(.custom-radio .el-radio__input.is-checked + .el-radio__label) {
  color: #f97316;
}

.form-hint {
  display: block;
  color: #94a3b8;
  font-size: 12px;
  margin-top: 6px;
  width: 100%;
  line-height: 1.5;
}

.hint-list {
  margin: 8px 0 0 20px;
  padding: 0;
  color: #64748b;
}
.hint-list li {
  margin-bottom: 4px;
}

.salary-range {
  display: flex;
  align-items: center;
  gap: 12px;
}

.salary-unit {
  color: #64748b;
  font-size: 14px;
}

:deep(.custom-input-number) {
  width: 160px;
}

/* ================= 底部操作按钮 ================= */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 32px;
  margin-bottom: 32px;
  border-top: 1px solid #f1f5f9;
}

.submit-button {
  min-width: 160px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 24px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}
.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(234, 88, 12, 0.4);
}

.cancel-button {
  min-width: 160px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 24px;
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  transition: all 0.3s;
}
.cancel-button:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 提示区域 ================= */
.form-tips {
  max-width: 600px;
  margin: 0 auto;
}

.custom-alert {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #4b5563;
}

:deep(.custom-alert .el-alert__title) {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

:deep(.custom-alert .el-alert__content) {
  padding: 8px 0;
}

/* 对话框按钮统一样式 */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  border-radius: 8px;
}
.theme-btn-outline {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  border-radius: 8px;
}
.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .edit-profile-container {
    padding: 16px;
  }
  .edit-profile-box {
    margin: 0;
  }
  .edit-header {
    padding: 32px 0;
  }
  .edit-header h2 {
    font-size: 22px;
  }
  .edit-form {
    padding: 24px 20px;
  }
  .form-group {
    padding: 20px 16px;
  }
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .form-actions {
    flex-direction: column;
    padding-top: 24px;
    gap: 16px;
  }
  .submit-button,
  .cancel-button {
    width: 100%;
    margin-left: 0 !important;
  }
  .form-tips {
    margin-top: 24px;
  }
}
</style>