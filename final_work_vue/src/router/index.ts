import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router' 
import { getAuthStatus } from '@/utils/auth.ts'
import { getLoginByUsername } from '@/api/login.ts'

// ================= 1. 公共无权限路由 =================
const commonRoutes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/common/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/common/Register.vue'),
  },
  {
    path: '/face-login',
    name: 'face-login',
    component: () => import('@/views/common/FaceLogin.vue'),
  }
]

// ================= 2. 用户端路由 =================
const userRoutes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/shouye' // 根路径默认重定向到用户端首页
  },
  {
    path: '/shouye',
    component: () => import('@/layout/user/index.vue'), // 用户端Layout
    children: [
      {
        path: '', // 默认匹配 /shouye
        name: 'shouye',
        component: () => import('@/views/user/home/index.vue'),
      }
    ]
  },
  {
    path: '/job-detail/:id',
    name: 'jobDetail',
    component: () => import('@/views/user/jobDetail/index.vue')
  },
  {
    path: '/job-detail/:id/speak',
    name: 'jobDetailSpeak',
    component: () => import('@/views/user/jobDetail/speak.vue')
  },
  // --- 个人中心模块 ---
  {
    path: '/personalcenter',
    name: 'personalCenter',
    component: () => import('@/views/user/personalCenter/index.vue')
  },
  {
    path: '/usermessage',
    name: 'userMessage',
    component: () => import('@/views/user/personalCenter/usermessage.vue')
  },
  {
    path: '/updateuser',
    name: 'updateUser',
    component: () => import('@/views/user/personalCenter/updateuser.vue')
  },
  {
    path: '/card',
    name: 'card',
    component: () => import('@/views/user/personalCenter/idCard.vue')
  },
  {
    path: '/predJobs',
    name: 'predJobs',
    component: () => import('@/views/user/personalCenter/predJob.vue')
  },
  {
    path: '/talk',
    name: 'talk',
    component: () => import('@/views/user/personalCenter/talk.vue')
  }
]

// ================= 3. 企业端路由 =================
const companyRoutes: Array<RouteRecordRaw> = [
  {
    path: '/company',
    component: () => import('@/layout/company/index.vue'), // 企业端专属 Layout
    meta: { requiredRole: 'company' },
    children: [
      {
        path: '', 
        name: 'companyDashboard',
        component: () => import('@/views/company/index.vue'),
      },
      {
        path: 'comidv', // 对应企业资料管理
        name: 'comidv',
        component: () => import('@/views/company/ComTable.vue'), 
      },
      {
        path: 'comemp', // 对应岗位管理
        name: 'comemp',
        component: () => import('@/views/company/EmpTable.vue'),
      },
      {
        path: 'message', // 对应消息对话
        name: 'companyMessage',
        component: () => import('@/views/company/message.vue'),
      },
    ],
  }
]

// ================= 4. 管理端路由 =================
const adminRoutes: Array<RouteRecordRaw> = [
  {
    path: '/admin',
    component: () => import('@/layout/admin/index.vue'), // 管理端专属 Layout
    meta: { requiredRole: 'admin' },
    children: [
      {
        path: '', 
        name: 'adminDashboard',
        component: () => import('@/views/admin/index.vue'),
      },
      {
        path: 'test',
        name: 'test',
        component: () => import('@/views/admin/test.vue'),
      },
      {
        path: 'user-management',
        name: 'userManagement',
        component: () => import('@/views/admin/UserTable.vue'),
      },
      {
        path: 'com-management',
        name: 'comManagement',
        component: () => import('@/views/admin/ComTable.vue'),
      },
      {
        path: 'emp-management',
        name: 'empManagement',
        component: () => import('@/views/admin/EmpTable.vue'),
      },
    ],
  }
]

// ================= 5. AI助手路由 =================
const assistantRoutes: Array<RouteRecordRaw> = [
  {
    path: '/assistant',
    name: 'assistant',
    component: () => import('@/views/assistant/index.vue')
  },
  {
    path: '/graph',
    name: 'graph',
    component: () => import('@/views/assistant/demo.vue')
  }
]

// ================= 实例化路由 =================
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...commonRoutes,
    ...userRoutes,
    ...companyRoutes,
    ...adminRoutes,
    ...assistantRoutes
  ],
})

// ================= 全局路由守卫 =================
// 增加 from 参数用于判断上一个页面来源
router.beforeEach(async (to, from) => {
  const isFirstVisit = !sessionStorage.getItem('visited');
  const { username, isAuthenticated } = getAuthStatus();
  
  // 1. 如果是首次访问，打上标记并跳转到首页
  if (isFirstVisit) {
    sessionStorage.setItem('visited', 'true');
    return { name: 'shouye' };
  }

  // 2. 允许无权限直接访问的白名单页面
  const whiteList = ['Login', 'Register', 'face-login', 'shouye'];
  if (whiteList.includes(to.name as string)) {
    return true;
  }

  // 3. 检查 AI 助手页面访问权限 (限制只能从首页进入)
  if (to.name === 'assistant' || to.name === 'graph') {
    // 【核心修复】：如果是 AI助手(assistant) 和 知识图谱(graph) 之间的内部互跳，直接放行
    if ((to.name === 'graph' && from.name === 'assistant') || 
        (to.name === 'assistant' && from.name === 'graph')) {
      // 内部互跳，不执行任何拦截拦截
    } else {
      // 从外部进来的话，依然要严格检查 fromHome 通行证
      const fromHome = sessionStorage.getItem('fromHome');
      if (!fromHome) {
        return { name: 'shouye' };
      }
      sessionStorage.removeItem('fromHome');
    }
  }

  // 4. 检查全局登录状态，未登录全部拦截去 Login
  if (!isAuthenticated) {
    return { name: 'Login' };
  }

  // 5. 基于角色的权限访问控制 (RBAC)
  if (isAuthenticated && username) {
    try {
      const res = await getLoginByUsername(username);
      // 假设 role 规则: 0: 用户, 1: 企业, 2: 管理员
      const currentRole = res.data.role; 
      
      // 普通用户 (role: 0) 禁止访问企业端和管理端
      if (currentRole === 0 && (to.path.startsWith('/company') || to.path.startsWith('/admin'))) {
        return { name: 'shouye' };
      }
      
      // 企业用户 (role: 1) 禁止访问管理端
      if (currentRole === 1 && to.path.startsWith('/admin')) {
        return { path: '/company' }; // 退回企业大厅
      }

    } catch (error) {
      console.error('获取用户角色失败:', error);
      return true; // 获取失败暂且放行（或根据你的安全要求改为拦截）
    }
  }

  return true;
  
});

export default router