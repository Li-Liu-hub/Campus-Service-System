<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside-container">
      <div class="sidebar-logo" :class="{ collapse: isCollapse }">
        <div class="logo-icon">
          <el-icon :size="24" color="#fff"><School /></el-icon>
        </div>
        <transition name="fade">
          <span class="logo-text" v-show="!isCollapse">校园服务平台</span>
        </transition>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :collapse-transition="false"
        unique-opened
        router
      >
        <el-menu-item index="/home/main">
          <el-icon><House /></el-icon>
          <template #title>首页概览</template>
        </el-menu-item>
        
        <el-menu-item index="/home/ordercenter">
          <el-icon><Document /></el-icon>
          <template #title>订单中心</template>
        </el-menu-item>

        <!-- 可以在这里添加更多菜单项 -->
      </el-menu>
    </el-aside>

    <!-- 右侧主体容器 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header-container">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon :size="20">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </div>
          
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-tooltip content="消息通知" placement="bottom">
            <div class="action-item">
              <el-badge :value="3" class="badge-item" :max="99">
                <el-icon :size="20"><Bell /></el-icon>
              </el-badge>
            </div>
          </el-tooltip>
          
          <el-dropdown trigger="click" class="user-dropdown">
            <div class="user-info-trigger">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">管理员</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfileClick">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">

import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import {
  House,
  User,
  Document,
  Bell,
  Expand,
  Fold,
  CaretBottom,
  SwitchButton,
  Lock,
  School
} from "@element-plus/icons-vue";


const LOGIN_PATH = "/login";


const router = useRouter();
const route = useRoute();
const isCollapse = ref(false);


const activeMenu = computed(() => {
  return route.path;
});

const currentRouteName = computed(() => {
  if (route.path.includes('main')) return '概览';
  if (route.path.includes('order')) return '订单中心';
  if (route.path.includes('profile')) return '个人中心';
  return '当前页面';
});


const toggleCollapse = (): void => {
  isCollapse.value = !isCollapse.value;
};

const handleProfileClick = (): void => {
  router.push("/home/profile");
};

const handleLogout = (): void => {
  localStorage.clear();
  ElMessage.success("退出登录成功");
  router.push(LOGIN_PATH);
};
</script>

<style lang="scss" scoped>
/* 变量定义 */
:root {
  --sidebar-width: 240px;
  --header-height: 64px;
  --menu-bg: #1f2937;
  --menu-hover: #374151;
  --menu-text: #9ca3af;
  --menu-active-text: #ffffff;
  --menu-active-bg: #3b82f6;
}

.layout-container {
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

/* 侧边栏样式 */
.aside-container {
  background-color: var(--menu-bg);
  transition: width var(--transition-normal);
  box-shadow: 4px 0 16px rgba(0, 0, 0, 0.05);
  z-index: 1001;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .sidebar-logo {
    height: 64px;
    line-height: 64px;
    background: #111827;
    text-align: center;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    
    .logo-icon {
      width: 36px;
      height: 36px;
      background: var(--primary-color);
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
    }
    
    .logo-text {
      color: #fff;
      font-weight: 700;
      font-size: 18px;
      font-family: var(--font-family);
      white-space: nowrap;
      letter-spacing: 0.5px;
    }
    
    &.collapse {
      .logo-icon {
        margin-right: 0;
      }
    }
  }

  .el-menu-vertical {
    border-right: none;
    width: 100%;
    padding-top: 16px;
    background-color: transparent !important;
    
    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
      margin: 4px 12px;
      border-radius: 8px;
      color: var(--menu-text) !important;
      background-color: transparent !important;
      
      &:hover {
        background-color: var(--menu-hover) !important;
        color: #fff !important;
      }
      
      &.is-active {
        background-color: var(--menu-active-bg) !important;
        color: var(--menu-active-text) !important;
        font-weight: 600;
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        
        &::before {
          display: none;
        }
      }
      
      .el-icon {
        width: 20px;
        text-align: center;
        font-size: 18px;
        margin-right: 12px;
        color: inherit;
      }
    }
  }
}

/* 主容器样式 */
.main-container {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  transition: margin-left 0.3s;
  background-color: var(--bg-secondary);
  position: relative;
}

/* 头部样式 */
.header-container {
  height: 64px;
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 1000;
  border-bottom: 1px solid var(--border-light);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .collapse-btn {
      padding: 8px;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      color: var(--text-secondary);
      
      &:hover {
        background: var(--bg-secondary);
        color: var(--primary-color);
      }
    }
    
    .breadcrumb {
      font-size: 14px;
      
      :deep(.el-breadcrumb__inner) {
        color: var(--text-tertiary);
        font-weight: 400;
        
        &.is-link, a {
          color: var(--text-tertiary);
          font-weight: 400;
          &:hover {
            color: var(--primary-color);
          }
        }
      }
      
      :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
        color: var(--text-primary);
        font-weight: 600;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    height: 100%;
    gap: 8px;
    
    .action-item {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      cursor: pointer;
      transition: all 0.2s;
      color: var(--text-secondary);
      
      &:hover {
        background: var(--bg-secondary);
        color: var(--primary-color);
      }
      
      .badge-item {
        display: flex;
        align-items: center;
      }
    }
    
    .user-dropdown {
      margin-left: 12px;
      padding: 4px 8px;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.2s;
      
      &:hover {
        background: var(--bg-secondary);
      }
      
      .user-info-trigger {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .username {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
        }
        
        .el-icon {
          font-size: 12px;
          color: var(--text-tertiary);
        }
      }
    }
  }
}

/* 主内容区 */
.app-main {
  min-height: calc(100vh - 64px);
  width: 100%;
  position: relative;
  overflow-y: auto;
  padding: 24px;
  background-color: var(--bg-secondary);
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.28s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.5, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
}

/* 折叠按钮优化 */
.el-button--text {
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  
  &:hover {
    color: var(--primary-color);
    background-color: var(--bg-tertiary);
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .el-aside {
    width: 64px !important;
  }
  
  .logo-text {
    display: none;
  }
  
  .header-actions {
    gap: var(--spacing-md);
  }
  
  .header-action-item span:not(.el-icon) {
    display: none;
  }
  
  .header-action-item .el-icon {
    margin-right: 0;
  }
}
</style>
