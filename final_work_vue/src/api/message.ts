import request from '@/utils/request'

// 根据发送者查找聊天记录
export const getChatByFrom = (from: string) => {
  console.log('调用 getChatByFrom，参数 from:', from)
  return request.get('/chat/selectByFrom', {
    params: { from }
  })
}

// 根据接收者查找聊天记录
export const getChatByTo = (to: string) => {
  console.log('调用 getChatByTo，参数 to:', to)
  return request.get('/chat/selectByTo', {
    params: { to }
  })
}

// 获取两个用户之间的聊天记录
export const getChatHistory = async (from: string, to: string) => {
  try {
    // 获取发送者发送的消息
    const fromResponse = await getChatByFrom(from)
    
    // 获取接收者接收的消息
    const toResponse = await getChatByTo(to)
    
    // 合并两个接口的数据
    const fromMessages = fromResponse.data || []
    const toMessages = toResponse.data || []
    
    // 过滤出与指定用户的聊天记录
    const filteredMessages = [
      ...fromMessages.filter((msg: any) => msg.to === to),
      ...toMessages.filter((msg: any) => msg.from === from)
    ]
    
    // 按时间排序
    filteredMessages.sort((a: any, b: any) => {
      const timeA = new Date(a.sendTime || a.timestamp || Date.now()).getTime()
      const timeB = new Date(b.sendTime || b.timestamp || Date.now()).getTime()
      return timeA - timeB
    })
    
    return filteredMessages
  } catch (error: any) {
    console.error('获取聊天记录失败:', error)
    throw error
  }
}

// 获取公司所有聊天记录
export const getCompanyAllChatHistory = async (companyUsername: string) => {
  try {
    console.log('=== 开始获取公司聊天记录 ===')
    console.log('公司用户名:', companyUsername)
    
    let fromMessages: any[] = []
    let toMessages: any[] = []
    
    try {
      // 获取公司作为发送者的消息
      console.log('正在调用 /chat/selectByFrom 接口...')
      const fromResponse = await getChatByFrom(companyUsername)
      console.log('selectByFrom 接口响应:', fromResponse)
      fromMessages = fromResponse.data || []
    } catch (fromError: any) {
      console.log('selectByFrom 接口调用失败:', fromError)
      if (fromError.message?.includes('没有数据')) {
        console.log('selectByFrom 接口返回没有数据，这是正常的')
      }
    }
    
    try {
      // 获取公司作为接收者的消息
      console.log('正在调用 /chat/selectByTo 接口...')
      const toResponse = await getChatByTo(companyUsername)
      console.log('selectByTo 接口响应:', toResponse)
      toMessages = toResponse.data || []
    } catch (toError: any) {
      console.log('selectByTo 接口调用失败:', toError)
      if (toError.message?.includes('没有数据')) {
        console.log('selectByTo 接口返回没有数据，这是正常的')
      }
    }
    
    console.log('发送消息数量:', fromMessages.length)
    console.log('接收消息数量:', toMessages.length)
    console.log('发送消息详情:', fromMessages)
    console.log('接收消息详情:', toMessages)
    
    // 合并所有消息
    const allMessages = [...fromMessages, ...toMessages]
    console.log('合并后的总消息数量:', allMessages.length)
    console.log('合并后的消息数组:', allMessages)

    // 如果没有数据，直接返回空列表
    if (allMessages.length === 0) {
      console.log('没有找到聊天记录，返回空列表')
      return []
    }
    
    // 按用户分组消息
    const userMessageMap = new Map()
    
    allMessages.forEach((msg: any, index: number) => {
      console.log(`处理第 ${index + 1} 条消息:`, msg)
      const otherUser = msg.from === companyUsername ? msg.to : msg.from
      console.log('对方用户:', otherUser)
      
      if (!userMessageMap.has(otherUser)) {
        userMessageMap.set(otherUser, {
          userId: Date.now() + Math.random(), // 生成唯一ID
          username: otherUser,
          avatar: '',
          lastMessage: '',
          lastMessageTime: new Date(),
          unreadCount: 0,
          messages: []
        })
        console.log('创建新用户记录:', otherUser)
      }
      
      const userData = userMessageMap.get(otherUser)
      const message = {
        ...msg,
        isFromCompany: msg.from === companyUsername,
        sendTime: new Date(msg.sendTime || msg.timestamp || Date.now())
      }
      
      userData.messages.push(message)
      console.log(`为用户 ${otherUser} 添加消息:`, message.content)
      
      // 更新最后一条消息
      if (!userData.lastMessageTime || message.sendTime > userData.lastMessageTime) {
        userData.lastMessage = message.content
        userData.lastMessageTime = message.sendTime
        console.log(`更新用户 ${otherUser} 的最后消息:`, message.content)
      }
    })
    
    console.log('用户消息映射:', userMessageMap)
    console.log('用户数量:', userMessageMap.size)
    
    // 转换为数组并按最后消息时间排序
    const userList = Array.from(userMessageMap.values())
    userList.sort((a, b) => b.lastMessageTime.getTime() - a.lastMessageTime.getTime())
    
    console.log('最终用户列表:', userList)
    console.log('最终用户数量:', userList.length)
    
    console.log('=== 聊天记录获取完成 ===')
    return userList
  } catch (error: any) {
    console.error('获取所有聊天记录失败:', error)
    console.error('错误详情:', error.response || error)
    throw error
  }
}

