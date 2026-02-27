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
import { ref, computed, watch } from "vue";
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
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();

const showNotifications = ref(false);
const notificationCount = ref(2);

const activeMenu = computed(() => route.path);

const notifications = ref([
  {
    id: 1,
    title: "订单更新",
    desc: "您的订单 ORD20260216001 已接单",
    time: "10分钟前",
    type: "success",
    icon: SuccessFilled,
    unread: true,
  },
  {
    id: 2,
    title: "新回复",
    desc: "您的帖子收到了新的回复",
    time: "1小时前",
    type: "info",
    icon: InfoFilled,
    unread: false,
  },
]);

const handleMenuSelect = (index: string) => {
  console.log("菜单点击，索引:", index);
  console.log("当前路由:", route.path);

  if (route.path === index) {
    console.log("已经在目标页面，无需跳转");
    return;
  }

  router
    .push(index)
    .then(() => {
      console.log("导航成功到:", index);
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
  background: var(--bg-secondary);
  display: flex;
  flex-direction: column;
}

.user-header {
  height: 64px;
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  box-shadow: var(--shadow-light);
  padding: 0;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.logo-icon {
  color: var(--primary-color);
}

.logo-text {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--text-primary);
}

.nav-menu {
  border: none;
  background: transparent;
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
    background: linear-gradient(
      135deg,
      var(--primary-color),
      var(--primary-active)
    );
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

.user-main {
  flex: 1;
  overflow: auto;
  padding: var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
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
        content: "";
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
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 var(--spacing-md);
  }

  .nav-menu {
    display: none;
  }

  .user-main {
    padding: var(--spacing-md);
  }
}
</style>
