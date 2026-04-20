import request from '@/utils/request'
import { getAuthStatus } from '@/utils/auth'

export function getEmpMessage(){
    const {username} = getAuthStatus()
    return request.get('/emp/selectWithCondition', { params: { username: username, pageNum: 1, pageSize: 10 } })
}

