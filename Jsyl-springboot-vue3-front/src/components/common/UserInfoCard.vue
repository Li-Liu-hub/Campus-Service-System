<template>
  <div class="user-info-card">
    <div class="user-avatar">
      <el-avatar :size="avatarSize" :src="userInfo.avatar || ''">
        {{ getAvatarText }}
      </el-avatar>
    </div>
    
    <div class="user-details">
      <h3 class="user-name">{{ userInfo.username || '未设置' }}</h3>
      <p class="user-campus">{{ userInfo.campusName || '未设置' }}</p>
      <p class="user-phone">{{ userInfo.phone || '未设置' }}</p>
      <p v-if="userInfo.createdAt" class="user-joined">
        注册时间: {{ userInfo.createdAt }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

export interface UserInfo {
  username?: string;
  phone?: string;
  avatar?: string;
  createdAt?: string;
  campusId?: number | null;
  campusName?: string;
}

const props = defineProps<{
  userInfo: UserInfo;
  avatarSize?: number;
}>();

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
}

@media (max-width: 768px) {
  .user-info-card {
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

.user-campus,
.user-phone,
.user-joined {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}
</style>
