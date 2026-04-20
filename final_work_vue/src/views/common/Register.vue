<template>
  <div class="register-container">
    <el-card class="register-box">
      <div class="register-header">
        <h2>创建您的账号</h2>
        <p class="subtitle">欢迎加入，请填写以下信息完成注册</p>
      </div>

      <div class="user-type-selector">
        <el-radio-group v-model="userType" @change="handleUserTypeChange" class="custom-radio-group">
          <el-radio-button label="jobseeker" class="user-type-button">
            <div class="user-type-content">
              <el-icon class="user-type-icon"><User /></el-icon>
              <h3>求职者</h3>
            </div>
          </el-radio-button>
          <el-radio-button label="company" class="user-type-button">
            <div class="user-type-content">
              <el-icon class="user-type-icon"><OfficeBuilding /></el-icon>
              <h3>企业</h3>
            </div>
          </el-radio-button>
        </el-radio-group>
      </div>

      <p class="user-type-description">
        {{ userType === 'jobseeker' ? '选择求职者身份，开启您的求职之旅' : '选择企业身份，寻找优秀人才' }}
      </p>

      <el-form
        v-if="userType === 'jobseeker'"
        ref="jobseekerFormRef"
        :model="jobseekerForm"
        :rules="jobseekerRules"
        @submit.prevent="handleSubmit"
        label-position="top"
        class="register-form"
      >
        <div class="form-section">
          <div class="form-group">
            <h4 class="form-section-title">账号信息</h4>
            <div class="form-row">
              <el-form-item label="用户名" prop="username" class="form-field">
                <el-input v-model="jobseekerForm.username" @blur="checkUsername" placeholder="请输入用户名" />
                <span :class="isExistEmp == false? 'hide':'show'">用户名已经存在</span>
              </el-form-item>
              <el-form-item label="昵称" prop="nickname" class="form-field">
                <el-input v-model="jobseekerForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="密码" prop="password" class="form-field">
                <el-input
                  v-model="jobseekerForm.password"
                  show-password
                  type="password"
                  placeholder="请输入密码"
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword" class="form-field">
                <el-input
                  v-model="jobseekerForm.confirmPassword"
                  show-password
                  type="password"
                  placeholder="请再次输入密码"
                />
              </el-form-item>
            </div>
          </div>

          <div class="form-group">
            <h4 class="form-section-title">个人信息</h4>
            <div class="form-row">
              <el-form-item
                label="真实姓名"
                prop="realName"
                class="form-field"
              >
                <el-input v-model="jobseekerForm.realName" placeholder="请输入真实姓名">
                  <template #append>
                    <el-tooltip content="仅在与企业私聊时可见" placement="top">
                      <el-icon><InfoFilled /></el-icon>
                    </el-tooltip>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="性别" prop="gender" class="form-field">
                <el-radio-group v-model="jobseekerForm.gender" class="theme-radio">
                  <el-radio label="male">男</el-radio>
                  <el-radio label="female">女</el-radio>
                  <el-radio label="other">保密</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="年龄" prop="age" class="form-field">
                <el-input-number v-model="jobseekerForm.age" :min="18" :max="60" class="theme-input-number" />
              </el-form-item>
              <el-form-item label="手机号码" prop="phone" class="form-field">
                <el-input v-model="jobseekerForm.phone" placeholder="请输入手机号码" />
              </el-form-item>
            </div>

            <el-form-item label="电子邮箱" prop="email" class="form-field">
              <el-input v-model="jobseekerForm.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </div>

          <div class="form-group">
            <h4 class="form-section-title">求职信息</h4>
            <div class="form-row">
              <el-form-item label="学历" prop="education" class="form-field">
                <el-select v-model="jobseekerForm.education" placeholder="请选择学历" class="theme-select">
                  <el-option label="初中" value="middle" />
                  <el-option label="高中" value="high" />
                  <el-option label="专科" value="college" />
                  <el-option label="本科" value="bachelor" />
                  <el-option label="硕士" value="master" />
                  <el-option label="博士" value="doctor" />
                </el-select>
              </el-form-item>
              <el-form-item label="工作经验" prop="hasExperience" class="form-field">
                <el-radio-group v-model="jobseekerForm.hasExperience" class="theme-radio">
                  <el-radio :label="1">有</el-radio>
                  <el-radio :label="0">无</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="专业" prop="major" class="form-field">
                <el-select v-model="jobseekerForm.major" clearable placeholder="请选择专业" class="theme-select">
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
            </div>

            <div class="form-row">
              <el-form-item label="期望薪资" prop="salaryRange" class="form-field">
                <div class="salary-range">
                  <el-input-number v-model="jobseekerForm.salaryWishK" class="theme-input-number" />
                  <span class="salary-unit">千/月</span>
                </div>
              </el-form-item>
              <el-form-item label="居住地址" prop="address" class="form-field">
                <el-input v-model="jobseekerForm.address" placeholder="请输入详细地址" />
              </el-form-item>
            </div>

            <el-form-item label="个人介绍" prop="introduction" class="form-field">
              <el-input
                v-model="jobseekerForm.introduction"
                type="textarea"
                :rows="4"
                placeholder="简单介绍一下自己吧..."
              />
            </el-form-item>
          </div>
        </div>

        <div class="id-photo-upload">
          <input
            type="file"
            id="idPhotoUpload"
            accept="image/*"
            style="display: none"
            @change="handleIdPhotoChange"
          />
          <div class="upload-btn-group">
            <el-button
              type="primary"
              icon="el-icon-upload"
              @click="triggerIdPhotoUpload"
              class="custom-upload-btn theme-btn-outline"
            >
              上传证件照
            </el-button>
            <el-button
              type="success"
              icon="el-icon-check"
              @click="uploadIdCardToServer"
              v-if="idPhotoFileName"
              class="upload-confirm-btn theme-btn-success"
            >
              确认上传
            </el-button>
          </div>
          <div class="selected-file" v-if="idPhotoFileName">
            <el-icon><Document /></el-icon> {{ idPhotoFileName }}
          </div>
        </div>

        <div class="form-actions">
          <el-button type="primary" native-type="submit" class="submit-button theme-btn-primary">
            完成注册
          </el-button>
        </div>

      </el-form>

      <el-form
        v-if="userType === 'company'"
        ref="companyFormRef"
        :model="companyForm"
        :rules="companyRules"
        @submit.prevent="handleSubmit"
        label-position="top"
        class="register-form"
      >
        <div class="form-group">
          <el-form-item label="企业账户名" prop="username" class="form-field">
            <el-input v-model="companyForm.username" @blur="checkUsername" placeholder="请输入企业账户名" />
            <span :class="isExistCompany == false? 'hide':'show'">用户名已经存在</span>
          </el-form-item>

          <el-form-item label="企业名称" prop="companyName" class="form-field">
            <el-input v-model="companyForm.companyname" placeholder="请输入完整企业名称" />
          </el-form-item>

          <div class="form-row">
            <el-form-item label="密码" prop="password" class="form-field">
              <el-input
                v-model="companyForm.password"
                show-password
                type="password"
                placeholder="请输入密码"
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword" class="form-field">
              <el-input
                v-model="companyForm.confirmPassword"
                show-password
                type="password"
                placeholder="请再次输入密码"
              />
            </el-form-item>
          </div>

          <el-form-item label="企业介绍" prop="companyIntroduction" class="form-field">
            <el-input
              v-model="companyForm.companyIntroduction"
              type="textarea"
              :rows="4"
              placeholder="请描述您的企业文化、业务范围等..."
            />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" native-type="submit" class="submit-button theme-btn-primary">
            完成注册
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import {
  User,
  OfficeBuilding,
  InfoFilled,
  Document
} from '@element-plus/icons-vue';
import { addCompany, addEmp, addLogin, isExistUsername } from '@/api/register.ts'
import type {LoginerData} from '@/api/register.ts'
import router from '@/router'
import {setAuthStatus} from '@/utils/auth.ts'
import type {EmpData, CompanyData} from '@/api/register.ts'

// 用户类型
type UserType = 'jobseeker' | 'company';

// 求职者表单类型
interface JobseekerForm {
  username: string;
  nickname: string;
  password: string;
  confirmPassword: string;
  realName: string;
  gender: string;
  age: number;
  phone: string;
  email: string;
  education: string;
  hasExperience: number;
  salaryWishK: number;
  address: string;
  introduction: string;
  major: string;
}

// 企业表单类型
interface CompanyForm {
  username: string;
  companyname: string;
  password: string;
  confirmPassword: string;
  companyIntroduction: string;
}

const userType = ref<UserType>('jobseeker');
const jobseekerFormRef = ref<FormInstance>();
const companyFormRef = ref<FormInstance>();

const jobseekerForm = ref<JobseekerForm>({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  realName: '',
  gender: '',
  age: 18,
  phone: '',
  email: '',
  education: '',
  hasExperience: 0,
  salaryWishK: 0,
  address: '',
  introduction: '',
  major: '',
});
//用户名是否已经存在
const isExistEmp = ref(false);
const isExistCompany = ref(false);
async function checkUsername() {
isExistUsername(jobseekerForm.value.username).then((res: any) => {
    isExistEmp.value = res.data;
  })
  isExistUsername(companyForm.value.username).then((res: any) => {
    isExistCompany.value = res.data;
  })
}

const companyForm = ref<CompanyForm>({
  username: '',
  companyname: '',
  password: '',
  confirmPassword: '',
  companyIntroduction: '',
});

const requestData = ref<LoginerData>({
  username: '',
  password: '',
  role: 0,
})

// 密码验证函数
const validatePassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'));
  } else {
    callback();
  }
};

// 确认密码验证函数
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  const form = userType.value === 'jobseeker' ? jobseekerForm.value : companyForm.value;
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

// 求职者表单验证规则
const jobseekerRules: FormRules<JobseekerForm> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
};

// 企业表单验证规则
const companyRules: FormRules<CompanyForm> = {
  username: [
    { required: true, message: '请输入企业账户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  companyname: [
    { required: true, message: '请输入企业名称', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 切换用户类型
const handleUserTypeChange = () => {
  if (userType.value === 'jobseeker') {
    // 重置求职者表单
    jobseekerForm.value = {
      username: '',
      nickname: '',
      password: '',
      confirmPassword: '',
      realName: '',
      gender: '',
      age: 18,
      phone: '',
      email: '',
      education: '',
      hasExperience: 0,
      salaryWishK: 0,
      address: '',
      introduction: '',
      major: '',
    };
  } else {
    // 重置企业表单
    companyForm.value = {
      username: '',
      companyname: '',
      password: '',
      confirmPassword: '',
      companyIntroduction: ''
    };
  }
};

// 提交表单
const handleSubmit = async () => {
  try {
    if (userType.value === 'jobseeker') {
      await jobseekerFormRef.value?.validate();
      requestData.value.role = 0;
      requestData.value.password = jobseekerForm.value.password;
      requestData.value.username = jobseekerForm.value.username;
      console.log(requestData.value);
      await addLogin(requestData.value);
      const empData = ref<EmpData>({
        username: jobseekerForm.value.username,
        nickname: jobseekerForm.value.nickname,
        password: jobseekerForm.value.password,
        name: jobseekerForm.value.realName,
        age: jobseekerForm.value.age,
        salaryWishK: jobseekerForm.value.salaryWishK,
        address: jobseekerForm.value.address,
        hasExperience: jobseekerForm.value.hasExperience,
        phone: jobseekerForm.value.phone,
        email: jobseekerForm.value.email,
        eduBack: jobseekerForm.value.education,
        introduction: jobseekerForm.value.introduction,
        sex: (jobseekerForm.value.gender === '男'? 1:0),
        major: jobseekerForm.value.major,
      })
      await addEmp(empData.value);
      setAuthStatus(true ,requestData.value.username)
      await  router.push('/shouye');
    } else {
      await companyFormRef.value?.validate();
      requestData.value.role = 1;
      requestData.value.password = companyForm.value.password;
      requestData.value.username = companyForm.value.username;
      await addLogin(requestData.value);
      const companyData = ref<CompanyData> ({
        username: companyForm.value.username,
        nickname: companyForm.value.companyname,
        password: companyForm.value.password,
        companyIntroduction: companyForm.value.companyIntroduction,
      })
      console.log(companyData);
      await addCompany(companyData.value);
      setAuthStatus(true ,requestData.value.username)
      localStorage.setItem('companyUsername', requestData.value.username)
      await router.push({ name: 'companyhome' });
    }
  } catch (error) {
    console.log('表单验证失败', error);
  }
};

const selectedIdPhoto = ref<File | null>(null); // 存储选中的证件照文件
const idPhotoFileName = ref(''); // 存储选中的证件照文件名
// 触发文件选择
const triggerIdPhotoUpload = () => {
  (document.getElementById('idPhotoUpload') as HTMLInputElement).click();
};

// 处理文件选择
const handleIdPhotoChange = (e: Event) => {
  const fileInput = e.target as HTMLInputElement;
  
  // 使用可选链获取第一个文件
  const file = fileInput.files?.[0];
  
  // 直接判断 file 是否存在（类型收窄：此时 file 100% 为 File 类型）
  if (file) {
    selectedIdPhoto.value = file;
    idPhotoFileName.value = file.name;
  }
};

// 上传证件照到后端
const uploadIdCardToServer = async () => {
  if (!selectedIdPhoto.value) {
    alert('请先选择证件照');
    return;
  }
  const username = sessionStorage.getItem('username') || '';
  const formData = new FormData();
  formData.append('file', selectedIdPhoto.value);
  formData.append('username', username);
  try {
    const response = await fetch('http://localhost:8080/face/uploadIdCard',{
      method: 'POST',
      body: formData
    });
    const data = await response.json();
    if (data.code === 200) {
      alert('证件照上传成功');
    } else {
      alert('证件照上传成功');
    }
  } catch (error) {
    alert('证件照上传成功');
  }
};

</script>

<style scoped>
/* ================= 全局 & 容器样式 ================= */
.register-container {
  min-height: 100vh;
  /* 深灰色渐变背景，体现现代感 */
  background: linear-gradient(135deg, #2c2e33 0%, #1a1b1e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 白色卡片，带层次阴影 */
.register-box {
  width: 100%;
  max-width: 850px;
  background-color: #ffffff;
  border-radius: 16px;
  border: none;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
}

:deep(.el-card__body) {
  padding: 40px 50px;
}

/* ================= 头部样式 ================= */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 28px;
  color: #2c2e33;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 1px;
}

.register-header .subtitle {
  color: #8c939d;
  font-size: 14px;
}

/* ================= 身份选择区域 ================= */
.user-type-selector {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

/* 重构单选框为卡片形式 */
:deep(.custom-radio-group .el-radio-button__inner) {
  width: 180px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fb;
  border: 2px solid transparent !important;
  border-radius: 12px !important;
  color: #606266;
  margin: 0 10px;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

:deep(.custom-radio-group .el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #fff4ed;
  border-color: #ff6b00 !important;
  color: #ff6b00;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.15) !important;
}

.user-type-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.user-type-icon {
  font-size: 32px;
}

.user-type-description {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-bottom: 30px;
}

/* ================= 表单区域 ================= */
.register-form {
  padding: 0 10px;
}

.form-section {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 30px;
  background: #fafafa;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}

/* 带有橙色点缀的标题 */
.form-section-title {
  font-size: 16px;
  color: #1a1b1e;
  margin-bottom: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.form-section-title::before {
  content: "";
  display: inline-block;
  width: 4px;
  height: 16px;
  background-color: #ff6b00;
  border-radius: 2px;
  margin-right: 8px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-field {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #4c4e54;
  padding-bottom: 6px;
}

/* ================= 输入框主题覆盖 (橙色) ================= */
:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #ffaa66 inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #ff6b00 inset !important;
}

/* 数字输入框、下拉框选中状态覆盖 */
:deep(.theme-input-number .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #ff6b00 inset !important;
}
:deep(.theme-select .el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #ff6b00 inset !important;
}
:deep(.el-select-dropdown__item.selected) {
  color: #ff6b00;
}

/* 单选框橙色主题 */
:deep(.theme-radio .el-radio__input.is-checked .el-radio__inner) {
  border-color: #ff6b00;
  background: #ff6b00;
}
:deep(.theme-radio .el-radio__input.is-checked + .el-radio__label) {
  color: #ff6b00;
}

.salary-range {
  display: flex;
  align-items: center;
  gap: 12px;
}
.salary-unit {
  color: #606266;
  font-size: 14px;
}

/* ================= 证件照上传区域 ================= */
.id-photo-upload {
  margin: 20px 0 30px;
  padding: 24px;
  background: #f8f9fb;
  border: 1px dashed #dcdfe6;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: border-color 0.3s;
}
.id-photo-upload:hover {
  border-color: #ffaa66;
}

.upload-btn-group {
  display: flex;
  gap: 16px;
}

/* 镂空主色调按钮 */
.theme-btn-outline {
  background-color: transparent !important;
  border: 1px solid #ff6b00 !important;
  color: #ff6b00 !important;
  font-weight: 500;
  transition: all 0.3s;
}
.theme-btn-outline:hover {
  background-color: #fff4ed !important;
}

/* 确认上传按钮（绿/橙过渡可自定义，这里保持原逻辑的success，调一下质感） */
.theme-btn-success {
  background: #67c23a !important;
  border: none !important;
  color: #fff !important;
}
.theme-btn-success:hover {
  background: #85ce61 !important;
  box-shadow: 0 4px 10px rgba(103, 194, 58, 0.3);
}

.selected-file {
  margin-top: 12px;
  color: #ff6b00;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff4ed;
  padding: 6px 12px;
  border-radius: 20px;
}

/* ================= 注册大按钮 ================= */
.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.theme-btn-primary {
  width: 100%;
  max-width: 320px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #ff8c00 0%, #ff5722 100%) !important;
  border: none !important;
  border-radius: 24px !important;
  color: #ffffff;
  box-shadow: 0 6px 16px rgba(255, 87, 34, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}

.theme-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 87, 34, 0.4);
}

.theme-btn-primary:active {
  transform: translateY(0);
}

/* ================= 错误提示信息样式 ================= */
.hide {
  display: none !important;
}

.show {
  display: block;
  margin-top: 6px;
  padding: 8px 12px;
  font-size: 12px;
  color: #f56c6c;
  background-color: #fef0f0;
  border-radius: 6px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  :deep(.custom-radio-group .el-radio-button__inner) {
    width: 140px;
  }
  
  :deep(.el-card__body) {
    padding: 30px 20px;
  }
}
</style>