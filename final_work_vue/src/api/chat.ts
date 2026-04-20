import request from '@/utils/request'

// 获取两个用户之间的聊天历史记录
export function getChatHistory(from: string, to: string) {
  return request.get('/chat/history', {
    params: {
      from,
      to
    }
  })
}

// 发送消息
export function sendMessage(data: {
  from: string
  to: string
  content: string
  type?: string
}) {
  return request.post('/chat/send', data)
}

// 获取用户聊天列表
export function getChatList(userId: string) {
  return request.get('/chat/list', {
    params: {
      userId
    }
  })
} 