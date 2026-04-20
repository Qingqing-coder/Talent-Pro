import request  from '@/utils/request.ts'
import { mockUsers, createMockResponse } from './mockData'

// 使用模拟数据，不依赖后端
export function getLoginByUsername(username: string) {
  return request.get(`/login/selectByUsername`,{
    params: {
      username,
    }
  })
}