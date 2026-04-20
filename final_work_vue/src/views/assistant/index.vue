<template>
  <div class="assistant-wrapper">
    <div class="assistant-card">
      <header class="topbar">
        <div class="brand">
          <div class="logo-icon">
            <div class="logo-inner"></div>
          </div>
          <div class="title">AI 智能助手</div>
        </div>

        <button
          class="kg-btn"
          @click="mockJump"
          @mouseenter="kgHover = true"
          @mouseleave="kgHover = false"
        >
          <span class="icon">🕸️</span>
          <span>查看知识图谱</span>
        </button>
      </header>

      <main class="chat-area" ref="chatArea">
        <div v-for="message in messages" :key="message.id" class="msg" :class="message.type">
          <div v-if="message.type === 'ai'" class="avatar ai-avatar">🤖</div>
          <div class="bubble">
            {{ message.content }}
            <div class="meta">{{ message.timestamp }}</div>
          </div>
          <div v-if="message.type === 'user'" class="avatar user-avatar">🧑</div>
        </div>

        <div v-if="isLoading" class="msg ai">
          <div class="avatar ai-avatar">🤖</div>
          <div class="bubble">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </main>

      <footer class="input-bar">
        <div class="input-wrap">
          <textarea
            v-model="input"
            class="ask-input"
            placeholder="向 AI 助手提问..."
            rows="1"
            @keydown.enter.prevent="sendMessage"
            @keydown.shift.enter="input += '\n'"
            @input="autoResize"
            ref="textareaRef"
          />
        </div>
        <div class="actions">
          <button class="action-btn attach" title="上传附件" @click="sendfile">📎</button>
          <button class="action-btn send" title="发送" @click="sendMessage" :disabled="isLoading">
            <span class="send-icon">➤</span>
          </button>
        </div>
      </footer>

      <transition name="slide-out">
        <div v-if="kgHover && jumping" class="jump-mask"></div>
      </transition>

      <div v-if="showUploadOptions" class="modal-overlay" @click.self="cancelUploadOptions">
        <div class="modal custom-dialog-style">
          <div class="modal-header">上传知识库设置</div>
          <div class="modal-body horizontal-form">
            <div class="form-row">
              <label for="chunkSize">单段最大长度</label>
              <select id="chunkSize" v-model.number="uploadOptions.chunkSize" class="custom-select-native">
                <option :value="200">200</option>
                <option :value="300">300</option>
                <option :value="500">500</option>
                <option :value="800">800</option>
                <option :value="1000">1000</option>
              </select>
            </div>
            <div class="form-row">
              <label for="overlap">相邻重合长度</label>
              <select id="overlap" v-model.number="uploadOptions.overlap" class="custom-select-native">
                <option :value="20">20</option>
                <option :value="30">30</option>
                <option :value="50">50</option>
                <option :value="80">80</option>
                <option :value="100">100</option>
              </select>
            </div>
            <div class="form-row">
              <label for="semanticSplit">分块方式</label>
              <select id="semanticSplit" v-model="uploadOptions.semanticSplit" class="custom-select-native">
                <option :value="false">递归分块</option>
                <option :value="true">语义分块</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <el-button class="theme-btn-outline" @click="cancelUploadOptions">取消</el-button>
            <el-button class="theme-btn-primary" type="primary" @click="confirmUploadOptions">选择文件并上传</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import router from '@/router'
import request from '@/utils/request'

// 1. 统一 API 配置（明确 Swagger 后端代理前缀）
const API_CONFIG = {
  baseUrl: '/swagger-api', // 对应 Vite 代理的 Swagger 后端（172.25.111.251:8096）
  chat: '/ask/commonChat',
  upload: '/knowledge/upload',
}

// 消息接口定义
interface Message {
  id: string
  type: 'user' | 'ai'
  content: string
  timestamp: string
}

const input = ref('')
const searchFocused = ref(false)
const kgHover = ref(false)
const jumping = ref(false)
const chatArea = ref<HTMLElement | null>(null)
const textareaRef = ref<HTMLTextAreaElement | null>(null)

// 对话历史存储
const messages = ref<Message[]>([
  {
    id: '1',
    type: 'ai',
    content: '你好，我是你的 AI 小助手，可以帮你搜索知识、解答问题。',
    timestamp: getCurrentTime(),
  },
])

// 加载状态
const isLoading = ref(false)

// 上传选项弹窗状态
const showUploadOptions = ref(false)
const uploadOptions = ref({
  chunkSize: 500,
  overlap: 50,
  semanticSplit: false,
})

// 知识图谱跳转
function mockJump() {
  jumping.value = true
  setTimeout(() => {
    jumping.value = false
  }, 500)

  sessionStorage.setItem('fromHome', 'true') 
  router.push('/graph')
}

// 生成唯一ID
function generateId() {
  return Date.now().toString() + Math.random().toString(36).substr(2, 9)
}

// 获取当前时间
function getCurrentTime() {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

// 发送消息（适配统一API，增加跨域日志）
async function sendMessage() {
  console.log('sendMessage 被调用')
  if (!input.value.trim() || isLoading.value) return

  // 添加用户消息
  const userMessage: Message = {
    id: generateId(),
    type: 'user',
    content: input.value.trim(),
    timestamp: getCurrentTime(),
  }
  messages.value.push(userMessage)

  // 清空输入框
  const messageContent = input.value.trim()
  input.value = ''
  isLoading.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    let currentUsername = localStorage.getItem("username")
    console.log("当前用户名为：")
    console.log(currentUsername)
    const requestUrl = `${API_CONFIG.baseUrl}${API_CONFIG.chat}?question=${encodeURIComponent(messageContent)}&username=${currentUsername}`
    console.log('发送问题到:', requestUrl)

    // 发送请求：显式配置跨域相关头，禁用 credentials 避免冲突
    const response = await fetch(requestUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Request-Method': 'GET', // 明确跨域请求方法
        'Access-Control-Request-Headers': 'Content-Type' // 明确跨域请求头
      },
      credentials: 'omit', // 关键：禁用凭证携带，避免与后端 * 跨域头冲突
      mode: 'cors' // 显式指定跨域模式
    })

    // 打印响应头，排查跨域配置
    console.log('聊天接口响应头:', Object.fromEntries(response.headers.entries()))

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)
    const responseData = await response.json()

    let finalResponse = '抱歉，我暂时无法回答您的问题。'
    if (responseData && responseData.code === 200 && responseData.data) {
      finalResponse = responseData.data
    } else if (responseData && responseData.message) {
      finalResponse = responseData.message
    }

    messages.value.push({
      id: generateId(),
      type: 'ai',
      content: finalResponse,
      timestamp: getCurrentTime(),
    })
  } catch (error) {
    console.error('发送消息失败（含跨域排查）:', {
      error: error instanceof Error ? error.message : '未知错误',
      stack: error instanceof Error ? error.stack : '',
      requestUrl: `${API_CONFIG.baseUrl}${API_CONFIG.chat}?question=${encodeURIComponent(messageContent)}`
    })
    // 增加跨域错误提示
    messages.value.push({
      id: generateId(),
      type: 'ai',
      content: `❌ 操作失败：${error instanceof Error ? error.message : '未知错误'}\n若提示 CORS 相关错误，请检查后端跨域配置（需允许 localhost:5173 且不使用 *）`,
      timestamp: getCurrentTime(),
    })
  } finally {
    isLoading.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 滚动到底部
function scrollToBottom() {
  if (chatArea.value) {
    chatArea.value.scrollTop = chatArea.value.scrollHeight
  }
}

// 自动调整输入框高度
function autoResize() {
  if (textareaRef.value) {
    textareaRef.value.style.height = 'auto'
    textareaRef.value.style.height = Math.min(textareaRef.value.scrollHeight, 120) + 'px'
  }
}

// 测试服务器连接（增加跨域头检测）
async function testConnection() {
  try {
    const testUrl = `${API_CONFIG.baseUrl}${API_CONFIG.chat}?question=test`
    console.log('测试服务器连接:', testUrl)

    const response = await fetch(testUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Request-Method': 'GET'
      },
      credentials: 'omit', // 禁用凭证，避免跨域冲突
      mode: 'cors'
    })

    // 关键：打印后端返回的跨域相关头，判断是否配置正确
    const corsHeaders = {
      'Access-Control-Allow-Origin': response.headers.get('Access-Control-Allow-Origin'),
      'Access-Control-Allow-Credentials': response.headers.get('Access-Control-Allow-Credentials'),
      'Access-Control-Allow-Methods': response.headers.get('Access-Control-Allow-Methods')
    }
    console.log('后端跨域配置检测:', corsHeaders)
    console.log('服务器连接测试结果:', response.status, response.statusText)

    // 若跨域头配置错误，提前提示
    if (corsHeaders['Access-Control-Allow-Origin'] === '*' && corsHeaders['Access-Control-Allow-Credentials'] === 'true') {
      console.error('跨域配置冲突：Access-Control-Allow-Origin 为 * 时，不能开启 Allow-Credentials')
      messages.value.push({
        id: generateId(),
        type: 'ai',
        content: '⚠️ 检测到后端跨域配置冲突：Allow-Origin 为 * 且 Allow-Credentials 为 true，会导致文件上传失败，请后端修改为允许具体源（如 localhost:5173）',
        timestamp: getCurrentTime(),
      })
    }
  } catch (error) {
    console.error('服务器连接测试失败（跨域排查）:', {
      error: error instanceof Error ? error.message : '未知错误',
      stack: error instanceof Error ? error.stack : ''
    })
  }
}

// 打开上传选项弹窗
function sendfile() {
  console.log('sendfile 被调用')
  showUploadOptions.value = true
}

// 取消上传选项
function cancelUploadOptions() {
  showUploadOptions.value = false
}

// 确认上传选项并选择文件（增加文件格式校验）
function confirmUploadOptions() {
  showUploadOptions.value = false
  const fileInput = document.createElement('input')
  fileInput.type = 'file'
  fileInput.accept = '.txt,.pdf,.doc,.docx,.jpg,.png,.gif'
  fileInput.onchange = (event) => {
    const target = event.target as HTMLInputElement
    const file = target.files?.[0]
    if (!file) {
      console.error('未选择有效文件')
      return
    }
    // 校验文件格式（避免后端因格式拒绝导致误判跨域）
    const allowedTypes = [
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // docx
      'application/msword', // doc
      'text/plain', // txt
      'application/pdf' // pdf
    ]
    if (!allowedTypes.includes(file.type) && !file.name.endsWith('.docx')) {
      console.error('不支持的文件格式，仅支持 txt/pdf/doc/docx')
      messages.value.push({
        id: generateId(),
        type: 'ai',
        content: `❌ 不支持的文件格式：${file.type}\n仅支持 txt/pdf/doc/docx 格式，请重新选择`,
        timestamp: getCurrentTime(),
      })
      return
    }
    console.log('选择的有效文件:', file.name, file.type, file.size + ' KB')
    uploadFile(file, { ...uploadOptions.value })
  }
  fileInput.click()
}

// 上传文件（核心：跨域排查配置）
async function uploadFile(
  file: File,
  options: { chunkSize: number; overlap: number; semanticSplit: boolean },
) {
  try {
    const uploadingMsg: Message = {
      id: generateId(),
      type: 'user',
      content: `📎 正在上传文件: ${file.name} (${(file.size / 1024).toFixed(2)} KB)`,
      timestamp: getCurrentTime(),
    }
    messages.value.push(uploadingMsg)
    await nextTick()
    scrollToBottom()

    // 1. 构造 FormData（严格匹配 Swagger，增加参数日志）
    const formData = new FormData()
    formData.append('file', file)
    console.log('FormData 中文件参数:', formData.get('file')) // 确认文件已添加

    // 2. 构造 query 参数
    const queryParams = new URLSearchParams()
    queryParams.append('chunkSize', String(options.chunkSize))
    queryParams.append('overlap', String(options.overlap))
    queryParams.append('semanticSplit', String(options.semanticSplit))
    console.log('上传 query 参数:', queryParams.toString())

    // 3. 拼接请求地址
    const uploadUrl = `${API_CONFIG.baseUrl}${API_CONFIG.upload}?${queryParams.toString()}`
    console.log('文件上传地址（代理后）:', uploadUrl)

    // 4. 发送上传请求（跨域关键配置）
    const response = await fetch(uploadUrl, {
      method: 'POST',
      body: formData,
      headers: {
        // 不手动设 Content-Type，浏览器自动添加正确的 multipart/form-data 及分隔符
        'Access-Control-Request-Method': 'POST', // 明确跨域请求方法
        'Access-Control-Request-Headers': 'Content-Type' // 明确跨域请求头
      },
      credentials: 'omit', // 禁用凭证携带，避免与后端 * 跨域头冲突
      mode: 'cors', // 显式指定跨域模式
      timeout: 60000 // 延长超时，避免大文件上传超时误判跨域
    })

    // 5. 打印跨域相关响应头，排查后端配置
    const corsHeaders = {
      'Access-Control-Allow-Origin': response.headers.get('Access-Control-Allow-Origin'),
      'Access-Control-Allow-Credentials': response.headers.get('Access-Control-Allow-Credentials'),
      'Content-Type': response.headers.get('Content-Type')
    }
    console.log('文件上传响应跨域头:', corsHeaders)

    if (!response.ok) {
      // 打印响应内容，辅助排查（如后端返回的错误信息）
      const errorResponse = await response.text().catch(() => '无响应内容')
      throw new Error(`上传失败: ${response.status} ${response.statusText}\n后端返回: ${errorResponse}`)
    }

    // 处理响应（兼容文本/JSON格式）
    let result
    const contentType = response.headers.get('Content-Type') || ''
    if (contentType.includes('application/json')) {
      result = await response.json()
      console.log('文件上传成功（JSON响应）:', result)
    } else {
      result = await response.text()
      console.log('文件上传成功（文本响应）:', result)
    }

    // 基于响应内容判断是否真成功（避免后端假成功）
    let successMsg = `✅ 文件上传成功！\n文件名: ${file.name}\n文件大小: ${(file.size / 1024).toFixed(2)} KB`
    if (typeof result === 'object' && result.code !== 200) {
      throw new Error(`后端返回失败: ${result.message || '未知错误'}`)
    } else if (typeof result === 'string' && !result.includes('成功')) {
      throw new Error(`后端返回异常: ${result}`)
    }

    messages.value.push({
      id: generateId(),
      type: 'ai',
      content: `${successMsg}\n已添加到知识库，可基于此文件问答。`,
      timestamp: getCurrentTime(),
    })
  } catch (error) {
    console.error('文件上传失败（跨域排查详情）:', {
      error: error instanceof Error ? error.message : '未知错误',
      stack: error instanceof Error ? error.stack : '',
      uploadUrl: `${API_CONFIG.baseUrl}${API_CONFIG.upload}?${new URLSearchParams(options).toString()}`,
      fileInfo: { name: file.name, type: file.type, size: file.size }
    })
    // 精准提示跨域错误
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    let corsHint = ''
    if (errorMsg.includes('CORS') || errorMsg.includes('Access-Control')) {
      corsHint = '\n⚠️ 推测为跨域问题：请后端配置 Access-Control-Allow-Origin: http://localhost:5173（不要用 *），且关闭 Allow-Credentials 或设为 false'
    }
    messages.value.push({
      id: generateId(),
      type: 'ai',
      content: `❌ 文件上传失败: ${errorMsg}${corsHint}`,
      timestamp: getCurrentTime(),
    })
  } finally {
    await nextTick()
    scrollToBottom()
  }
}

// 组件挂载（延迟初始化，避免浏览器扩展干扰跨域检测）
onMounted(() => {
  setTimeout(() => {
    nextTick(() => chatArea.value && (chatArea.value.scrollTop = chatArea.value.scrollHeight))
    testConnection() // 初始化时检测跨域配置
  }, 500)
})
</script>

<style scoped>
/* ================= 全局与容器 ================= */
.assistant-wrapper {
  min-height: calc(100vh - 60px);
  background: #f5f7fa; /* 统一极简浅灰背景 */
  padding: 24px;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.assistant-card {
  max-width: 1000px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 108px);
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

/* ================= 顶部导航 (深空灰) ================= */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(145deg, #242933 0%, #15181e 100%);
  border-bottom: none;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.4);
}

.logo-inner {
  width: 10px;
  height: 10px;
  background: #ffffff;
  border-radius: 2px;
  transform: rotate(45deg);
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}

/* 悬浮跳转按钮 */
.kg-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
}

.kg-btn:hover {
  background: rgba(249, 115, 22, 0.15);
  border-color: rgba(249, 115, 22, 0.4);
  color: #f97316;
  transform: translateY(-1px);
}

/* ================= 聊天内容区 ================= */
.chat-area {
  flex: 1;
  overflow: auto;
  background: #f8fafc;
  padding: 24px;
}

.msg {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: flex-end;
}

.msg.ai {
  justify-content: flex-start;
}

.msg.user {
  justify-content: flex-end;
}

/* 头像设计 */
.avatar {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ai-avatar {
  background: #ffffff;
  border: 1px solid #e2e8f0;
}

.user-avatar {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #ffffff;
  border: none;
}

/* 气泡设计 */
.bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.msg.ai .bubble {
  background: #ffffff;
  color: #111827;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.msg.user .bubble {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #ffffff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.2);
  border: none;
}

.meta {
  font-size: 11px;
  margin-top: 6px;
  text-align: right;
  opacity: 0.7;
}

.msg.ai .meta {
  color: #94a3b8;
}

/* 打字动画 (活力橙) */
.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 4px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f97316;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }
.typing-indicator span:nth-child(3) { animation-delay: 0s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.3; }
  40% { transform: scale(1); opacity: 1; }
}

/* ================= 底部输入区 ================= */
.input-bar {
  display: flex;
  gap: 16px;
  align-items: flex-end;
  padding: 20px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
}

.input-wrap {
  flex: 1;
}

/* 输入框深空灰线框，聚焦变活力橙 */
.ask-input {
  width: 100%;
  resize: none;
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0;
  border: none;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 14px;
  color: #111827;
  outline: none;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  font-family: inherit;
  line-height: 1.5;
}

.ask-input:hover {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1;
}

.ask-input:focus {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02);
}

.actions {
  display: flex;
  gap: 10px;
  padding-bottom: 2px;
}

.action-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

.action-btn.attach {
  background: #ffffff;
  border: 1px solid #cbd5e1;
  color: #475569;
  font-size: 18px;
}

.action-btn.attach:hover {
  border-color: #f97316;
  color: #ea580c;
  background: #fff4ed;
}

.action-btn.send {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
}

.action-btn.send:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4);
}

.action-btn.send:disabled {
  background: #e2e8f0;
  box-shadow: none;
  color: #94a3b8;
  cursor: not-allowed;
}

.send-icon {
  font-size: 14px;
  margin-left: 2px;
}

/* ================= 动画与遮罩 ================= */
.slide-out-enter-active,
.slide-out-leave-active {
  transition: transform 0.4s ease, opacity 0.4s ease;
}
.slide-out-enter-from,
.slide-out-leave-to {
  transform: translateX(0);
  opacity: 0;
}
.slide-out-leave-from {
  transform: translateX(0);
  opacity: 1;
}
.slide-out-enter-to {
  transform: translateX(100%);
  opacity: 1;
}
.jump-mask {
  position: fixed;
  inset: 0;
  background: #ffffff;
  z-index: 100;
}

/* ================= 上传选项弹窗 ================= */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.custom-dialog-style {
  width: 500px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: none;
}

.modal-header {
  padding: 24px;
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  border-bottom: 1px solid #f1f5f9;
}

.modal-body {
  padding: 30px 24px;
}

.horizontal-form .form-row {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.horizontal-form .form-row:last-child {
  margin-bottom: 0;
}

.horizontal-form label {
  width: 120px;
  text-align: right;
  margin-right: 16px;
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
}

/* 原生 select 深度定制以匹配风格 */
.custom-select-native {
  flex: 1;
  height: 38px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  color: #111827;
  outline: none;
  transition: all 0.3s;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2394a3b8%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E");
  background-repeat: no-repeat;
  background-position: right 12px top 50%;
  background-size: 10px auto;
}

.custom-select-native:hover {
  border-color: #cbd5e1;
}

.custom-select-native:focus {
  background-color: #ffffff;
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15);
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  background: #fafbfc;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 按钮统一样式 */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 500;
  border-radius: 8px;
}
.theme-btn-outline {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #4b5563 !important;
  font-weight: 500;
  border-radius: 8px;
}
.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .assistant-wrapper {
    padding: 12px;
  }
  .assistant-card {
    height: calc(100vh - 24px);
  }
  .title {
    display: none;
  }
  .bubble {
    max-width: 85%;
  }
  .custom-dialog-style {
    width: 90%;
  }
  .horizontal-form label {
    width: 90px;
  }
}
</style>