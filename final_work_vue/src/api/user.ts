import request from '@/utils/request'

// 获取所有用户
export function getAllUsers(params: any) {
  return request.get('/emp/all', { params })
}

// 分页条件查询用户
export function getUserList(params: any) {
  return request.get('/emp/selectWithCondition', { params })
}

// data: data
// 添加用户
export function addUser(data:any) {
  return request.post('/emp/add', data)
}

// 更新用户
export function updateUser(data:any) {
  return request.put('/emp/update', data)
}

// 删除用户
export function deleteUser(username:any) {
  return request.delete('/emp/delete', { params: { username } })
}

// 上传头像
export function uploadAvatar(data: FormData) {
  return request.post('/upload', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取头像路径
export function getAvatarPath(username: string) {
  return request.get('/getFilePath', { params: { username } })
}

// 定义并导出上传证件照的函数（必须与后端接口匹配）
// export const uploadIdCard = (formData: FormData) => {
//   return request.post('/face/uploadIdCard', data, {
//     headers: { 'Content-Type': 'multipart/form-data' }
//   })
// }
export const uploadIdCard = (formData: FormData) => {
  // 1. 第一个参数：后端接口路径（与后端保持一致）
  // 2. 第二个参数：请求体数据（直接传formData，无需额外包装）
  // 3. 无需手动设置Content-Type：axios会自动识别formData并设置为multipart/form-data
  return request.post('/face/uploadIdCard', formData, {
    timeout: 10000 // 可选：上传文件超时时间设长一点（10秒），避免大文件上传超时
  })
}

