import request from '@/utils/request'

// 获取所有用户
export function getAllEmps(params: any) {
  return request.get('/job/all', { params })
}

// 分页条件查询用户
export function getEmpList(params: any) {
  return request.get('/job/selectWithCondition', { params })
}

// data: data
// 添加用户
export function addEmp(data:any) {
  return request.post('/job/add', data)
}

// 更新用户
export function updateEmp(data:any) {
  return request.put('/job/update', data)
}

// 删除用户
export function deleteEmp(id:any) {
  return request.delete('/job/delete', { params: { id } })
}

// 删除用户
export function deleteEmpByName(companyName:any) {
  return request.delete('/job/deleteByCompanyName', { params: { companyName } })
}