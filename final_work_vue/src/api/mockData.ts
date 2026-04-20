// 模拟数据
export const mockUsers = [
  { id: 1, username: 'admin', password: '123456', role: 2, name: '管理员' },
  { id: 2, username: 'company1', password: '123456', role: 1, name: '企业用户1' },
  { id: 3, username: 'user1', password: '123456', role: 0, name: '普通用户1' },
  { id: 4, username: 'user2', password: '123456', role: 0, name: '普通用户2' },
]

export const mockJobs = [
  {
    id: 1,
    title: '前端开发工程师',
    salary: '15K-25K',
    companyName: '腾讯科技',
    companySize: '1000-9999人',
    fullLocation: '深圳·南山区',
    experience: '3-5年',
    education: '本科',
    type: '全职',
    benefits: ['五险一金', '带薪年假', '弹性工作'],
    jobType: '开发',
    createTime: '2024-01-15',
    preNum: 128,
    isFavorite: false
  },
  {
    id: 2,
    title: 'Java后端开发',
    salary: '12K-20K',
    companyName: '阿里巴巴',
    companySize: '10000人以上',
    fullLocation: '杭州·西湖区',
    experience: '2-4年',
    education: '本科',
    type: '全职',
    benefits: ['股票期权', '年终奖', '免费三餐'],
    jobType: '开发',
    createTime: '2024-01-14',
    preNum: 95,
    isFavorite: false
  },
  {
    id: 3,
    title: '产品经理',
    salary: '18K-30K',
    companyName: '字节跳动',
    companySize: '10000人以上',
    fullLocation: '北京·海淀区',
    experience: '3-5年',
    education: '本科',
    type: '全职',
    benefits: ['股权激励', '健身房', '下午茶'],
    jobType: '产品',
    createTime: '2024-01-13',
    preNum: 76,
    isFavorite: false
  },
  {
    id: 4,
    title: 'UI设计师',
    salary: '10K-18K',
    companyName: '美团',
    companySize: '10000人以上',
    fullLocation: '北京·朝阳区',
    experience: '1-3年',
    education: '本科',
    type: '全职',
    benefits: ['五险一金', '带薪年假', '节日福利'],
    jobType: '设计',
    createTime: '2024-01-12',
    preNum: 52,
    isFavorite: false
  },
  {
    id: 5,
    title: '数据分析师',
    salary: '13K-22K',
    companyName: '滴滴出行',
    companySize: '1000-9999人',
    fullLocation: '北京·朝阳区',
    experience: '2-4年',
    education: '硕士',
    type: '全职',
    benefits: ['弹性工作', '学习津贴', '团建活动'],
    jobType: '技术',
    createTime: '2024-01-11',
    preNum: 43,
    isFavorite: false
  }
]

export const mockCompanies = [
  { id: 1, name: '腾讯科技', size: '1000-9999人', location: '深圳' },
  { id: 2, name: '阿里巴巴', size: '10000人以上', location: '杭州' },
  { id: 3, name: '字节跳动', size: '10000人以上', location: '北京' },
  { id: 4, name: '美团', size: '10000人以上', location: '北京' },
  { id: 5, name: '滴滴出行', size: '1000-9999人', location: '北京' }
]

// 模拟API响应
export const createMockResponse = (data: any, code: number = 200, message: string = '成功') => {
  return {
    code,
    message,
    data
  }
}

// 模拟分页响应
export const createMockPageResponse = (list: any[], pageNum: number = 1, pageSize: number = 10, total: number = list.length) => {
  return {
    code: 200,
    message: '成功',
    data: {
      list: list.slice((pageNum - 1) * pageSize, pageNum * pageSize),
      total,
      pageNum,
      pageSize
    }
  }
}

