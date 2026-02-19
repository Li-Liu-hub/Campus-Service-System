<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="sidebar-header">
        <div class="logo-wrapper">
          <el-icon :size="isCollapse ? 32 : 28" class="logo-icon">
            <Odometer />
          </el-icon>
          <span v-show="!isCollapse" class="logo-text">校园服务</span>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        :collapse-transition="false"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><House /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>帖子管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/settings">系统设置</el-menu-item>
          <el-menu-item index="/admin/logs">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>

      <div class="sidebar-footer">
        <el-button
          :icon="isCollapse ? Expand : Fold"
          text
          @click="toggleCollapse"
          class="collapse-btn"
        >
          <span v-show="!isCollapse">{{ isCollapse ? '展开' : '收起' }}</span>
        </el-button>
      </div>
    </el-aside>

    <el-container class="main-wrapper">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <el-button circle @click="toggleFullscreen">
              <el-icon><FullScreen /></el-icon>
            </el-button>
          </el-tooltip>

          <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
            <el-tooltip content="消息通知" placement="bottom">
              <el-button circle @click="showNotifications = true">
                <el-icon><Bell /></el-icon>
              </el-button>
            </el-tooltip>
          </el-badge>

          <el-dropdown class="user-dropdown" @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">管理员</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><UserFilled /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <el-drawer
      v-model="showNotifications"
      title="消息通知"
      size="360px"
      direction="rtl"
    >
      <div class="notification-list">
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: item.unread }"
        >
          <div class="notification-icon" :class="item.type">
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ item.title }}</div>
            <div class="notification-desc">{{ item.desc }}</div>
            <div class="notification-time">{{ item.time }}</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Odometer,
  House,
  Document,
  ChatDotRound,
  User,
  Setting,
  Expand,
  Fold,
  FullScreen,
  Bell,
  ArrowDown,
  UserFilled,
  SwitchButton,
  InfoFilled,
  Warning,
  SuccessFilled
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const isCollapse = ref(false);
const showNotifications = ref(false);
const notificationCount = ref(3);

const activeMenu = computed(() => route.path);

const currentPageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/admin/dashboard': '工作台',
    '/admin/orders': '订单管理',
    '/admin/posts': '帖子管理',
    '/admin/users': '用户管理',
    '/admin/settings': '系统设置',
    '/admin/logs': '操作日志',
    '/admin/profile': '个人中心'
  };
  return titleMap[route.path] || '页面';
});

const notifications = ref([
  { id: 1, title: '新订单提醒', desc: '您有3个新订单待处理', time: '5分钟前', type: 'info', icon: InfoFilled, unread: true },
  { id: 2, title: '系统更新', desc: '系统将于今晚22:00进行维护', time: '1小时前', type: 'warning', icon: Warning, unread: true },
  { id: 3, title: '订单完成', desc: '订单 ORD20260216001 已完成', time: '2小时前', type: 'success', icon: SuccessFilled, unread: false }
]);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};

const handleMenuSelect = (index: string) => {
  router.push(index);
};

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile');
      break;
    case 'settings':
      router.push('/admin/settings');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    localStorage.clear();
    ElMessage.success('退出登录成功');
    router.push('/login');
  } catch {
  }
};

watch(() => route.path, () => {
  window.scrollTo(0, 0);
});
</script>

<style lang="scss">
.admin-layout {
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

.sidebar {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  transition: width var(--transition-normal);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--spacing-lg);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  width: 100%;
}

.logo-icon {
  color: var(--primary-color);
}

.logo-text {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border: none;
  background: transparent;
  padding: var(--spacing-md) 0;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
  }

  .el-menu-item,
  .el-sub-menu__title {
    height: 52px;
    line-height: 52px;
    margin: var(--spacing-xs) var(--spacing-sm);
    border-radius: var(--border-radius-md);
    color: rgba(255, 255, 255, 0.7);
    transition: all var(--transition-fast);

    &:hover {
      background: rgba(255, 255, 255, 0.1) !important;
      color: #fff;
    }

    .el-icon {
      font-size: 18px;
      margin-right: var(--spacing-md);
    }
  }

  .el-menu-item.is-active {
    background: linear-gradient(90deg, var(--primary-color), var(--primary-active)) !important;
    color: #fff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  .el-sub-menu .el-menu {
    background: transparent;

    .el-menu-item {
      padding-left: 56px !important;
    }
  }
}

.sidebar-footer {
  padding: var(--spacing-md);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  width: 100%;
  height: 40px;
  color: rgba(255, 255, 255, 0.7);

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.1);
  }
}

.main-wrapper {
  display: flex;
  flex-direction: column;
  background: var(--bg-secondary);
  overflow: hidden;
}

.header {
  height: 64px;
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  box-shadow: var(--shadow-light);
}

.header-left {
  flex: 1;
}

.breadcrumb {
  :deep(.el-breadcrumb__inner) {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);

    &:hover {
      color: var(--primary-color);
    }

    &.is-link {
      color: var(--text-primary);
      font-weight: 500;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.notification-badge {
  :deep(.el-badge__content) {
    top: 4px;
    right: 4px;
  }
}

.user-dropdown {
  cursor: pointer;

  .user-info {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-xs) var(--spacing-md);
    border-radius: var(--border-radius-md);
    transition: all var(--transition-fast);

    &:hover {
      background: var(--bg-tertiary);
    }
  }

  .user-avatar {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-active));
  }

  .user-name {
    font-size: var(--font-size-sm);
    color: var(--text-primary);
    font-weight: 500;
  }

  .arrow-icon {
    color: var(--text-tertiary);
    font-size: 12px;
  }
}

.main-content {
  flex: 1;
  overflow: auto;
  padding: 0;
}

.notification-list {
  .notification-item {
    display: flex;
    gap: var(--spacing-md);
    padding: var(--spacing-lg);
    border-bottom: 1px solid var(--border-light);
    transition: all var(--transition-fast);

    &:hover {
      background: var(--bg-tertiary);
    }

    &.unread {
      background: rgba(64, 158, 255, 0.05);

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background: var(--primary-color);
      }
    }
  }

  .notification-icon {
    width: 44px;
    height: 44px;
    border-radius: var(--border-radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &.info {
      background: rgba(64, 158, 255, 0.1);
      color: var(--primary-color);
    }

    &.warning {
      background: rgba(230, 162, 60, 0.1);
      color: var(--warning-color);
    }

    &.success {
      background: rgba(103, 194, 58, 0.1);
      color: var(--success-color);
    }
  }

  .notification-content {
    flex: 1;
    min-width: 0;
  }

  .notification-title {
    font-size: var(--font-size-sm);
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: var(--spacing-xs);
  }

  .notification-desc {
    font-size: var(--font-size-xs);
    color: var(--text-secondary);
    margin-bottom: var(--spacing-xs);
  }

  .notification-time {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all var(--transition-normal);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }

  .user-name {
    display: none;
  }

  .breadcrumb {
    display: none;
  }
}
</style>
