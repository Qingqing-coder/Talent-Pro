<template>
  <div class="talk-page">
    <div class="talk-container">
      <div class="message-list-section">
        <div class="section-header">
          <h2>公司消息</h2>
          <div class="header-actions">
            <el-button type="primary" size="small" class="theme-btn-primary-small" @click="refreshMessages" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
        <div class="message-list">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="companyMessages.length === 0" class="empty-container">
            <div class="empty-state-wrapper">
              <div class="empty-icon-box">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <p>暂无公司消息</p>
            </div>
          </div>
          <div
            v-else
            v-for="company in companyMessages"
            :key="company.companyId"
            class="message-item"
            :class="{ active: selectedCompany?.companyId === company.companyId }"
            @click="selectCompany(company)"
          >
            <div class="company-avatar">
              <el-avatar :size="44" :src="company.avatar" class="custom-avatar">
                {{ (company.name || company.username).charAt(0).toUpperCase() }}
              </el-avatar>
            </div>
            <div class="company-info">
              <div class="company-name-row">
                <span class="company-name">{{ company.nickname || company.username }}</span>
                <span class="message-time">{{ formatTime(company.lastMessageTime) }}</span>
              </div>
              <div class="last-message">{{ company.lastMessage }}</div>
            </div>
            <div class="message-badge" v-if="company.unreadCount > 0">
              <el-badge :value="company.unreadCount" :max="99" class="custom-badge" />
            </div>
          </div>
        </div>
      </div>
      <div class="chat-section">
        <div v-if="selectedCompany" class="chat-container">
          <div class="chat-header">
            <div class="chat-company-info">
              <el-avatar :size="36" :src="selectedCompany.avatar" class="custom-avatar">
                {{ (selectedCompany.name || selectedCompany.username).charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="company-details">
                <div class="company-name">{{ selectedCompany.nickname || selectedCompany.username }}</div>
              </div>
            </div>
            <div class="chat-actions">
              <div class="connection-status">
                <div class="status-dot" :class="isConnected ? 'online' : 'offline'"></div>
                <span class="status-text">{{ isConnected ? '已连接' : '未连接' }}</span>
              </div>
            </div>
          </div>
          <div class="chat-messages" ref="messagesContainer">
            <div class="messages-list">
              <div
                v-for="message in selectedCompany.messages"
                :key="message.id"
                class="message-bubble"
                :class="{
                  'message-sent': message.isFromUser,
                  'message-received': !message.isFromUser
                }"
              >
                <div class="message-content">
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-time">{{ formatTime(message.sendTime) }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <div class="input-container">
              <el-input
                v-model="newMessage"
                type="textarea"
                :rows="3"
                placeholder="输入回复消息，按Enter键发送..."
                @keydown.enter.prevent="sendMsg"
                resize="none"
                class="custom-textarea"
              />
              <div class="input-actions">
                <el-button type="primary" class="theme-btn-primary" @click="sendMsg" :disabled="!newMessage.trim() || !isConnected">
                  <el-icon><Position /></el-icon>
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-chat-selected">
          <div class="empty-state-wrapper">
            <div class="empty-icon-box">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <p>请选择左侧联系人开始沟通</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
import { Refresh, Position, ChatDotRound } from '@element-plus/icons-vue'
import { getAuthStatus } from '@/utils/auth'
import { getChatHistory, getChatByFrom, getChatByTo } from '@/api/message'
import { sendMessage } from '@/api/chat'
import { getComList } from '@/api/company'
import { showCustomMessage } from '@/utils/message'

// 响应式数据
const companyMessages = ref<any[]>([])
const selectedCompany = ref<any>(null)
const newMessage = ref('')
const messagesContainer = ref<HTMLElement>()
const loading = ref(false)
const isConnected = ref(false)
const socket = ref<WebSocket | null>(null)

// 获取当前用户信息
const currentUser = computed(() => {
  return getAuthStatus().username
})

// 格式化时间
const formatTime = (time: Date) => {
  const now = new Date()
  const t = new Date(time)
  const diff = now.getTime() - t.getTime()
  if (diff < 1000 * 60) {
    return '刚刚'
  } else if (diff < 1000 * 60 * 60) {
    return `${Math.floor(diff / (1000 * 60))}分钟前`
  } else if (diff < 1000 * 60 * 60 * 24) {
    return `${Math.floor(diff / (1000 * 60 * 60))}小时前`
  } else {
    return t.toLocaleDateString()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 批量补全公司nickname
const fillCompanyNicknames = async (companyList: any[]) => {
  await Promise.all(companyList.map(async (company) => {
    if (!company.nickname && company.username) {
      try {
        const res = await getComList({ username: company.username, pageNum: 1, pageSize: 1 })
        let info = null
        if (Array.isArray(res.data?.list)) {
          info = res.data.list[0]
        } else if (Array.isArray(res.data)) {
          info = res.data[0]
        } else if (typeof res.data === 'object') {
          info = res.data
        }
        if (info && info.nickname) {
          company.nickname = info.nickname
        } else {
          company.nickname = company.username
        }
      } catch {
        company.nickname = company.username
      }
    }
  }))
}

// 获取公司消息列表（聚合from和to）
const getAllCompanyChatList = async () => {
  loading.value = true
  try {
    const userId = currentUser.value
    // 获取用户作为发送者和接收者的所有消息
    const [fromRes, toRes] = await Promise.all([
      getChatByFrom(userId),
      getChatByTo(userId)
    ])
    const allMessages = [...(fromRes.data || []), ...(toRes.data || [])]
    // 聚合所有公司
    const companyMap = new Map()
    allMessages.forEach((msg: any) => {
      const companyId = msg.from === userId ? msg.to : msg.from
      if (!companyMap.has(companyId)) {
        companyMap.set(companyId, {
          companyId,
          username: companyId,
          name: companyId,
          avatar: '',
          messages: [],
          lastMessage: '',
          lastMessageTime: null,
          unreadCount: 0,
          nickname: ''
        })
      }
      const company = companyMap.get(companyId)
      company.messages.push({
        ...msg,
        isFromUser: msg.from === userId,
        sendTime: new Date(msg.sendTime || msg.timestamp || Date.now())
      })
    })
    // 处理最后一条消息和时间
    companyMap.forEach((company) => {
      if (company.messages.length > 0) {
        company.messages.sort((a: any, b: any) => a.sendTime - b.sendTime)
        company.lastMessage = company.messages[company.messages.length - 1].content
        company.lastMessageTime = company.messages[company.messages.length - 1].sendTime
      }
    })
    // 转为数组并按最后消息时间排序
    const arr = Array.from(companyMap.values()).sort((a, b) => {
      if (!a.lastMessageTime) return 1
      if (!b.lastMessageTime) return -1
      return b.lastMessageTime - a.lastMessageTime
    })
    await fillCompanyNicknames(arr)
    companyMessages.value = arr
  } catch (e) {
    showCustomMessage('获取公司消息列表失败', 'error')
    companyMessages.value = []
  } finally {
    loading.value = false
  }
}

// 加载与公司聊天记录
const loadCompanyChatHistory = async (company: any) => {
  try {
    const userId = currentUser.value
    const companyId = company.username
    const res = await getChatHistory(userId, companyId)
    // [{ id, from, to, content, sendTime }]
    company.messages = (res || []).map((msg: any) => ({
      ...msg,
      isFromUser: msg.from === userId,
      sendTime: new Date(msg.sendTime || msg.timestamp || Date.now())
    }))
    company.messages.sort((a: any, b: any) => a.sendTime - b.sendTime)
    if (company.messages.length > 0) {
      company.lastMessage = company.messages[company.messages.length - 1].content
      company.lastMessageTime = company.messages[company.messages.length - 1].sendTime
    }
  } catch (e) {
    company.messages = []
  }
}

// 选择公司
const selectCompany = async (company: any) => {
  selectedCompany.value = company
  company.unreadCount = 0
  if (!company.messages || company.messages.length === 0) {
    await loadCompanyChatHistory(company)
  }
  scrollToBottom()
}

// 刷新消息
const refreshMessages = async () => {
  await getAllCompanyChatList()
  if (selectedCompany.value) {
    await loadCompanyChatHistory(selectedCompany.value)
    scrollToBottom()
  }
}

// WebSocket连接
const connectWebSocket = () => {
  const userId = currentUser.value
  if (!userId) return
  if (socket.value) {
    socket.value.close()
  }
  const wsUrl = `ws://127.0.0.1:8080/channel/echo?username=${userId}`
  try {
    socket.value = new WebSocket(wsUrl)
    socket.value.onopen = () => {
      isConnected.value = true
    }
    socket.value.onmessage = (event) => {
      handleIncomingMessage(event.data)
    }
    socket.value.onerror = () => {
      isConnected.value = false
    }
    socket.value.onclose = () => {
      isConnected.value = false
    }
  } catch (e) {
    isConnected.value = false
  }
}

// 断开WebSocket
const disconnectWebSocket = () => {
  if (socket.value) {
    socket.value.close()
    socket.value = null
  }
  isConnected.value = false
}

// 处理接收到的消息
const handleIncomingMessage = (data: string) => {
  try {
    if (data.includes('连接成功') || data.includes('当前用户')) return
    let messageData: any
    try {
      messageData = JSON.parse(data)
    } catch {
      return
    }
    if (!messageData.from || !messageData.to || !messageData.content) return
    if (messageData.content === 'ping' || messageData.content === 'pong') return
    if (messageData.from === messageData.to) return
    const { from, to, content, sendTime } = messageData
    const userId = currentUser.value
    // 找到对应公司
    const companyId = from === userId ? to : from
    let company = companyMessages.value.find(c => c.username === companyId)
    if (!company) {
      company = {
        companyId: companyId,
        username: companyId,
        name: companyId,
        avatar: '',
        messages: [],
        lastMessage: '',
        lastMessageTime: new Date(),
        unreadCount: 0,
        nickname: ''
      }
      companyMessages.value.unshift(company)
    }
    const newMessage = {
      id: Date.now() + Math.random(),
      from,
      to,
      content,
      sendTime: new Date(sendTime || Date.now()),
      isFromUser: from === userId
    }
    // 避免重复
    if (!company.messages.find((msg: any) => msg.content === newMessage.content && Math.abs(new Date(msg.sendTime).getTime() - newMessage.sendTime.getTime()) < 5000)) {
      company.messages.push(newMessage)
      company.messages.sort((a: any, b: any) => a.sendTime - b.sendTime)
      company.lastMessage = newMessage.content
      company.lastMessageTime = newMessage.sendTime
      if (selectedCompany.value?.companyId === company.companyId) {
        company.unreadCount = 0
        scrollToBottom()
      } else {
        company.unreadCount = (company.unreadCount || 0) + 1
      }
      // 将公司移到顶部
      const idx = companyMessages.value.findIndex(c => c.companyId === company.companyId)
      if (idx > 0) {
        const c = companyMessages.value.splice(idx, 1)[0]
        companyMessages.value.unshift(c)
      }
    }
  } catch (e) {}
}

// 发送消息
const sendMsg = async () => {
  if (!isConnected.value || !selectedCompany.value || !newMessage.value.trim()) return
  const userId = currentUser.value
  const companyId = selectedCompany.value.username
  const msgObj = {
    from: userId,
    to: companyId,
    content: newMessage.value.trim(),
    sendTime: new Date().toISOString()
  }
  try {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(msgObj))
    }
    // 本地添加
    const localMsg = {
      ...msgObj,
      id: Date.now() + Math.random(),
      isFromUser: true,
      sendTime: new Date()
    }
    selectedCompany.value.messages.push(localMsg)
    selectedCompany.value.lastMessage = localMsg.content
    selectedCompany.value.lastMessageTime = localMsg.sendTime
    newMessage.value = ''
    scrollToBottom()
  } catch (e) {
    showCustomMessage('发送消息失败', 'error')
  }
}

// 生命周期
onMounted(() => {
  getAllCompanyChatList()
  setTimeout(connectWebSocket, 1000)
})
onBeforeUnmount(() => {
  disconnectWebSocket()
})
// 监听选中公司消息变化自动滚动
watch(() => selectedCompany.value?.messages, () => {
  scrollToBottom()
}, { deep: true })
</script>

<style scoped>
/* ================= 全局与容器 ================= */
.talk-page {
  padding: 24px;
  min-height: calc(100vh - 120px);
  background: #f5f7fa; /* 统一极简浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.talk-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  display: flex;
  height: calc(100vh - 160px);
  overflow: hidden;
}

/* ================= 左侧消息列表 ================= */
.message-list-section {
  width: 280px;
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.section-header {
  padding: 20px 16px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
}

.section-header h2 {
  margin: 0;
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
}

.loading-container,
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;
}

.empty-state-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.empty-icon-box {
  width: 64px;
  height: 64px;
  background: #f8fafc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.02);
  margin-bottom: 16px;
  font-size: 28px;
  color: #cbd5e1;
}

.message-item {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid #f8fafc;
  position: relative;
}

.message-item:hover {
  background-color: #f8fafc;
}

/* 选中态：浅橙色背景与左侧橙色指示条 */
.message-item.active {
  background-color: #fff4ed;
  border-left: 3px solid #f97316;
}

.company-avatar {
  position: relative;
  margin-right: 12px;
}

:deep(.custom-avatar) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #64748b;
  font-weight: bold;
}

.company-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.company-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.company-name {
  font-weight: 600;
  color: #111827;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.last-message {
  color: #64748b;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  color: #94a3b8;
  font-size: 11px;
}

.message-badge {
  margin-left: 8px;
}

/* 修改未读角标为活力橙 */
:deep(.custom-badge .el-badge__content) {
  background-color: #ea580c;
  border: none;
}

/* ================= 右侧聊天区域 ================= */
.chat-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
}

.chat-company-info {
  display: flex;
  align-items: center;
}

.company-details {
  margin-left: 12px;
}

.company-details .company-name {
  font-weight: 700;
  color: #111827;
  font-size: 16px;
  margin-bottom: 2px;
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

/* ================= 聊天消息区 ================= */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f8fafc;
  min-height: 0;
}

.messages-list {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  padding-bottom: 10px;
}

.message-bubble {
  margin-bottom: 20px;
  display: flex;
}

.message-bubble.message-sent {
  justify-content: flex-end;
}

.message-bubble.message-received {
  justify-content: flex-start;
}

.message-content {
  max-width: 65%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

/* 自己发送的消息（活力橙渐变） */
.message-sent .message-content {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #ffffff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.2);
}

/* 接收到的消息（白底灰边） */
.message-received .message-content {
  background: #ffffff;
  color: #111827;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
}

.message-text {
  margin-bottom: 6px;
  line-height: 1.5;
  font-size: 14px;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  text-align: right;
  opacity: 0.7;
}

.message-received .message-time {
  color: #94a3b8;
}

/* ================= 聊天输入区域 ================= */
.chat-input {
  padding: 20px 24px;
  border-top: 1px solid #f1f5f9;
  background: #ffffff;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 输入框定制 */
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

.input-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* ================= 按钮与缺省状态 ================= */
.theme-btn-primary {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
  padding: 8px 20px;
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

.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

/* ================= 滚动条美化 ================= */
.message-list::-webkit-scrollbar,
.chat-messages::-webkit-scrollbar {
  width: 6px;
}
.message-list::-webkit-scrollbar-track,
.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}
.message-list::-webkit-scrollbar-thumb,
.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
.message-list::-webkit-scrollbar-thumb:hover,
.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .talk-container {
    flex-direction: column;
    height: auto;
  }
  .message-list-section {
    width: 100%;
    height: 300px;
  }
  .chat-section {
    height: 450px;
  }
}
</style>