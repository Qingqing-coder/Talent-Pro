// 模拟聊天数据
const mockChatData = {
  // 用户之间的聊天记录
  'user_002_hr_1': [
    {
      id: 1,
      from: 'user_002',
      to: 'hr_1',
      content: '您好，看了贵司的测试工程师职位，与我的经历非常匹配，希望详谈，感谢！',
      timestamp: Date.now() - 3600000, // 1小时前
      type: 'text'
    },
    {
      id: 2,
      from: 'hr_1',
      to: 'user_002',
      content: '您好，看了您的简历，觉得您很适合我们的测试工程师岗位，方便详细聊聊吗？',
      timestamp: Date.now() - 3500000, // 58分钟前
      type: 'text'
    },
    {
      id: 3,
      from: 'user_002',
      to: 'hr_1',
      content: '当然可以，我有多年的测试经验，熟悉自动化测试和性能测试',
      timestamp: Date.now() - 3400000, // 57分钟前
      type: 'text'
    }
  ],
  'user_002_hr_2': [
    {
      id: 1,
      from: 'user_002',
      to: 'hr_2',
      content: '您好，我对贵公司的前端开发岗位很感兴趣',
      timestamp: Date.now() - 86400000, // 1天前
      type: 'text'
    }
  ]
}

// 模拟API响应延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

// 模拟获取聊天历史记录
export async function mockGetChatHistory(from: string, to: string) {
  await delay(500) // 模拟网络延迟
  
  const key = `${from}_${to}`
  const reverseKey = `${to}_${from}`
  
  const messages = mockChatData[key] || mockChatData[reverseKey] || []
  
  return {
    code: 200,
    message: 'success',
    data: messages
  }
}

// 模拟发送消息
export async function mockSendMessage(data: {
  from: string
  to: string
  content: string
  type?: string
}) {
  await delay(300) // 模拟网络延迟
  
  const key = `${data.from}_${data.to}`
  const reverseKey = `${data.to}_${data.from}`
  
  const newMessage = {
    id: Date.now(),
    from: data.from,
    to: data.to,
    content: data.content,
    timestamp: Date.now(),
    type: data.type || 'text'
  }
  
  // 添加到模拟数据中
  if (mockChatData[key]) {
    mockChatData[key].push(newMessage)
  } else if (mockChatData[reverseKey]) {
    mockChatData[reverseKey].push(newMessage)
  } else {
    mockChatData[key] = [newMessage]
  }
  
  return {
    code: 200,
    message: 'success',
    data: newMessage
  }
}

// 模拟获取聊天列表
export async function mockGetChatList(userId: string) {
  await delay(400) // 模拟网络延迟
  
  const chatList = [
    {
      id: 1,
      name: '某先生',
      title: 'HR',
      company: '北京中创科软科技有限公司',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      lastMessage: '您好，看了您的简历，觉得您很适合我们的测试工程师岗位',
      lastTime: '刚刚',
      unreadCount: 0
    }
  ]
  
  return {
    code: 200,
    message: 'success',
    data: chatList
  }
} 