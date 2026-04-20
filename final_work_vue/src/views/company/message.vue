<template>
  <div class="speak-page">
    <div class="speak-container">
      <div class="message-list-section">
        <div class="section-header">
          <h2>用户消息</h2>
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
          <div v-else-if="userMessages.length === 0" class="empty-container">
            <div class="empty-state-wrapper">
              <div class="empty-icon-box">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <p>暂无用户消息</p>
            </div>
          </div>
          <div
            v-else
            v-for="user in userMessages"
            :key="user.userId"
            class="message-item"
            :class="{ active: selectedUser?.userId === user.userId }"
            @click="selectUser(user)"
          >
            <div class="user-avatar">
              <el-avatar :size="44" :src="user.avatar" class="custom-avatar">
                {{ (user.name || user.username).charAt(0).toUpperCase() }}
              </el-avatar>
            </div>
            <div class="user-info">
              <div class="user-name-row">
                <span class="user-name">{{ user.name || user.username }}</span>
                <span class="message-time">{{ formatTime(user.lastMessageTime) }}</span>
              </div>
              <div class="last-message">{{ user.lastMessage }}</div>
            </div>
            <div class="message-badge" v-if="user.unreadCount > 0">
              <el-badge :value="user.unreadCount" :max="99" class="custom-badge" />
            </div>
          </div>
        </div>
      </div>

      <div class="chat-section">
        <div v-if="selectedUser" class="chat-container">
          <div class="chat-header">
            <div class="chat-user-info">
              <el-avatar :size="36" :src="selectedUser.avatar" class="custom-avatar">
                {{ (selectedUser.name || selectedUser.username).charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ selectedUser.name || selectedUser.username }}</div>
              </div>
            </div>
            <div class="chat-actions">
              <el-button class="theme-btn-outline" size="small" @click="getUserResume" :loading="resumeLoading">
                <el-icon><Document /></el-icon>
                查看简历
              </el-button>
              </div>
          </div>

          <div class="chat-messages" ref="messagesContainer">
            <div class="messages-list">
              <div
                v-for="message in selectedUser.messages"
                :key="message.id"
                class="message-bubble"
                :class="{
                  'message-sent': message.isFromCompany,
                  'message-received': !message.isFromCompany
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
                placeholder="输入回复消息，按Enter发送..."
                @keydown.enter.prevent="sendMsg"
                resize="none"
                class="custom-textarea"
              />
              <div class="input-actions">
                <div class="connection-status">
                  <div class="status-dot" :class="isConnected ? 'online' : 'offline'"></div>
                  <span class="connection-info">
                    {{ isConnected ? '已连接' : '未连接' }} · {{ currentCompany }}
                  </span>
                </div>
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

    <el-dialog
      title="用户简历"
      v-model="resumeDialogVisible"
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      class="custom-dialog resume-dialog"
    >
      <div v-if="currentUserInfo" class="resume-content">
        <div class="resume-header">
          <h3>{{ getFieldValue('username') }} 的简历</h3>
        </div>

        <div class="resume-body">
          <div class="resume-section">
            <h4>基本信息</h4>
            <div class="resume-grid">
              <div class="resume-item">
                <span class="label">用户名：</span>
                <span class="value">{{ getFieldValue('username') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">昵称：</span>
                <span class="value">{{ getFieldValue('nickname') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">姓名：</span>
                <span class="value">{{ getFieldValue('name') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">性别：</span>
                <span class="value">{{getFieldValue('gender') == 2 ? '保密' : getFieldValue('gender') == 1 ? '男' : getFieldValue('gender') == 0 ? '女' : getFieldValue('gender') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">年龄：</span>
                <span class="value">{{ getFieldValue('age') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">电话：</span>
                <span class="value">{{ getFieldValue('phone') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">邮箱：</span>
                <span class="value">{{ getFieldValue('email') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">地址：</span>
                <span class="value">{{ getFieldValue('address') }}</span>
              </div>
            </div>
          </div>

          <div class="resume-section">
            <h4>教育背景</h4>
            <div class="resume-grid">
              <div class="resume-item">
                <span class="label">学历：</span>
                <span class="value">{{ getFieldValue('education') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">专业：</span>
                <span class="value">{{ getFieldValue('major') }}</span>
              </div>
            </div>
          </div>

          <div class="resume-section">
            <h4>工作信息</h4>
            <div class="resume-grid">
              <div class="resume-item">
                <span class="label">期望薪资：</span>
                <span class="value">{{ getFieldValue('salary') }}</span>
              </div>
              <div class="resume-item">
                <span class="label">工作经验：</span>
                <span class="value">{{ getFieldValue('experience') == 1 ? '有经验' : getFieldValue('experience') == 0 ? '无经验' : getFieldValue('experience') }}</span>
              </div>
            </div>
          </div>

          <div class="resume-section">
            <h4>自我介绍</h4>
            <div class="resume-item full-width">
              <span class="value">{{ getFieldValue('introduction') }}</span>
            </div>
          </div>

          <div v-if="getFilledFields().length === 0" class="resume-empty">
            <el-empty description="该用户暂无简历信息">
              <el-icon class="empty-icon"><Document /></el-icon>
            </el-empty>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="theme-btn-outline" @click="resumeDialogVisible = false">关闭</el-button>
          <el-button type="primary" class="theme-btn-primary" @click="resumeDialogVisible = false">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed, onBeforeUnmount, watch } from 'vue'
import { Refresh, Position, Delete, ChatDotRound, Document, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { showCustomMessage } from '@/utils/message'
import { getAuthStatus } from '@/utils/auth'
import { getChatHistory, getCompanyAllChatHistory } from '@/api/message'
import { getUserList } from '@/api/user'

// 消息格式（参考messageTest）
interface MessageFormat {
  id?: number
  from: string
  to: string
  content: string
  sendTime: string
}

// 验证消息格式（参考messageTest）
const isValidMessage = (data: any): boolean => {
  return (
    typeof data === 'object' &&
    typeof data.from === 'string' &&
    typeof data.to === 'string' &&
    typeof data.content === 'string' &&
    typeof data.sendTime === 'string'
  )
}

// 添加日志（参考messageTest的addLog方式）
const addLog = (msg: string) => {
  console.log(`[${new Date().toLocaleTimeString()}] ${msg}`)
}

// 响应式数据
const selectedUser = ref<any>(null)
const newMessage = ref('')
const messagesContainer = ref<HTMLElement>()
const loading = ref(false)
const resumeLoading = ref(false)
const resumeDialogVisible = ref(false)
const currentUserInfo = ref<any>(null)
const socket = ref<WebSocket | null>(null)
const isConnected = ref(false)
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5
const reconnectInterval = ref<number | null>(null)
const heartbeatInterval = ref<number | null>(null)
const heartbeatTimeout = 30000 // 30秒发送一次心跳

// 用户消息列表（从聊天记录中动态生成）
const userMessages = ref([] as any[])

// 批量补全用户name（根据username）
const fillUserNames = async (userList: any[]) => {
  for (const user of userList) {
    console.log('准备查name，user对象：', user);
    if (!user.name && user.username && !user._nameQueried) {
      try {
        console.log('实际传递给接口的username:', user.username);
        const res = await getUserList({ username: user.username, pageNum: 1, pageSize: 1 })
        let userInfo = null
        if (Array.isArray(res.data)) {
          userInfo = res.data[0]
        } else if (res.data && Array.isArray(res.data.list)) {
          userInfo = res.data.list[0]
        } else if (typeof res.data === 'object') {
          userInfo = res.data
        }
        if (userInfo && userInfo.name) {
          user.name = userInfo.name
        } else {
          user.name = user.username
        }
      } catch (e) {
        console.error('查找用户 name 失败:', user.username, e)
        user.name = user.username
      }
      user._nameQueried = true // 标记已查过
    }
  }
}

// 获取当前公司信息
const currentCompany = computed(() => {
  // 优先从localStorage获取公司用户名，这是公司登录时存储的
  const companyUsername = localStorage.getItem('companyUsername')
  if (companyUsername) {
    return companyUsername
  }

  // 如果没有找到公司用户名，则从认证状态获取
  const { username } = getAuthStatus()
  return username
})

// 监听消息变化，自动滚动到底部
const messagesCount = computed(() => {
  if (selectedUser.value && selectedUser.value.messages) {
    return selectedUser.value.messages.length
  }
  return 0
})

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动到底部
const watchMessages = () => {
  if (selectedUser.value && selectedUser.value.messages) {
    nextTick(() => {
      scrollToBottom()
    })
  }
}

// 格式化时间
const formatTime = (time: Date) => {
  const now = new Date()
  const diff = now.getTime() - time.getTime()

  if (diff < 1000 * 60) {
    return '刚刚'
  } else if (diff < 1000 * 60 * 60) {
    return `${Math.floor(diff / (1000 * 60))}分钟前`
  } else if (diff < 1000 * 60 * 60 * 24) {
    return `${Math.floor(diff / (1000 * 60 * 60))}小时前`
  } else {
    return time.toLocaleDateString()
  }
}



// 获取公司所有聊天记录
const getAllCompanyChatHistory = async () => {
  const companyUsername = currentCompany.value
  if (!companyUsername) {
    showCustomMessage('未找到公司用户名', 'error')
    return
  }

  try {
    // 调用API获取所有聊天记录
    const userList = await getCompanyAllChatHistory(companyUsername)

    // 过滤掉自己发送给自己的消息
    const filteredUserList = userList.filter((user: any) => {
      // 过滤掉用户名为当前公司用户名的记录
      if (user.username === companyUsername) {
        console.log('过滤掉自己发送给自己的消息:', user.username)
        return false
      }
      return true
    })

    // 批量补全name
    await fillUserNames(filteredUserList)

    // 按最新消息时间排序用户列表（最新的在前面）
    filteredUserList.sort((a: any, b: any) => {
      const timeA = new Date(a.lastMessageTime || 0).getTime()
      const timeB = new Date(b.lastMessageTime || 0).getTime()
      return timeB - timeA // 最新的在前面
    })

    // 更新用户消息列表
    userMessages.value = filteredUserList

    // if (filteredUserList.length > 0) {
    //   showCustomMessage(`聊天记录加载成功，共找到 ${filteredUserList.length} 个用户`, 'success')
    // } else {
    //   showCustomMessage('暂无聊天记录', 'info')
    // }
  } catch (error: any) {
    console.error('获取所有聊天记录失败:', error)

    // // 检查是否是"没有数据"的错误
    // if (error.message?.includes('没有数据') || error.response?.data?.message?.includes('没有数据')) {
    //   console.log('数据库中暂无聊天记录')
    //   showCustomMessage('暂无聊天记录', 'info')
    // } else {
    //   showCustomMessage('获取聊天记录失败: ' + (error.response?.data?.message || error.message), 'error')
    // }

    // 如果接口调用失败，不添加测试数据，直接显示空列表
    console.log('接口调用失败，显示空列表')
    userMessages.value = []
  }
}

// 加载用户聊天记录
const loadUserChatHistory = async (user: any) => {
  const companyUsername = currentCompany.value
  const userUsername = user.username

  try {
    console.log('加载用户聊天记录:', companyUsername, userUsername)
    const messages = await getChatHistory(companyUsername, userUsername)

    console.log('获取到的用户消息:', messages)

    // 处理消息数据，标记发送方，并过滤掉自己发送给自己的消息
    user.messages = messages
      .filter((msg: any) => {
        // 过滤掉发送方和接收方都是当前公司用户的消息
        if (msg.from === companyUsername && msg.to === companyUsername) {
          console.log('过滤掉自己发送给自己的消息:', msg)
          return false
        }
        return true
      })
      .map((msg: any) => ({
        ...msg,
        isFromCompany: msg.from === companyUsername,
        sendTime: new Date(msg.sendTime || msg.timestamp || Date.now())
      }))

    // 按时间排序消息（最新的在最后）
    sortUserMessages(user)

    // 更新最后一条消息
    if (messages.length > 0) {
      const lastMessage = messages[messages.length - 1]
      user.lastMessage = lastMessage.content
      user.lastMessageTime = new Date(lastMessage.sendTime || lastMessage.timestamp || Date.now())
    }
  } catch (error: any) {
    console.error('加载聊天记录失败:', error)
    showCustomMessage('加载聊天记录失败: ' + (error.response?.data?.message || error.message), 'error')
  }
}

// 清空聊天记录
const clearChat = async () => {
  if (!selectedUser.value) return

  try {
    await ElMessageBox.confirm('确定要清空与当前用户的聊天记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    selectedUser.value.messages = []
    showCustomMessage('聊天记录已清空', 'success')
  } catch (error: any) {
    if (error !== 'cancel') {
      showCustomMessage('清空聊天记录失败: ' + error.message, 'error')
    }
  }
}

// 刷新当前用户的消息列表
const refreshCurrentUserMessages = async () => {
  if (!selectedUser.value) {
    console.log('没有选中的用户，跳过刷新')
    return
  }

  console.log('=== 开始刷新当前用户消息 ===')
  console.log('当前用户:', selectedUser.value.username)

  try {
    // 重新加载当前用户的聊天记录
    await loadUserChatHistory(selectedUser.value)

    // 强制更新右侧聊天区域的显示
    nextTick(() => {
      if (selectedUser.value && selectedUser.value.messages) {
        selectedUser.value.messages = [...selectedUser.value.messages]
        scrollToBottom()
      }
    })

    console.log('当前用户消息刷新完成')
  } catch (error: any) {
    console.error('刷新当前用户消息失败:', error)
    showCustomMessage('刷新消息失败: ' + (error.response?.data?.message || error.message), 'error')
  }
}

// 刷新消息
const refreshMessages = async () => {
  console.log('=== 开始刷新消息 ===')
  loading.value = true

  try {
    // 清空当前选中的用户，避免状态混乱
    selectedUser.value = null

    // 获取所有聊天记录
    await getAllCompanyChatHistory()

    console.log('刷新完成，当前用户列表:', userMessages.value)
    showCustomMessage('消息已刷新', 'success')
  } catch (error: any) {
    console.error('刷新消息失败:', error)
    showCustomMessage('刷新消息失败: ' + (error.response?.data?.message || error.message), 'error')
  } finally {
    loading.value = false
    console.log('=== 刷新消息完成 ===')
  }
}

// 选择用户
const selectUser = async (user: any) => {
  selectedUser.value = user
  // 清除未读消息计数
  user.unreadCount = 0

  // 如果用户没有消息，尝试重新加载
  if (user.messages.length === 0) {
    await loadUserChatHistory(user)
  }

  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
}

// 连接WebSocket
const connectWebSocket = () => {
  const companyUsername = currentCompany.value
  if (!companyUsername) {
    ElMessageBox.alert('未找到公司用户名，无法连接WebSocket', '提示', {
      confirmButtonText: '确定',
      type: 'warning'
    })
    return
  }

  // 如果已经有连接，先关闭
  if (socket.value) {
    socket.value.close()
  }

  const wsUrl = `ws://127.0.0.1:8080/channel/echo?username=${companyUsername}`
  console.log('正在连接WebSocket:', wsUrl)
  console.log('公司用户名:', companyUsername)

  try {
    socket.value = new WebSocket(wsUrl)

    socket.value.onopen = () => {
      isConnected.value = true
      reconnectAttempts.value = 0 // 重置重连次数
      addLog('WebSocket连接已建立')
      // showCustomMessage('WebSocket连接成功', 'success')

      // 启动心跳机制
      startHeartbeat()
    }

    socket.value.onmessage = (event) => {
      console.log('收到WebSocket消息:', event.data)
      handleIncomingMessage(event.data)
    }

    socket.value.onerror = (error) => {
      console.error('WebSocket连接错误:', error)
      isConnected.value = false
      // showCustomMessage('WebSocket连接错误', 'error')
    }

    socket.value.onclose = (event) => {
      isConnected.value = false
      stopHeartbeat() // 停止心跳
      console.log('WebSocket连接已关闭，代码:', event.code, '原因:', event.reason)

      // 错误代码说明：
      // 1000: 正常关闭
      // 1011: 服务器错误或重启
      // 1006: 连接异常断开

      if (event.code === 1000) {
        // 正常关闭，不重连
        console.log('WebSocket正常关闭，不进行重连')
        // showCustomMessage('WebSocket连接已正常关闭', 'info')
      } else if (event.code === 1011) {
        // 服务器错误，延迟重连
        console.log('服务器错误，延迟重连')
        if (reconnectAttempts.value < maxReconnectAttempts) {
          const delay = Math.min(3000 * (reconnectAttempts.value + 1), 15000) // 递增延迟，最大15秒
          console.log(`WebSocket连接断开，${delay/1000}秒后尝试重连 (${reconnectAttempts.value + 1}/${maxReconnectAttempts})`)
          // showCustomMessage(`连接断开，${delay/1000}秒后重连`, 'warning')

          reconnectAttempts.value++
          reconnectInterval.value = setTimeout(() => {
            connectWebSocket()
          }, delay)
        } else {
          console.error('重连次数已达上限，请刷新页面')
        }
      } else {
        // 其他错误，正常重连
        if (reconnectAttempts.value < maxReconnectAttempts) {
          console.log(`WebSocket连接断开，${3}秒后尝试重连 (${reconnectAttempts.value + 1}/${maxReconnectAttempts})`)
          // showCustomMessage(`连接断开，${3}秒后重连`, 'warning')

          reconnectAttempts.value++
          reconnectInterval.value = setTimeout(() => {
            connectWebSocket()
          }, 3000)
        } else {
          // showCustomMessage('重连次数已达上限，请刷新页面', 'error')
        }
      }
    }
  } catch (error) {
    console.error('创建WebSocket连接失败:', error)
    // showCustomMessage('WebSocket连接创建失败', 'error')
  }
}

// 处理接收到的消息
const handleIncomingMessage = (data: string) => {
  try {
    console.log('=== 处理WebSocket新消息 ===')
    console.log('原始消息数据:', data)

    // 检查是否是连接成功消息（非JSON格式）
    if (data.includes('连接成功') || data.includes('当前用户')) {
      console.log('收到WebSocket连接成功消息:', data)
      return
    }

    // 尝试解析JSON消息
    let messageData: MessageFormat
    try {
      messageData = JSON.parse(data)
    } catch (parseError) {
      console.log('消息不是JSON格式，可能是系统消息:', data)
      return
    }

    // 验证消息格式
    if (!isValidMessage(messageData)) {
      console.log('消息格式无效，跳过处理:', messageData)
      return
    }

    const { from, to, content, sendTime, id } = messageData

    addLog('收到消息: ' + JSON.stringify(messageData))
    addLog(`消息ID: ${id}, 发送方: ${from}, 接收方: ${to}`)

    // 如果是心跳消息，不处理为聊天消息
    if (content === 'ping' || content === 'pong') {
      addLog('收到心跳消息，跳过处理')
      return
    }

    // 过滤掉自己发送给自己的消息
    if (from === to) {
      addLog('跳过自己发送给自己的消息')
      return
    }

    // 查找或创建用户记录
    // 如果是公司发送的消息，查找接收方用户
    // 如果是用户发送的消息，查找发送方用户
    const targetUsername = from === currentCompany.value ? to : from
    let user = userMessages.value.find(u => u.username === targetUsername)

    if (!user) {
      // 如果是新用户，创建用户记录
      console.log('创建新用户记录:', targetUsername)
      user = {
        userId: Date.now() + Math.random(),
        username: targetUsername,
        avatar: '',
        lastMessage: content,
        lastMessageTime: new Date(sendTime),
        unreadCount: 0,
        messages: []
      }
      userMessages.value.unshift(user)
    }

    // 创建消息对象
    const newMessage = {
      id: id || Date.now() + Math.random(), // 确保ID唯一
      from: from,
      to: to,
      content: content,
      sendTime: new Date(sendTime),
      isFromCompany: from === currentCompany.value
    }

    // 检查是否已存在相同ID或相同内容的消息，避免重复
    const existingMessage = user.messages.find((msg: any) =>
      msg.id === newMessage.id ||
      (msg.content === newMessage.content &&
        Math.abs(new Date(msg.sendTime).getTime() - new Date(newMessage.sendTime).getTime()) < 5000) // 5秒内的相同内容消息
    )
    if (existingMessage) {
      console.log('消息已存在，跳过重复添加:', newMessage.id, newMessage.content)
      return
    }

    // 添加消息到用户记录
    user.messages.push(newMessage)
    // 按时间排序消息（最新的在最后）
    sortUserMessages(user)
    user.lastMessage = content
    user.lastMessageTime = new Date(sendTime)

    console.log(`为用户 ${user.username} 添加新消息:`, content)
    console.log('消息来源:', newMessage.isFromCompany ? '公司' : '用户')

    // 更新未读消息计数
    if (selectedUser.value?.userId === user.userId) {
      user.unreadCount = 0
      console.log('当前用户正在聊天，清除未读计数')
      // 滚动到底部
      nextTick(() => {
        scrollToBottom()
      })

      // 强制更新右侧聊天区域的显示
      nextTick(() => {
        if (selectedUser.value && selectedUser.value.messages) {
          // 触发Vue的响应式更新
          selectedUser.value.messages = [...selectedUser.value.messages]
        }
      })
    } else {
      // 只有用户发送的消息才增加未读计数
      if (!newMessage.isFromCompany) {
        user.unreadCount = (user.unreadCount || 0) + 1
        console.log(`用户 ${user.username} 未读消息计数:`, user.unreadCount)
      }
    }

    // 将新消息的用户移到列表顶部
    moveUserToTop(user.userId)
    console.log('将用户移到列表顶部')

    console.log('=== WebSocket消息处理完成 ===')
  } catch (error) {
    console.error('处理WebSocket消息失败:', error)
    console.error('错误详情:', error)
  }
}

// 按时间排序用户消息列表（最新的在前面）
const sortUserMessages = (user: any) => {
  if (user.messages && user.messages.length > 0) {
    user.messages.sort((a: any, b: any) => {
      const timeA = new Date(a.sendTime).getTime()
      const timeB = new Date(b.sendTime).getTime()
      return timeA - timeB // 时间早的在前面，最新的在后面
    })
  }
}

// 将用户移到列表顶部（按最新消息时间排序）
const moveUserToTop = (userId: number) => {
  const userIndex = userMessages.value.findIndex(u => u.userId === userId)
  if (userIndex > 0) {
    const user = userMessages.value.splice(userIndex, 1)[0]
    userMessages.value.unshift(user)
  }

  // 按最新消息时间重新排序整个列表
  userMessages.value.sort((a, b) => {
    const timeA = new Date(a.lastMessageTime || 0).getTime()
    const timeB = new Date(b.lastMessageTime || 0).getTime()
    return timeB - timeA // 最新的在前面
  })
}

// 发送消息（参考messageTest的架构）
const sendMsg = async () => {
  if (!isConnected.value) {
    addLog('错误: 未连接到WebSocket')
    // showCustomMessage('WebSocket未连接，正在尝试重连...', 'warning')
    connectWebSocket()
    return
  }

  if (!newMessage.value.trim() || !selectedUser.value) {
    addLog('错误: 请输入消息内容并选择用户')
    // showCustomMessage('请输入消息内容并选择用户', 'warning')
    return
  }

  const companyUsername = currentCompany.value
  const userUsername = selectedUser.value.username

  // 生成唯一ID，避免重复
  const uniqueId = Date.now() + Math.random()

  // 参考messageTest的消息结构
  const messageToSend = {
    id: 1,
    from: companyUsername,
    to: userUsername,
    content: newMessage.value.trim(),
    sendTime: new Date().toISOString()
  }

  try {
    if (socket.value) {
      socket.value.send(JSON.stringify(messageToSend))
      addLog(`已发送消息: ${JSON.stringify(messageToSend)}`)
      addLog(`发送消息ID: ${messageToSend.id}`)
    } else {
      addLog('错误: WebSocket连接不存在')
      return
    }

    // 立即在本地添加消息到聊天记录
    const message = {
      id: uniqueId,
      from: companyUsername,
      to: userUsername,
      content: messageToSend.content,
      sendTime: new Date(messageToSend.sendTime),
      isFromCompany: true
    }

    // 确保选中的用户有消息数组
    if (!selectedUser.value.messages) {
      selectedUser.value.messages = []
    }

    // 检查是否已存在相同ID的消息，避免重复
    const existingMessage = selectedUser.value.messages.find((msg: any) => msg.id === message.id)
    if (existingMessage) {
      console.log('消息已存在，跳过重复添加:', message.id)
    } else {
      selectedUser.value.messages.push(message)
      // 按时间排序消息（最新的在最后）
      sortUserMessages(selectedUser.value)
    }
    selectedUser.value.lastMessage = messageToSend.content
    selectedUser.value.lastMessageTime = new Date(messageToSend.sendTime)

    // 清空输入框
    newMessage.value = ''

    // 滚动到底部
    nextTick(() => {
      scrollToBottom()
    })

    // 强制更新右侧聊天区域的显示
    nextTick(() => {
      if (selectedUser.value && selectedUser.value.messages) {
        // 触发Vue的响应式更新
        selectedUser.value.messages = [...selectedUser.value.messages]
      }
    })

    // showCustomMessage('消息发送成功', 'success')
  } catch (error: any) {
    addLog(`发送消息失败: ${error}`)
    showCustomMessage('消息发送失败: ' + error.message, 'error')
  }
}

// 手动重连WebSocket
const reconnectWebSocket = () => {
  console.log('手动重连WebSocket')
  disconnectWebSocket()
  setTimeout(() => {
    connectWebSocket()
  }, 1000)
}

// 启动心跳机制
const startHeartbeat = () => {
  // 清除之前的心跳定时器
  if (heartbeatInterval.value) {
    clearInterval(heartbeatInterval.value)
  }

  heartbeatInterval.value = setInterval(() => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      const companyUsername = currentCompany.value
      const heartbeatMessage = {
        id: Date.now(),
        from: companyUsername,
        to: 'server',
        content: 'ping',
        sendTime: new Date().toISOString()
      }

      try {
        socket.value.send(JSON.stringify(heartbeatMessage))
        addLog('发送心跳消息')
      } catch (error) {
        addLog('发送心跳消息失败: ' + error)
        // 心跳失败，尝试重连
        reconnectWebSocket()
      }
    } else {
      console.log('WebSocket未连接，停止心跳')
      stopHeartbeat()
    }
  }, heartbeatTimeout)

  console.log('心跳机制已启动，间隔:', heartbeatTimeout, 'ms')
}

// 停止心跳机制
const stopHeartbeat = () => {
  if (heartbeatInterval.value) {
    clearInterval(heartbeatInterval.value)
    heartbeatInterval.value = null
    console.log('心跳机制已停止')
  }
}

// 测试WebSocket连接
const testWebSocket = () => {
  const companyUsername = currentCompany.value
  if (!companyUsername) {
    showCustomMessage('未找到公司用户名', 'error')
    return
  }

  const testMessage = {
    id: Date.now(),
    from: companyUsername,
    to: 'test_user',
    content: '这是一条测试消息',
    sendTime: new Date().toISOString()
  }

  if (socket.value && socket.value.readyState === WebSocket.OPEN) {
    socket.value.send(JSON.stringify(testMessage))
    addLog('发送测试消息')
    // showCustomMessage('测试消息已发送', 'success')
  } else {
    showCustomMessage('WebSocket未连接，无法发送测试消息', 'error')
  }
}

// 测试用户信息查询（参考UserTable架构）
const testUserQuery = async () => {
  if (!selectedUser.value) {
    // showCustomMessage('请先选择一个用户', 'warning')
    return
  }

  try {
    const username = selectedUser.value.username
    console.log('=== 测试用户信息查询（参考UserTable架构） ===')
    console.log('查询用户名:', username)

    // 参考UserTable的查询参数结构
    const params: Record<string, any> = {
      username: username,
      pageNum: 1,
      pageSize: 10
    }

    // 清理空值参数（参考UserTable的处理方式）
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === undefined || params[key] === null) {
        delete params[key]
      }
    })

    console.log('查询参数:', params)
    const response = await getUserList(params)
    console.log('完整响应:', response)
    console.log('响应数据类型:', typeof response)
    console.log('响应数据结构:', Object.keys(response || {}))

    if (response && response.data) {
      console.log('data字段类型:', typeof response.data)
      console.log('data是否为数组:', Array.isArray(response.data))
      console.log('data内容:', response.data)

      // 参考UserTable的响应数据处理方式
      let userList: any[] = []

      if (Array.isArray(response.data)) {
        userList = response.data
      } else if (response.data.list && Array.isArray(response.data.list)) {
        userList = response.data.list
      } else if (typeof response.data === 'object') {
        userList = [response.data]
      }

      console.log('处理后的用户列表:', userList)
      console.log('用户列表长度:', userList.length)

      if (userList.length > 0) {
        console.log('第一个用户信息:', userList[0])
        console.log('用户字段列表:', Object.keys(userList[0] || {}))
      }
    }

    console.log('=== 测试完成 ===')
    // showCustomMessage('测试查询完成，请查看控制台日志', 'success')
  } catch (error: any) {
    console.error('测试查询失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '网络请求失败'
    // showCustomMessage('测试查询失败: ' + errorMsg, 'error')
  }
}

// 获取用户简历信息（参考UserTable架构）
const getUserResume = async () => {
  if (!selectedUser.value) {
    // showCustomMessage('请先选择一个用户', 'warning')
    return
  }

  resumeLoading.value = true

  try {
    const username = selectedUser.value.username
    console.log('获取用户简历信息:', username)

    // 参考UserTable的查询参数结构
    const params: Record<string, any> = {
      username: username, // 用户名条件查询
      pageNum: 1,
      pageSize: 10
    }

    // 清理空值参数（参考UserTable的处理方式）
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === undefined || params[key] === null) {
        delete params[key]
      }
    })

    console.log('查询参数:', params)
    const response = await getUserList(params)
    console.log('用户信息响应:', response)

    // 参考UserTable的响应数据处理方式
    if (response && response.data) {
      // 处理不同的响应数据结构
      let userList: any[] = []

      if (Array.isArray(response.data)) {
        // 直接是数组
        userList = response.data
      } else if (response.data.list && Array.isArray(response.data.list)) {
        // 包含list字段的响应
        userList = response.data.list
      } else if (typeof response.data === 'object') {
        // 单个对象
        userList = [response.data]
      }

      console.log('处理后的用户列表:', userList)

      if (userList.length > 0) {
        // 找到匹配的用户（优先匹配username字段）
        const userInfo = userList.find((user: any) =>
          user.username === username ||
          user.name === username ||
          user.userName === username
        ) || userList[0]

        console.log('找到的用户信息:', userInfo)

        // 显示用户简历信息
        showUserResumeDialog(userInfo)
      } else {
        showCustomMessage(`未找到用户 "${username}" 的简历信息`, 'warning')
      }
    } else {
      showCustomMessage('API返回数据格式异常', 'error')
    }
  } catch (error: any) {
    console.error('获取用户简历失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '网络请求失败'
    showCustomMessage('获取用户简历失败: ' + errorMsg, 'error')
  } finally {
    resumeLoading.value = false
  }
}

// 显示用户简历对话框（使用Dialog弹窗）
const showUserResumeDialog = (userInfo: any) => {
  console.log('显示用户简历，原始数据:', userInfo)
  currentUserInfo.value = userInfo
  resumeDialogVisible.value = true
}

// 获取字段值的函数
const getFieldValue = (fieldName: string) => {
  if (!currentUserInfo.value) return '未填写'

  // 参考UserTable的字段结构进行映射
  const fieldMapping: any = {
    username: ['username', 'name', 'userName', 'realName'],
    nickname: ['nickname', 'nickName'],
    email: ['email', 'mail', 'emailAddress'],
    phone: ['phone', 'telephone', 'tel', 'phoneNumber', 'mobile'],
    gender: ['sex', 'gender'], // UserTable中使用sex字段，1=男，0=女
    age: ['age'],
    education: ['eduBack', 'education', 'degree', 'edu', 'educationLevel'], // UserTable中使用eduBack
    major: ['major', 'specialty', 'profession', 'specialization'],
    salary: ['salaryWishK', 'salary', 'expectedSalary'], // UserTable中的期望薪资
    address: ['address'],
    experience: ['hasExperience', 'experience', 'workExperience', 'work', 'workYears'], // UserTable中使用hasExperience，1=有经验，0=无经验
    introduction: ['introduction', 'selfIntro', 'description', 'desc', 'aboutMe']
  }

  const possibleFields = fieldMapping[fieldName] || [fieldName]
  for (const field of possibleFields) {
    if (currentUserInfo.value[field] !== undefined && currentUserInfo.value[field] !== null && currentUserInfo.value[field] !== '') {
      console.log(`找到字段 ${fieldName}: ${currentUserInfo.value[field]} (来源字段: ${field})`)
      return currentUserInfo.value[field]
    }
  }
  console.log(`字段 ${fieldName} 未找到或为空`)
  return '未填写'
}

// 获取所有已填写的字段
const getFilledFields = () => {
  if (!currentUserInfo.value) return []

  const fieldMapping: any = {
    username: ['username', 'name', 'userName', 'realName'],
    nickname: ['nickname', 'nickName'],
    email: ['email', 'mail', 'emailAddress'],
    phone: ['phone', 'telephone', 'tel', 'phoneNumber', 'mobile'],
    gender: ['sex', 'gender'], // 1=男，0=女
    age: ['age'],
    education: ['eduBack', 'education', 'degree', 'edu', 'educationLevel'],
    major: ['major', 'specialty', 'profession', 'specialization'],
    salary: ['salaryWishK', 'salary', 'expectedSalary'],
    address: ['address'],
    experience: ['hasExperience', 'experience', 'workExperience', 'work', 'workYears'], // 1=有经验，0=无经验
    introduction: ['introduction', 'selfIntro', 'description', 'desc', 'aboutMe']
  }

  const availableFields = Object.keys(fieldMapping)
  return availableFields.filter(field => getFieldValue(field) !== '未填写')
}

// 断开WebSocket连接
const disconnectWebSocket = () => {
  // 停止心跳机制
  stopHeartbeat()

  // 清除重连定时器
  if (reconnectInterval.value) {
    clearTimeout(reconnectInterval.value)
    reconnectInterval.value = null
  }

  // 重置重连次数
  reconnectAttempts.value = 0

  // 关闭WebSocket连接
  if (socket.value) {
    socket.value.close(1000, '主动断开连接')
    socket.value = null
  }

  isConnected.value = false
  console.log('WebSocket连接已主动断开')
}

// 监听选中用户的消息变化，确保按时间排序
watch(() => selectedUser.value?.messages, (newMessages) => {
  if (selectedUser.value && newMessages && newMessages.length > 0) {
    sortUserMessages(selectedUser.value)
    // 确保右侧聊天区域立即更新
    nextTick(() => {
      scrollToBottom()
    })
  }
}, { deep: true })

// 监听选中用户的变化，确保切换用户时能正确显示消息
watch(() => selectedUser.value?.userId, (newUserId, oldUserId) => {
  if (newUserId && newUserId !== oldUserId) {
    nextTick(() => {
      scrollToBottom()
    })
  }
})

// 组件挂载时
onMounted(() => {
  // 调试信息：显示当前公司用户名
  console.log('=== 组件挂载调试信息 ===')
  console.log('localStorage companyUsername:', localStorage.getItem('companyUsername'))
  console.log('getAuthStatus username:', getAuthStatus().username)
  console.log('computed currentCompany:', currentCompany.value)
  console.log('=== 调试信息结束 ===')

  // 延迟连接WebSocket，确保页面完全加载
  setTimeout(() => {
    connectWebSocket()
  }, 1000)

  // 加载所有聊天记录
  getAllCompanyChatHistory()

  // 添加页面可见性监听，当页面重新可见时检查连接
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden && !isConnected.value) {
      console.log('页面重新可见，检查WebSocket连接')
      setTimeout(() => {
        if (!isConnected.value) {
          console.log('WebSocket未连接，尝试重连')
          connectWebSocket()
        }
      }, 1000)
    }
  })

  // 监听消息变化，自动滚动到底部
  if (selectedUser.value) {
    watchMessages()
  }
})

// 组件卸载时
onBeforeUnmount(() => {
  disconnectWebSocket()
})
</script>

<style scoped>
/* ================= 全局与容器 ================= */
.speak-page {
  padding: 24px;
  min-height: calc(100vh - 120px);
  background: #f5f7fa; /* 统一浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.speak-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  display: flex;
  height: calc(100vh - 160px);
  overflow: hidden;
  border: 1px solid #f1f5f9;
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

/* 选中项的橙色主题 */
.message-item.active {
  background-color: #fff4ed;
  border-left: 3px solid #f97316;
}

.user-avatar {
  position: relative;
  margin-right: 12px;
}

:deep(.custom-avatar) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #64748b;
  font-weight: bold;
}

.user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-weight: 600;
  color: #111827;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-time {
  color: #94a3b8;
  font-size: 11px;
}

.last-message {
  color: #64748b;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 消息徽标活力橙色 */
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

.chat-user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 12px;
}

.user-details .user-name {
  font-weight: 700;
  color: #111827;
  font-size: 16px;
}

.chat-actions {
  display: flex;
  gap: 8px;
}

/* 聊天消息流 */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f8fafc; /* 对话区内底色浅岩灰 */
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

/* 发送方的消息：活力橙渐变 */
.message-sent .message-content {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #ffffff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.2);
}

/* 接收方的消息：纯白底岩灰边 */
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

/* 自定义文本域 */
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
  justify-content: space-between;
  align-items: center;
}

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

.connection-info {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* ================= 缺省状态 ================= */
.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.empty-state-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #94a3b8;
}

.empty-icon-box {
  width: 80px;
  height: 80px;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
  font-size: 36px;
  color: #cbd5e1;
}

/* ================= 按钮统一主题 ================= */
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
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.theme-btn-outline {
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #4b5563 !important;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
}
.theme-btn-outline:hover {
  border-color: #f97316 !important;
  color: #ea580c !important;
  background: #fff4ed !important;
}

/* ================= 简历弹窗 ================= */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
}
:deep(.custom-dialog .el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}
:deep(.custom-dialog .el-dialog__title) {
  font-weight: 700;
  color: #111827;
}

.resume-header {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
}

.resume-header h3 {
  margin: 0;
  color: #111827;
  font-size: 20px;
  font-weight: 700;
}

.resume-body {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 10px;
}

.resume-section {
  margin-bottom: 24px;
}

.resume-section h4 {
  margin: 0 0 16px 0;
  color: #111827;
  font-size: 16px;
  font-weight: 700;
  padding-left: 10px;
  border-left: 4px solid #f97316; /* 橙色指示条 */
}

.resume-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.resume-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.resume-item.full-width {
  grid-column: 1 / -1;
}

.resume-item .label {
  font-weight: 600;
  color: #64748b;
  min-width: 80px;
  margin-right: 8px;
  flex-shrink: 0;
}

.resume-item .value {
  color: #111827;
  font-weight: 500;
  flex: 1;
  word-break: break-word;
}

/* 滚动条美化 */
.message-list::-webkit-scrollbar,
.chat-messages::-webkit-scrollbar,
.resume-body::-webkit-scrollbar {
  width: 6px;
}
.message-list::-webkit-scrollbar-track,
.chat-messages::-webkit-scrollbar-track,
.resume-body::-webkit-scrollbar-track {
  background: transparent;
}
.message-list::-webkit-scrollbar-thumb,
.chat-messages::-webkit-scrollbar-thumb,
.resume-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
.message-list::-webkit-scrollbar-thumb:hover,
.chat-messages::-webkit-scrollbar-thumb:hover,
.resume-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* ================= 响应式 ================= */
@media (max-width: 768px) {
  .speak-container {
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
  .resume-grid {
    grid-template-columns: 1fr;
  }
  :deep(.custom-dialog) {
    width: 95% !important;
  }
}
</style>