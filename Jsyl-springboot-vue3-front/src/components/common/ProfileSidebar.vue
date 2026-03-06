<template>
  <div class="profile-sidebar">
    <div class="user-card">
      <el-avatar :size="60" :src="userInfo.avatar">
        {{ userInfo.nickname?.charAt(0) || "U" }}
      </el-avatar>
      <div class="user-info">
        <div class="nickname">{{ userInfo.nickname || "用户" }}</div>
        <el-tag :type="roleType" size="small">{{ roleText }}</el-tag>
      </div>
    </div>

    <el-menu
      :default-active="activeTab"
      class="profile-menu"
      @select="handleMenuSelect"
      :router="false"
    >
      <el-menu-item index="info">
        <el-icon><User /></el-icon>
        <span>个人资料</span>
      </el-menu-item>

      <el-menu-item index="orders">
        <el-icon><List /></el-icon>
        <span>我发起的订单</span>
      </el-menu-item>

      <el-menu-item index="accepted-orders">
        <el-icon><Check /></el-icon>
        <span>我接受的订单</span>
      </el-menu-item>

      <el-menu-item index="posts">
        <el-icon><Document /></el-icon>
        <span>我的帖子</span>
      </el-menu-item>

      <el-menu-item index="wallet">
        <el-icon><Wallet /></el-icon>
        <span>我的钱包</span>
      </el-menu-item>

      <el-menu-item index="messages">
        <el-icon><Bell /></el-icon>
        <span>消息通知</span>
        <el-badge
          v-if="unreadCount > 0"
          :value="unreadCount"
          :max="99"
          class="unread-badge"
        />
      </el-menu-item>

      <el-menu-item index="address">
        <el-icon><Location /></el-icon>
        <span>收货地址</span>
      </el-menu-item>

      <el-menu-item index="settings">
        <el-icon><Setting /></el-icon>
        <span>个性设置</span>
      </el-menu-item>

      <el-menu-item index="security">
        <el-icon><Lock /></el-icon>
        <span>账号安全</span>
      </el-menu-item>

      <el-menu-item index="help">
        <el-icon><QuestionFilled /></el-icon>
        <span>帮助与反馈</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import {
  User,
  List,
  Check,
  Document,
  Wallet,
  Bell,
  Location,
  Setting,
  Lock,
  QuestionFilled,
} from "@element-plus/icons-vue";

interface UserInfo {
  nickname?: string;
  avatar?: string;
  role?: number;
}

const props = defineProps<{
  activeTab: string;
  userInfo: UserInfo;
  unreadCount?: number;
}>();

const emit = defineEmits<{
  (e: "menu-select", key: string): void;
}>();

const roleType = computed(() => {
  const types: Record<number, string> = {
    0: "danger",
    1: "info",
    2: "warning",
    3: "success",
    4: "primary",
  };
  return types[props.userInfo.role || 1] || "info";
});

const roleText = computed(() => {
  const texts: Record<number, string> = {
    0: "已限制",
    1: "普通用户",
    2: "VIP",
    3: "管理员",
    4: "超级管理员",
  };
  return texts[props.userInfo.role || 1] || "普通用户";
});

const handleMenuSelect = (key: string) => {
  emit("menu-select", key);
};
</script>

<style lang="scss" scoped>
.profile-sidebar {
  width: 220px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;

  @media (max-width: 768px) {
    width: 100%;
    border-radius: 0;
    padding: 10px;
  }
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  margin-bottom: 20px;
  backdrop-filter: blur(10px);

  .el-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: 2px solid white;
  }

  .user-info {
    flex: 1;
    min-width: 0;

    .nickname {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .el-tag {
      font-size: 12px;
    }
  }
}

.profile-menu {
  background: transparent;
  border: none;

  .el-menu-item {
    color: rgba(255, 255, 255, 0.85);
    border-radius: 8px;
    margin-bottom: 4px;
    height: 44px;
    line-height: 44px;

    &:hover,
    &.is-active {
      background: rgba(255, 255, 255, 0.2);
      color: white;
    }

    .el-icon {
      font-size: 18px;
    }

    .unread-badge {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
    }
  }
}
</style>
