import request from '@/utils/request'
import { getAuthStatus } from '@/utils/auth'

export interface Pre {
  preferenceId?: number
  username: string
  jobId: number
}

// 根据条件搜索岗位
export function searchJobs(params: {
  id ?: number
  title ?: string
  salary ?: string
  companyName ?: string
  companySize ?: string
  fullLocation ?: string
  experience ?: string
  education ?: string
  type ?: string
  benefits ?: string
  jobType ?: string
  sortField?: string
  sortOrder?: string
  pageNum : number
  pageSize : number
}) {
  return request.get('/job/selectWithCondition', { params })
}

//获取工作的收藏数
export function getJobPreNum(params: {
  jobId: number
  pageNum: number
  pageSize: number
}) {
  return request.get('/emp_pre/selectByJobId', { params })
}

//添加收藏关系
export function addPre(pre: Pre){
  return request.post('/emp_pre/addEmpPre', pre)
}

//删除收藏关系
export function deletePre(username: String, jobId: number) {
  return request.delete('/emp_pre/deleteEmpPre', {
    params: {
      username: username,
      jobId: jobId,
    }
  })
}

//获得企业的总量
export function getCompanyTotal() {
  return request.get('/company/all', {
    params: {
      pageNum: 1,
      pageSize: 10
    }
  })
}

//获得用户的总量
export function getEmpTotal() {
  return request.get('/emp/all', {
    params: {
      pageNum: 1,
      pageSize: 10
    }
  })
}

//获得已登录用户的收藏列表
export function getEmpPred(pageNum: number) {
  const {username} = getAuthStatus()
  return request.get('/emp_pre/allByUsername', {
    params: {
      username: username,
      pageNum: pageNum,
      pageSize: 7
    }
  })
}

//判断用户是否已经收藏该工作
export function isPred(jobId: number) {
  const {username} = getAuthStatus()
  return request.get('/emp_pre/isExistEmpPre', {
    params: {
      username: username,
      jobId: jobId,
    }
  })
}
