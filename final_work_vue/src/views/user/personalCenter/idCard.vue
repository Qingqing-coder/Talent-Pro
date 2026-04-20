<template>
  <div class="id-card-update">
    <div class="header">
      <el-button icon="ArrowLeft" @click="handleBack" class="back-btn">
        返回
      </el-button>
      <h1 class="page-title">更新证件照片</h1>
    </div>

    <div class="main-container">
      <div class="upload-container">
        <div class="upload-box">
          <div class="upload-hint" v-if="!imageUrl">
            <el-icon class="upload-icon"><Camera /></el-icon>
            <p class="upload-text">点击或拖拽证件照正面至此处上传</p>
            <p class="upload-note">支持JPG、PNG格式，大小不超过5MB</p>
          </div>

          <div class="preview-box" v-if="imageUrl">
            <img :src="imageUrl" alt="证件照预览" class="preview-img" />
            <div class="preview-actions">
              <el-button type="text" class="reupload-btn" @click="handleReupload">
                <el-icon><RefreshLeft /></el-icon> 重新上传
              </el-button>
              <el-button type="text" class="remove-btn" @click="handleRemove">
                <el-icon><Delete /></el-icon> 移除
              </el-button>
            </div>
          </div>

          <input
            type="file"
            ref="fileInput"
            class="file-input"
            accept="image/jpeg, image/png"
            @change="handleFileChange"
          />
        </div>

        <div class="example-box">
          <h3 class="example-title">证件照要求</h3>
          <ul class="requirement-list">
            <li>• 需上传证件照正面清晰图片</li>
            <li>• 背景为白色或浅蓝色</li>
            <li>• 面部特征清晰可见，不佩戴墨镜、帽子</li>
            <li>• 图片内容完整，无裁剪、遮挡</li>
          </ul>
          <div class="example-img-container">
            <img
              src="D:\IDEA\data program\final_work_springBoot\facedata\id_photos\example.webp"
              alt="证件照示例"
              class="example-img"
            />
            <p class="example-caption">示例：标准证件照样式</p>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <el-button
          type="primary"
          :disabled="!imageUrl"
          :loading="isUploading"
          @click="handleSubmit"
          class="submit-btn theme-btn-primary"
        >
          <el-icon v-if="isUploading"><Loading /></el-icon>
          <span v-else>确认上传</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Camera, ArrowLeft, RefreshLeft, Delete, Loading } from '@element-plus/icons-vue'
import { showCustomMessage } from '@/utils/message'
import { uploadIdCard } from '@/api/user' // 需创建对应的API方法

// 路由实例
const router = useRouter()

// 响应式变量
const fileInput = ref<HTMLInputElement | null>(null)
const imageUrl = ref('')
const isUploading = ref(false)
const selectedFile = ref<File | null>(null)

// 处理返回
const handleBack = () => {
  router.back()
}

// 触发文件选择
const triggerFileSelect = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    const file = target.files[0]

    // 验证文件类型
    if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
      showCustomMessage('请上传JPG或PNG格式的图片', 'error')
      target.value = ''
      return
    }

    // 验证文件大小
    if (file.size > 5 * 1024 * 1024) {
      // 5MB
      showCustomMessage('图片大小不能超过5MB', 'error')
      target.value = ''
      return
    }

    // 预览图片
    const reader = new FileReader()
    reader.onload = (e) => {
      imageUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
    selectedFile.value = file

    // 清空input值，允许重复选择同一文件
    target.value = ''
  }
}

// 重新上传
const handleReupload = () => {
  triggerFileSelect()
}

// 移除图片
const handleRemove = () => {
  imageUrl.value = ''
  selectedFile.value = null
}

// 提交上传
const handleSubmit = async () => {
  if (!selectedFile.value) return

  isUploading.value = true
  try {
    const formData = new FormData()
    formData.append('front', selectedFile.value)

    const response = await uploadIdCard(formData)

    if (typeof response === 'string') {
      if (response.includes('成功')) {
        showCustomMessage('证件照上传成功', 'success')
      } else {
        showCustomMessage(response || '上传失败，请重试', 'error')
      }
    } else if (response.code === 200) {
      showCustomMessage(response.message || '证件照上传成功', 'success')
    } else {
      showCustomMessage(response.message || '上传失败，请重试', 'error')
    }
  } catch (error) {
    console.error('上传失败:', error)
    showCustomMessage('上传失败，请检查网络后重试', 'error')
  } finally {
    isUploading.value = false
  }
}
// 初始化 - 监听拖拽事件
onMounted(() => {
  // const uploadBox = document.querySelector('.upload-box')
  // if (uploadBox) {
  //   // 拖拽enter事件
  //   uploadBox.addEventListener('dragover', (e) => {
  //     e.preventDefault()
  //     uploadBox.classList.add('dragover')
  //   })
  //
  //   // 拖拽leave事件
  //   uploadBox.addEventListener('dragleave', () => {
  //     uploadBox.classList.remove('dragover')
  //   })
  //
  //   // 拖拽drop事件
  //   uploadBox.addEventListener('drop', (e) => {
  //     e.preventDefault()
  //     uploadBox.classList.remove('dragover')
  //
  //     if (e.dataTransfer.files && e.dataTransfer.files[0]) {
  //       // 处理拖放的文件
  //       const file = e.dataTransfer.files[0]
  //       // 这里可以复用之前的文件验证逻辑
  //       if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
  //         showCustomMessage('请上传JPG或PNG格式的图片', 'error')
  //         return
  //       }
  //       if (file.size > 5 * 1024 * 1024) {
  //         showCustomMessage('图片大小不能超过5MB', 'error')
  //         return
  //       }
  //
  //       const reader = new FileReader()
  //       reader.onload = (e) => {
  //         imageUrl.value = e.target?.result as string
  //       }
  //       reader.readAsDataURL(file)
  //       selectedFile.value = file
  //     }
  //   })
  //
  //   // 点击上传区域触发文件选择
  //   uploadBox.addEventListener('click', triggerFileSelect)
  // }
})
</script>

<style scoped>
/* ================= 全局与容器 ================= */
.id-card-update {
  min-height: 100vh;
  /* 统一极简浅灰底色 */
  background-color: #f5f7fa;
  padding-bottom: 40px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ================= 头部导航 ================= */
.header {
  display: flex;
  align-items: center;
  padding: 0 32px;
  height: 64px;
  /* 深空灰渐变背景 */
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  margin-right: 24px;
  /* 半透明磨砂质感按钮，融入深色背景 */
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  letter-spacing: 1px;
}

/* ================= 主要内容区 ================= */
.main-container {
  max-width: 1000px;
  margin: 40px auto 0;
  padding: 0 24px;
}

/* ================= 上传与示例区域容器 ================= */
.upload-container {
  display: flex;
  gap: 24px;
  margin-bottom: 40px;
}

/* 上传框 */
.upload-box {
  flex: 1;
  height: 420px;
  background-color: #ffffff;
  border: 2px dashed #cbd5e1;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.upload-box:hover {
  border-color: #f97316;
  background-color: #fffaf5; /* 浅橙色背景回馈 */
  box-shadow: 0 10px 25px rgba(249, 115, 22, 0.08);
}

.upload-box.dragover {
  border-color: #f97316;
  background-color: #fff4ed;
  transform: scale(1.02);
}

/* 上传提示内容 */
.upload-hint {
  text-align: center;
  padding: 0 20px;
  pointer-events: none; /* 避免挡住点击事件 */
}

.upload-icon {
  font-size: 56px;
  color: #f97316; /* 活力橙 */
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.upload-box:hover .upload-icon {
  transform: translateY(-5px);
}

.upload-text {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 10px 0;
}

.upload-note {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

/* 预览区域 */
.preview-box {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #ffffff;
  border-radius: 14px;
}

.preview-img {
  max-width: 100%;
  max-height: 280px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid #f1f5f9;
}

.preview-actions {
  margin-top: 24px;
  display: flex;
  gap: 30px;
}

.reupload-btn {
  color: #f97316 !important;
  font-weight: 500;
  font-size: 15px;
}
.reupload-btn:hover {
  color: #ea580c !important;
}

.remove-btn {
  color: #ef4444 !important;
  font-weight: 500;
  font-size: 15px;
}
.remove-btn:hover {
  color: #dc2626 !important;
}

/* 隐藏的文件输入 */
.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

/* ================= 示例说明卡片 ================= */
.example-box {
  flex: 0 0 320px;
  background-color: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
}

.example-title {
  font-size: 18px;
  color: #111827;
  margin: 0 0 20px 0;
  font-weight: 700;
  position: relative;
  padding-left: 12px;
}

.example-title::before {
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

.requirement-list {
  padding-left: 0;
  margin: 0 0 24px 0;
  list-style: none;
}

.requirement-list li {
  font-size: 14px;
  color: #4b5563;
  margin-bottom: 12px;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
}

.requirement-list li::before {
  content: '•';
  color: #f97316;
  font-weight: bold;
  margin-right: 8px;
}

.example-img-container {
  text-align: center;
  margin-top: auto;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.example-img {
  width: 100%;
  max-width: 200px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.example-caption {
  font-size: 13px;
  color: #64748b;
  margin: 12px 0 0 0;
  font-weight: 500;
}

/* ================= 确认上传按钮 ================= */
.action-buttons {
  text-align: center;
  margin-top: 10px;
}

/* 统一活力橙主按钮样式 */
.theme-btn-primary {
  padding: 0 48px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  border-radius: 24px !important;
  color: #ffffff !important;
  box-shadow: 0 8px 20px -6px rgba(234, 88, 12, 0.5);
  transition: all 0.3s ease;
}

.theme-btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px -6px rgba(234, 88, 12, 0.65);
}

.theme-btn-primary:disabled {
  background: #e2e8f0 !important;
  color: #94a3b8 !important;
  box-shadow: none !important;
  cursor: not-allowed;
  transform: none;
}

/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }
  
  .upload-container {
    flex-direction: column;
    gap: 24px;
  }

  .example-box {
    flex: none;
    width: 100%;
  }

  .upload-box {
    height: 320px;
  }
}
</style>