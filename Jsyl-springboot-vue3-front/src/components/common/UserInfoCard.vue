<template>
  <div class="user-info-card">
    <!-- 用户头像 -->
    <div class="user-avatar">
      <el-avatar :size="avatarSize" :src="userInfo.avatar || ''">
        {{ getAvatarText }}
      </el-avatar>
    </div>
    
    <!-- 用户详情 -->
    <div class="user-details">
      <h3 class="user-name">{{ userInfo.username || '未设置' }}</h3>
      <p class="user-email">{{ userInfo.email || '未设置' }}</p>
      <p class="user-phone">{{ userInfo.phone || '未设置' }}</p>
      <p v-if="userInfo.createdAt" class="user-joined">
        注册时间: {{ userInfo.createdAt }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

/**
 * 用户信息类型
 */
export interface UserInfo {
  username?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  createdAt?: string;
}

/**
 * 组件属性
 */
const props = defineProps<{
  /**
   * 用户信息对象
   */
  userInfo: UserInfo;
  /**
   * 头像大小
   * @default 80
   */
  avatarSize?: number;
}>();

/**
 * 获取头像显示文本
 * @returns 头像文本
 */
const getAvatarText = computed(() => {
  return props.userInfo.username?.charAt(0).toUpperCase() || 'U';
});
</script>

<style lang="scss" scoped>
.user-info-card {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;

  // 响应式布局
  @media (max-width: 768px) {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
}

.user-avatar {
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.user-email,
.user-phone,
.user-joined {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}
</style>
