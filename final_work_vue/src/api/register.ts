import request  from '@/utils/request.ts'
import { mockUsers, createMockResponse } from './mockData'

export interface LoginerData {
  username: string
  password: string
  role: number
}

export interface EmpData {
  username: string;
  nickname: string;
  password: string;
  name: string;
  age: number;
  salaryWishK: number;
  address: string;
  hasExperience: number;
  phone: string;
  email: string;
  eduBack: string;
  introduction: string;
  major: string;
  sex: number;
}

export interface CompanyData {
  username: string;
  nickname: string;
  password: string;
  companyIntroduction: string;
}

// 模拟用户数据存储
let mockUserStorage = [...mockUsers]

export function addLogin(loginerData: LoginerData) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 检查用户名是否已存在
      const exists = mockUserStorage.some(u => u.username === loginerData.username)
      if (exists) {
        resolve(createMockResponse(null, 400, '用户名已存在'))
      } else {
        // 添加新用户
        mockUserStorage.push({
          id: mockUserStorage.length + 1,
          username: loginerData.username,
          password: loginerData.password,
          role: loginerData.role,
          name: loginerData.username
        })
        resolve(createMockResponse({ success: true }))
      }
    }, 300)
  })
}

export function addEmp(empData: EmpData) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(createMockResponse({ success: true }))
    }, 200)
  })
}

export function addCompany(companyData: CompanyData) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(createMockResponse({ success: true }))
    }, 200)
  })
}

export function isExistUsername(username: string) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const exists = mockUserStorage.some(u => u.username === username)
      resolve(createMockResponse(exists))
    }, 200)
  })
}