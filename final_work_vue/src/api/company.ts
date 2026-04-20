import request from '@/utils/request'

// 获取所有用户
export function getAllComs(params: any) {
  return request.get('/company/all', { params })
}

// 分页条件查询用户
export function getComList(params: any) {
  return request.get('/company/selectWithCondition', { params })
}

// 根据公司名称来获取该公司所有职位
export function getJobByN(params: any) {
  return request.get('/company/selectAllJobByUsername', { params })
}

// data: data
// 添加用户
export function addCom(data:any) {
  return request.post('/company/add', data)
}

// 更新用户
export function updateCom(data:any) {
  return request.put('/company/update', data)
}

// 删除用户
export function deleteCom(username:any) {
  return request.delete('/company/delete', { params: { username } })
}
