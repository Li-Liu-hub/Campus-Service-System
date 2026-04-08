
import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { getToken } from '@/utils/storage';
import { ElMessage } from 'element-plus';


interface RouteMeta {
  /** 页面标题 */
  title?: string;
  /** 是否需要登录认证 */
  requiresAuth?: boolean;
  /** 页面图标 */
  icon?: string;
}

interface AppRouteRecordRaw extends Omit<RouteRecordRaw, 'children' | 'meta'> {
  children?: AppRouteRecordRaw[];
  meta?: RouteMeta;
}


/** 应用标题 */
const APP_TITLE = '健康医疗服务平台';

/** 登录页面路径 */
const LOGIN_PATH = '/login';

/** 注册页面路径 */
const REGISTER_PATH = '/register';


const routes: AppRouteRecordRaw[] = [
  {
    path: '/',
    redirect: LOGIN_PATH
  },
  {
    path: LOGIN_PATH,
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: REGISTER_PATH,
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/layouts/UserLayout.vue'),
    redirect: '/user/home',
    meta: {
      title: '用户端',
      requiresAuth: true
    },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/UserHome.vue'),
        meta: {
          title: '首页',
          requiresAuth: true
        }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/UserOrders.vue'),
        meta: {
          title: '订单中心',
          requiresAuth: true
        }
      },
      {
        path: 'posts',
        name: 'UserPosts',
        component: () => import('@/views/user/UserPosts.vue'),
        meta: {
          title: '交流广场',
          requiresAuth: true
        }
      },
      {
        path: 'notifications',
        name: 'UserNotifications',
        component: () => import('@/views/user/UserNotifications.vue'),
        meta: {
          title: '消息通知',
          requiresAuth: true
        }
      },
      {
        path: 'posts/:id',
        name: 'PostDetail',
        component: () => import('@/views/user/PostDetailPage.vue'),
        meta: {
          title: '帖子详情',
          requiresAuth: true
        }
      },
      {
        path: 'chat/:targetUserId',
        name: 'UserChat',
        component: () => import('@/views/user/UserChat.vue'),
        meta: {
          title: '聊天',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '账户设置',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: {
      title: '管理端',
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/components/admin/Main.vue'),
        meta: {
          title: '管理首页',
          requiresAuth: true
        }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/components/admin/OrderCenter.vue'),
        meta: {
          title: '订单管理',
          requiresAuth: true
        }
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/components/admin/PostCenter.vue'),
        meta: {
          title: '帖子管理',
          requiresAuth: true
        }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/components/admin/UserManagement.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/components/admin/SystemSettings.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      },
      {
        path: 'logs',
        name: 'AdminLogs',
        component: () => import('@/components/admin/OperationLogs.vue'),
        meta: {
          title: '操作日志',
          requiresAuth: true
        }
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: () => import('@/components/admin/NotificationManagement.vue'),
        meta: {
          title: '通知管理',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      }
    ]
  }
];


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes as RouteRecordRaw[],
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  }
});


router.beforeEach((to, _from, next) => {
  setPageTitle(to);
  
  const token = getToken();
  const userInfoStr = localStorage.getItem('userInfo');
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;
  const userRole = userInfo?.role || 0;
  
  const isAdminRoute = to.path.startsWith('/admin');
  const isUserRoute = to.path.startsWith('/user');
  
  if (to.meta.requiresAuth) {
    if (!token) {
      ElMessage.warning('请先登录');
      next('/login');
      return;
    }
    
    if (isAdminRoute && userRole !== 4) {
      ElMessage.warning('您没有权限访问该页面');
      next('/user/home');
      return;
    }
    
    if (isUserRoute && userRole === 4) {
      ElMessage.warning('管理员请访问管理后台');
      next('/admin/dashboard');
      return;
    }
  }
  
  next();
});

function setPageTitle(route: typeof router.currentRoute.value): void {
  const title = route.meta?.title;
  document.title = title ? `${title} - ${APP_TITLE}` : APP_TITLE;
}


export default router;
