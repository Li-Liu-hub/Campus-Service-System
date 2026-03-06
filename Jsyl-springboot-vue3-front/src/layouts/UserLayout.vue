<template>
  <el-container class="user-layout">
    <el-header class="user-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo">
            <el-icon :size="28" class="logo-icon">
              <Odometer />
            </el-icon>
            <span class="logo-text">校园服务</span>
          </div>
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            class="nav-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/user/home">
              <el-icon><House /></el-icon>
              首页
            </el-menu-item>
            <el-menu-item index="/user/posts">
              <el-icon><ChatDotRound /></el-icon>
              交流广场
            </el-menu-item>
            <el-menu-item index="/user/orders">
              <el-icon><Document /></el-icon>
              订单中心
            </el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <el-badge
            :value="notificationCount"
            :hidden="notificationCount === 0"
            class="notification-badge"
          >
            <el-tooltip content="消息通知" placement="bottom">
              <el-button circle @click="router.push('/user/notifications')">
                <el-icon><Bell /></el-icon>
              </el-button>
            </el-tooltip>
          </el-badge>
          <el-dropdown class="user-dropdown" @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">用户</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><UserFilled /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-main class="user-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <el-drawer
      v-model="showNotifications"
      title="消息通知"
      size="400px"
      direction="rtl"
    >
      <template #header>
        <div class="notification-header">
          <span>消息通知</span>
          <el-button
            v-if="notificationCount > 0"
            type="primary"
            link
            size="small"
            @click="markAllAsRead"
          >
            全部已读
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="notification-list">
        <el-empty
          v-if="notifications.length === 0"
          description="暂无通知"
          :image-size="100"
        />
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: !item.isRead && !item.is_read }"
          @click="handleNotificationClick(item)"
        >
          <div class="notification-icon" :class="getNotificationType(item.type)">
            <el-icon>
              <component :is="getNotificationIcon(item.type)" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ item.title }}</div>
            <div class="notification-desc">{{ item.content }}</div>
            <div class="notification-time">{{ formatTime(item.createTime || item.create_time) }}</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Odometer,
  House,
  ChatDotRound,
  Document,
  Bell,
  User,
  ArrowDown,
  UserFilled,
  Setting,
  SwitchButton,
  InfoFilled,
  SuccessFilled,
  WarningFilled,
} from "@element-plus/icons-vue";
import { getNotificationList, markNotificationRead, markAllNotificationsRead } from "@/api/notification";
import type { Notification } from "@/types/entities";
import { NOTIFICATION_TYPE } from "@/types/entities";

const router = useRouter();
const route = useRoute();

const showNotifications = ref(false);
const notificationCount = ref(0);
const notifications = ref<Notification[]>([]);
const loading = ref(false);

const activeMenu = computed(() => route.path);

// 获取通知图标
const getNotificationIcon = (type?: number) => {
  switch (type) {
    case 1: return InfoFilled;
    case 2: return SuccessFilled;
    case 3: return WarningFilled;
    default: return InfoFilled;
  }
};

// 获取通知类型样式
const getNotificationType = (type?: number) => {
  switch (type) {
    case 1: return 'info';
    case 2: return 'success';
    case 3: return 'warning';
    default: return 'info';
  }
};

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true;
    const res = await getNotificationList({ page: 1, pageSize: 20 });
    notifications.value = res.records || [];
    notificationCount.value = res.unreadCount || 0;
  } catch (error) {
    console.error('加载通知失败:', error);
  } finally {
    loading.value = false;
  }
};

// 标记单条通知为已读
const markAsRead = async (notification: Notification) => {
  if (notification.isRead || notification.is_read) return;

  try {
    await markNotificationRead(notification.id!);
    notification.isRead = true;
    notification.is_read = true;
    notificationCount.value = Math.max(0, notificationCount.value - 1);
  } catch (error) {
    console.error('标记已读失败:', error);
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    await markAllNotificationsRead();
    notifications.value.forEach(n => {
      n.isRead = true;
      n.is_read = true;
    });
    notificationCount.value = 0;
    ElMessage.success('已全部标记为已读');
  } catch (error) {
    console.error('标记全部已读失败:', error);
    ElMessage.error('操作失败');
  }
};

// 处理通知点击
const handleNotificationClick = (notification: Notification) => {
  markAsRead(notification);

  // 根据通知类型跳转
  if (notification.type === 3 && notification.relatedId) {
    router.push('/user/orders');
  } else if (notification.type === 2 && notification.relatedId) {
    router.push(`/user/posts/${notification.relatedId}`);
  }

  showNotifications.value = false;
};

// 监听抽屉打开，加载通知
watch(showNotifications, (val) => {
  if (val) {
    loadNotifications();
  }
});

// 组件挂载时加载未读数量
onMounted(() => {
  loadNotifications();

  // 每30秒刷新一次未读数量
  setInterval(() => {
    loadNotifications();
  }, 30000);
});

const handleMenuSelect = (index: string) => {

  if (route.path === index) {
    return;
  }

  router
    .push(index)
    .then(() => {
    })
    .catch((err) => {
      console.error("导航失败:", err);
      ElMessage.error("页面跳转失败");
    });
};

const handleUserCommand = (command: string) => {
  switch (command) {
    case "profile":
      router.push("/user/profile");
      break;
    case "logout":
      handleLogout();
      break;
  }
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    localStorage.clear();
    ElMessage.success("退出登录成功");
    router.push("/login");
  } catch {}
};

watch(
  () => route.path,
  () => {
    window.scrollTo(0, 0);
  }
);
</script>

<style lang="scss" scoped>
.user-layout {
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
}

.user-header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 48px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu {
  border: none;
  background: transparent;
  display: flex;
  gap: 4px;

  :deep(.el-menu-item) {
    height: 44px;
    line-height: 44px;
    padding: 0 20px;
    margin: 0 4px;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 500;
    color: #64748b;
    transition: all 0.2s;
    border: none;

    &:hover {
      color: #667eea;
      background: rgba(102, 126, 234, 0.08);
    }

    &.is-active {
      color: #667eea;
      background: rgba(102, 126, 234, 0.12);
      font-weight: 600;
    }

    .el-icon {
      margin-right: 6px;
      font-size: 16px;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  :deep(.el-badge__content) {
    top: 2px;
    right: 2px;
  }

  :deep(.el-button) {
    width: 40px;
    height: 40px;
    border: none;
    background: #f1f5f9;
    color: #64748b;
    border-radius: 10px;
    transition: all 0.2s;

    &:hover {
      background: #e2e8f0;
      color: #667eea;
    }
  }
}

.user-dropdown {
  cursor: pointer;

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 12px 6px 6px;
    border-radius: 12px;
    transition: all 0.2s;

    &:hover {
      background: #f1f5f9;
    }
  }

  .user-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: 2px solid #fff;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  }

  .user-name {
    font-size: 14px;
    color: #1e293b;
    font-weight: 500;
  }

  .arrow-icon {
    color: #94a3b8;
    font-size: 12px;
  }
}

.user-main {
  flex: 1;
  overflow: auto;
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
  background: #f8fafc;
}

.notification-list {
  .notification-item {
    display: flex;
    gap: 16px;
    padding: 16px 20px;
    border-bottom: 1px solid #f1f5f9;
    transition: all 0.2s;
    cursor: pointer;
    border-radius: 12px;
    margin-bottom: 4px;

    &:hover {
      background: #f8fafc;
    }

    &.unread {
      background: linear-gradient(90deg, rgba(102, 126, 234, 0.06) 0%, transparent 100%);

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
        border-radius: 0 4px 4px 0;
      }
    }
  }

  .notification-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &.info {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(102, 126, 234, 0.05) 100%);
      color: #667eea;
    }

    &.success {
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(16, 185, 129, 0.05) 100%);
      color: #10b981;
    }

    &.warning {
      background: linear-gradient(135deg, rgba(245, 158, 11, 0.15) 0%, rgba(245, 158, 11, 0.05) 100%);
      color: #f59e0b;
    }
  }

  .notification-content {
    flex: 1;
    min-width: 0;
  }

  .notification-title {
    font-size: 14px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 4px;
  }

  .notification-desc {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 6px;
    line-height: 1.5;
  }

  .notification-time {
    font-size: 12px;
    color: #94a3b8;
  }
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-weight: 600;
  color: #1e293b;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .header-left {
    gap: 24px;
  }

  .logo-text {
    display: none;
  }

  .nav-menu {
    display: none;
  }

  .user-main {
    padding: 16px;
  }
}
</style>
