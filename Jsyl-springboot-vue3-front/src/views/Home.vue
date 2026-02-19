<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #2e3b4e">
      <div class="sidebar-logo" :class="{ collapse: isCollapse }">
        <el-icon size="24" color="#fff"><Menu /></el-icon>
        <span class="logo-text" v-show="!isCollapse">校园服务平台</span>
      </div>
      <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        background-color="#2e3b4e"
        text-color="#fff"
        active-text-color="#ffd04b"
        :collapse="isCollapse"
        collapse-transition
        style="height: calc(100% - 60px); border-right: none"
      >
        <el-menu-item index="1" @click="handleMainClick">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="2" @click="handleOrderCenterClick">
          <el-icon><Document /></el-icon>
          <template #title>订单中心</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主体容器 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header
        style="background-color: #fff; border-bottom: 1px solid #e6e6e6"
      >
        <div class="header-actions">
          <el-dropdown>
            <span class="header-action-item">
              <el-icon size="18"><Bell /></el-icon>
              <sup class="badge">3</sup>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>您有1条新消息</el-dropdown-item>
                <el-dropdown-item>您的账号将于7天后过期</el-dropdown-item>
                <el-dropdown-item>系统更新提醒</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown class="header-action-item">
            <span>
              <el-icon size="18"><User /></el-icon>
              管理员
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfileClick"
                  >个人中心</el-dropdown-item
                >
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout"
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button
            type="text"
            class="header-action-item"
            @click="toggleCollapse"
          >
            {{ isCollapse ? "展开" : "折叠" }}
          </el-button>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <router-view />
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
/**
 * 主布局组件
 *
 * 功能说明：
 * - 侧边栏导航
 * - 顶部导航栏
 * - 用户操作（退出登录、个人中心）
 * - 菜单折叠/展开
 */

import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  ElContainer,
  ElAside,
  ElHeader,
  ElMenu,
  ElMenuItem,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElButton,
  ElIcon,
} from "element-plus";
import { Menu, House, User, Document, Bell } from "@element-plus/icons-vue";

// ==================== 常量定义 ====================

const LOGIN_PATH = "/login";

// ==================== 响应式数据 ====================

const router = useRouter();
const isCollapse = ref(false);

// ==================== 事件处理 ====================

/**
 * 切换菜单折叠状态
 */
const toggleCollapse = (): void => {
  isCollapse.value = !isCollapse.value;
};

/**
 * 跳转到主页
 */
const handleMainClick = (): void => {
  router.push("/home/main");
};

/**
 * 跳转到订单中心
 */
const handleOrderCenterClick = (): void => {
  router.push("/home/ordercenter");
};

/**
 * 跳转到个人中心
 */
const handleProfileClick = (): void => {
  router.push("/home/profile");
};

/**
 * 退出登录
 */
const handleLogout = (): void => {
  localStorage.clear();
  ElMessage.success("退出登录成功");
  router.push(LOGIN_PATH);
};
</script>

<style lang="scss">
html,
body,
#app {
  margin: 0;
  padding: 0;
  height: 100%;
}

/* 侧边栏样式优化 */
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-normal);
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
}

.sidebar-logo.collapse {
  justify-content: center;
  padding: 0;
}

.logo-text {
  margin-left: var(--spacing-md);
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: #fff;
  transition: opacity var(--transition-normal);
}

/* 侧边栏菜单优化 */
.el-menu-vertical-demo {
  background: linear-gradient(180deg, #2e3b4e 0%, #2c3e50 100%);
  border-right: none;
  overflow-x: hidden;
  
  .el-menu-item {
    height: 56px;
    line-height: 56px;
    margin: var(--spacing-xs) var(--spacing-sm);
    border-radius: var(--border-radius-md);
    transition: all var(--transition-fast);
    color: rgba(255, 255, 255, 0.8);
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
      color: #fff;
    }
    
    &.is-active {
      background-color: var(--primary-color);
      color: #fff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    }
    
    .el-icon {
      margin-right: var(--spacing-md);
      font-size: 18px;
    }
  }
}

/* 顶部导航栏优化 */
.el-header {
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 0 var(--spacing-xl);
  height: 60px;
  line-height: 60px;
}

.header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--spacing-lg);
  height: 100%;
}

.header-action-item {
  cursor: pointer;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  color: var(--text-secondary);
  
  &:hover {
    background-color: var(--bg-tertiary);
    color: var(--text-primary);
  }
  
  .el-icon {
    font-size: 18px;
    margin-right: var(--spacing-xs);
  }
}

/* 徽章样式优化 */
.badge {
  background-color: var(--danger-color);
  color: #fff;
  border-radius: var(--border-radius-full);
  padding: 0 6px;
  font-size: var(--font-size-xs);
  font-weight: 500;
  position: relative;
  top: -12px;
  left: -8px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.3);
}

/* 下拉菜单优化 */
.el-dropdown-menu {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-medium);
  border: none;
  padding: var(--spacing-xs) 0;
  
  .el-dropdown-item {
    padding: var(--spacing-sm) var(--spacing-xl);
    margin: 0;
    transition: all var(--transition-fast);
    
    &:hover {
      background-color: var(--bg-tertiary);
      color: var(--primary-color);
    }
    
    &.is-disabled {
      color: var(--text-quaternary);
    }
  }
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
