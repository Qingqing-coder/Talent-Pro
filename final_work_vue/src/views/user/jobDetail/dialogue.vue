<template>
  <div class="dialogue-container" v-if="visible">
    <div class="dialogue-overlay" @click="closeDialogue"></div>
    <div class="dialogue-content">
      <div class="dialogue-left" v-if="selectedHr">
        <div class="hr-list-header">
          <h3>与 {{ selectedHr.name }} 沟通</h3>
          <el-button link class="close-btn" @click="closeDialogue">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>

        <div class="hr-info-card">
          <div class="hr-avatar-large">
            <el-avatar :size="80" :src="selectedHr.avatar" class="custom-avatar" />
          </div>
          <div class="hr-details-large">
            <div class="hr-name-large">{{ selectedHr.name }}</div>
            <div class="hr-title-large">{{ selectedHr.title }}</div>
            <div class="hr-company-large">{{ selectedHr.company }}</div>
          </div>
          <div class="hr-job-info">
            <div class="job-title">当前岗位：{{ jobTitle }}</div>
          </div>
        </div>
      </div>

      <div class="dialogue-right">
        <div class="chat-header" v-if="selectedHr">
          <div class="chat-hr-info">
            <el-avatar :size="32" :src="selectedHr.avatar" class="custom-avatar" />
            <div class="hr-details">
              <div class="hr-name">{{ selectedHr.name }}</div>
              <div class="hr-title">{{ selectedHr.title }} · {{ selectedHr.company }}</div>
            </div>
          </div>
          <div class="connection-status">
            <div class="status-dot" :class="isConnected ? 'online' : 'offline'"></div>
            <span class="status-text">{{ isConnected ? '已连接' : '未连接' }}</span>
          </div>
        </div>

        <div class="message-list" ref="messagesContainer">
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>正在加载聊天记录...</span>
          </div>
          <div v-else-if="currentMessages.length === 0" class="empty-container">
            <span>暂无聊天记录</span>
          </div>
          <div v-else class="message-item" v-for="message in currentMessages" :key="message.id"
               :class="{'from-me': message.senderName === '我', 'from-other': message.senderName !== '我'}">
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </div>

        <div class="quick-reply">
          <div class="reply-header">
            <span class="reply-title">快捷回复</span>
            <el-button type="primary" size="small" class="theme-btn-primary-small" @click="sendQuickReply">
              一键发送
            </el-button>
          </div>
          <div class="reply-content">
            <p>您好，看了贵司的 <strong>{{ jobTitle }}</strong> 职位，与我的经历非常匹配，希望详谈，感谢！</p>
          </div>
        </div>

        <div class="input-section">
          <div class="input-container">
            <div class="input-wrapper">
              <el-input
                v-model="inputMessage"
                placeholder="请输入文字，按Enter键发送"
                type="textarea"
                :rows="2"
                resize="none"
                class="custom-textarea"
                @keyup.enter.prevent="sendMessage"
              />
            </div>
            <el-button
              type="primary"
              class="send-btn theme-btn-primary"
              @click="sendMessage"
              :disabled="!inputMessage.trim()"
            >
              发送
            </el-button>
          </div>
        </div>

        <div class="dialogue-footer">
          <span class="footer-text">仅支持查看180天以内的会话</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, onBeforeUnmount, nextTick } from 'vue'
import { Close, Loading } from '@element-plus/icons-vue'
import { getChatHistory } from '@/api/chat'
import { getComList } from '@/api/company'
import { getAuthStatus } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Props {
  visible: boolean
  jobTitle: string
  currentJobHr: {
    id: number
    name: string
    title: string
    company: string
    avatar: string
  }
}

const props = withDefaults(defineProps<Props>(), {
  jobTitle: '测试工程师',
  currentJobHr: () => ({
    id: 2,
    name: '招聘者',
    title: 'HR',
    company: '北京中创科软科技有限公司',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  })
})

const emit = defineEmits<{ close: [] }>()

const inputMessage = ref('')
const selectedHrId = ref(props.currentJobHr?.id || 1)
const messagesData = ref<any[]>([])
const loading = ref(false)
const companyAdminUsername = ref('')
const socket = ref<WebSocket | null>(null)
const isConnected = ref(false)
const messagesContainer = ref<HTMLElement>()

const message = ref({
  id: null as number | null,
  from: '',
  to: '',
  sendTime: null as string | null,
  content: ''
})

const hrList = computed(() => {
  return [{
    id: props.currentJobHr?.id || 1,
    name: props.currentJobHr?.name || '招聘者',
    title: props.currentJobHr?.title || 'HR',
    company: props.currentJobHr?.company || '北京中创科软科技有限公司',
    avatar: props.currentJobHr?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    lastMessage: '您好，看了您的简历，觉得您很适合我们的岗位',
    lastTime: '刚刚'
  }]
})

const selectedHr = computed(() => hrList.value.find(hr => hr.id === selectedHrId.value) || hrList.value[0])
const currentMessages = computed(() => messagesData.value)
const watchVisible = computed(() => props.visible)

const fetchCompanyAdminUsername = async () => {
  if (!selectedHr.value) {
    console.warn("当前未选中 HR 或数据未加载，取消获取公司管理员请求");
    return;
  }
  try {
    const params = { nickname: selectedHr.value.company, pageNum: 1, pageSize: 1 }
    const response = await getComList(params)
    const list = response.data?.list || []
    if (list.length > 0 && list[0].username) {
      companyAdminUsername.value = list[0].username
    } else {
      companyAdminUsername.value = ''
    }
  } catch (error) {
    companyAdminUsername.value = ''
    console.error('获取公司管理员username失败:', error)
  }
}

const checkUserLogin = () => {
  const authStatus = getAuthStatus()
  if (!authStatus.isAuthenticated || !authStatus.username) {
    ElMessageBox.alert('请先登录后再进行聊天', '提示', { confirmButtonText: '确定', type: 'warning' })
    return false
  }
  return authStatus.username
}

const useDefaultMessages = () => {
  messagesData.value = [
    { id: 1, senderName: '我', senderTitle: '求职者', content: `您好，看了贵司的${props.jobTitle}职位，与我的经历非常匹配，希望详谈，感谢！`, time: '10:30', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' },
    { id: 2, senderName: props.currentJobHr?.name || '招聘者', senderTitle: props.currentJobHr?.title || 'HR', content: `您好，看了您的简历，觉得您很适合我们的${props.jobTitle}岗位，方便详细聊聊吗？`, time: '10:35', avatar: props.currentJobHr?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' }
  ]
}

const fetchChatHistory = async () => {
  try {
    loading.value = true
    const { username } = getAuthStatus()
    await fetchCompanyAdminUsername()
    const from = username
    const to = companyAdminUsername.value

    const response = await getChatHistory(from, to)
    if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
      messagesData.value = response.data.map((msg: any) => ({
        id: msg.id || Date.now(),
        senderName: msg.from === from ? '我' : props.currentJobHr?.name || '招聘者',
        senderTitle: msg.from === from ? '求职者' : props.currentJobHr?.title || 'HR',
        content: msg.content,
        time: new Date(msg.timestamp || Date.now()).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        avatar: msg.from === from ? 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' : props.currentJobHr?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }))
    } else {
      useDefaultMessages()
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error)
    useDefaultMessages()
  } finally {
    loading.value = false
  }
}

const connectWebSocket = () => {
  const currentUsername = checkUserLogin()
  if (!currentUsername) return
  message.value.from = currentUsername as string
  const wsUrl = `ws://127.0.0.1:8080/channel/echo?username=${currentUsername}`

  try {
    socket.value = new WebSocket(wsUrl)
    socket.value.onopen = () => {
      isConnected.value = true
      console.log('WebSocket连接已建立')
    }
    socket.value.onmessage = (event) => {
      handleIncomingMessage(event.data)
    }
    socket.value.onerror = (error) => {
      console.error('WebSocket连接错误:', error)
      isConnected.value = false
    }
    socket.value.onclose = () => {
      isConnected.value = false
      console.log('WebSocket连接已关闭')
    }
  } catch (error) {
    console.error('创建WebSocket连接失败:', error)
  }
}

const disconnectWebSocket = () => {
  if (socket.value) {
    socket.value.close()
    socket.value = null
  }
  isConnected.value = false
}

const handleIncomingMessage = (data: string) => {
  try {
    if (data.includes('连接成功') || data.includes('当前用户')) return
    let messageData: any
    try { messageData = JSON.parse(data) } catch { return }

    if (!messageData.from || !messageData.to || !messageData.content) return
    if (messageData.content === 'ping' || messageData.content === 'pong') return
    if (messageData.from === messageData.to) return

    const { from, to, content, sendTime } = messageData
    const currentUsername = getAuthStatus().username
    const isRelevantMessage = (from === currentUsername && to === companyAdminUsername.value) || (from === companyAdminUsername.value && to === currentUsername)

    if (!isRelevantMessage) return

    const newMessage = {
      id: Date.now(), 
      senderName: from === currentUsername ? '我' : props.currentJobHr?.name || '招聘者',
      senderTitle: from === currentUsername ? '求职者' : props.currentJobHr?.title || 'HR',
      content: content,
      time: new Date(sendTime || Date.now()).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      avatar: from === currentUsername ? 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' : props.currentJobHr?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }

    messagesData.value.push(newMessage)
    nextTick(scrollToBottom)
  } catch (error) {
    console.error('处理WebSocket消息失败:', error)
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const selectHr = (hrId: number) => {
  selectedHrId.value = hrId
}

const closeDialogue = () => {
  emit('close')
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  const currentUsername = checkUserLogin()
  if (!currentUsername) return

  if (!isConnected.value) {
    ElMessage.warning('WebSocket未连接，正在尝试重连...')
    connectWebSocket()
    return
  }

  try {
    message.value = {
      content: inputMessage.value.trim(),
      to: companyAdminUsername.value,
      from: currentUsername as string,
      id: Date.now(),
      sendTime: new Date().toISOString()
    }

    if (socket.value?.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(message.value))
    } else {
      throw new Error('WebSocket连接不可用')
    }

    inputMessage.value = ''
    nextTick(scrollToBottom)
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败: ' + (error as Error).message)
  }
}

const sendQuickReply = async () => {
  const quickMessage = `您好，看了贵司的${props.jobTitle}职位，与我的经历非常匹配，希望详谈，感谢！`
  const currentUsername = checkUserLogin()
  if (!currentUsername) return

  if (!isConnected.value) {
    ElMessage.warning('WebSocket未连接，正在尝试重连...')
    connectWebSocket()
    return
  }

  try {
    message.value = {
      content: quickMessage,
      to: companyAdminUsername.value,
      from: currentUsername as string,
      id: Date.now(),
      sendTime: new Date().toISOString()
    }

    if (socket.value?.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(message.value))
    } else {
      throw new Error('WebSocket连接不可用')
    }

    nextTick(scrollToBottom)
  } catch (error) {
    console.error('发送快捷回复失败:', error)
    ElMessage.error('发送快捷回复失败: ' + (error as Error).message)
  }
}

onMounted(() => {
  if (props.visible) {
    fetchChatHistory()
    fetchCompanyAdminUsername()
  }
})

onBeforeUnmount(() => {
  disconnectWebSocket()
})

watch(watchVisible, (newVal) => {
  if (newVal) {
    fetchChatHistory()
    fetchCompanyAdminUsername().then(() => {
      setTimeout(connectWebSocket, 1000)
    })
  } else {
    disconnectWebSocket()
  }
})
</script>

<style scoped>
/* ================= 容器与遮罩 ================= */
.dialogue-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.dialogue-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
}

.dialogue-content {
  position: relative;
  width: 1000px;
  height: 80vh;
  max-height: 80vh;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  overflow: hidden;
}

/* ================= 左侧：HR与职位信息 ================= */
.dialogue-left {
  width: 320px;
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* 同步头部渐变黑 */
.hr-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #111417 0%, #1a1f24 100%);
  color: #ffffff;
}

.hr-list-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.close-btn {
  color: #9ca3af;
  font-size: 20px;
  transition: all 0.3s;
}

.close-btn:hover {
  color: #ffffff;
  transform: scale(1.1);
}

.hr-info-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 24px;
  text-align: center;
  background: #f8fafc;
}

:deep(.custom-avatar) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border: 2px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.hr-avatar-large {
  margin-bottom: 24px;
}

.hr-details-large {
  margin-bottom: 24px;
}

.hr-name-large {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}

.hr-title-large {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.hr-company-large {
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.5;
}

/* 职位卡片浅橙色背景 */
.hr-job-info {
  margin-top: auto;
  padding: 14px 16px;
  background: #fff4ed;
  border-radius: 8px;
  border: 1px solid #ffedd5;
  width: 100%;
}

.job-title {
  font-size: 14px;
  color: #ea580c;
  font-weight: 600;
  text-align: center;
}

/* ================= 右侧：聊天主区域 ================= */
.dialogue-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: #ffffff;
}

.chat-hr-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hr-details .hr-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.hr-details .hr-title {
  font-size: 13px;
  color: #64748b;
}

/* 在线状态提示点 */
.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.status-dot.online {
  background-color: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}
.status-dot.offline {
  background-color: #ef4444;
}

.status-text {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

/* ================= 消息列表与气泡 ================= */
.message-list {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  max-width: 75%;
}

.message-item.from-me {
  margin-left: auto;
  justify-content: flex-end;
}

.message-item.from-other {
  margin-right: auto;
  justify-content: flex-start;
}

.message-content {
  padding: 12px 16px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

/* 自己的消息：浅橙色气泡 */
.message-item.from-me .message-content {
  background: #fff4ed;
  color: #111827;
  border: 1px solid #fdba74;
  border-radius: 12px 12px 0 12px;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.06);
}

/* 对端的消息：白底灰边气泡 */
.message-item.from-other .message-content {
  background: #ffffff;
  color: #111827;
  border: 1px solid #e2e8f0;
  border-radius: 12px 12px 12px 0;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 6px;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
}
.message-item.from-me .message-time {
  text-align: right;
  color: #9a3412; /* 更深的橙色时间以保证可读性 */
}
.message-item.from-other .message-time {
  text-align: right;
  color: #94a3b8;
}

/* 加载缺省 */
.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;
  font-size: 14px;
}

.loading-container .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: #f97316;
}

/* ================= 快捷回复区域 ================= */
.quick-reply {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  background: #ffffff;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reply-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.reply-content {
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.reply-content p {
  margin: 0;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.6;
}
.reply-content strong {
  color: #ea580c;
}

/* ================= 输入区域 ================= */
.input-section {
  padding: 16px 24px;
  background: #ffffff;
}

.input-container {
  display: flex;
  gap: 16px;
  align-items: flex-end;
}

.input-wrapper {
  flex: 1;
}

:deep(.custom-textarea .el-textarea__inner) {
  background-color: #f8fafc;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 14px;
  color: #111827;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

:deep(.custom-textarea .el-textarea__inner:hover) {
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.02), 0 0 0 1px #cbd5e1 !important;
}

:deep(.custom-textarea .el-textarea__inner:focus) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #f97316, 0 0 0 4px rgba(249, 115, 22, 0.15), inset 0 1px 2px rgba(0,0,0,0.02) !important;
}

/* ================= 主题按钮 ================= */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
  transition: all 0.3s ease;
}
.theme-btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(234, 88, 12, 0.4);
}
.theme-btn-primary:disabled {
  background: #e2e8f0 !important;
  box-shadow: none !important;
  color: #94a3b8 !important;
}

.theme-btn-primary-small {
  background: #fff4ed !important;
  border: 1px solid #fdba74 !important;
  color: #ea580c !important;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
}
.theme-btn-primary-small:hover {
  background: #f97316 !important;
  color: #ffffff !important;
  border-color: #f97316 !important;
}

.send-btn {
  height: 40px;
  padding: 0 24px;
}

.dialogue-footer {
  padding: 12px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}

.footer-text {
  font-size: 12px;
  color: #94a3b8;
}

/* ================= 滚动条美化 ================= */
.message-list::-webkit-scrollbar {
  width: 6px;
}
.message-list::-webkit-scrollbar-track {
  background: transparent;
}
.message-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
.message-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .dialogue-content {
    width: 95vw;
    max-height: 95vh;
  }
  .dialogue-left {
    display: none; /* 移动端隐藏左侧卡片 */
  }
}
</style>