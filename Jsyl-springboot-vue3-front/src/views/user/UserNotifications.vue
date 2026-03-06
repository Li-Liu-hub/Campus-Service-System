<template>
  <div class="notifications-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h2>消息通知</h2>
          <div class="header-actions">
            <el-button
              v-if="unreadCount > 0"
              type="primary"
              @click="handleMarkAllRead"
            >
              全部已读
            </el-button>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button :label="undefined">全部</el-radio-button>
          <el-radio-button :label="1">系统通知</el-radio-button>
          <el-radio-button :label="2">评论通知</el-radio-button>
          <el-radio-button :label="3">订单通知</el-radio-button>
        </el-radio-group>

        <el-radio-group v-model="filterRead" @change="handleFilterChange">
          <el-radio-button :label="undefined">全部</el-radio-button>
          <el-radio-button :label="false">未读</el-radio-button>
          <el-radio-button :label="true">已读</el-radio-button>
        </el-radio-group>
      </div>

      <div v-loading="loading" class="notification-list">
        <el-empty
          v-if="notifications.length === 0"
          description="暂无通知"
          :image-size="120"
        />

        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: !item.isRead && !item.is_read }"
        >
          <div class="notification-icon" :class="getNotificationType(item.type)">
            <el-icon :size="24">
              <component :is="getNotificationIcon(item.type)" />
            </el-icon>
          </div>

          <div class="notification-content" @click="handleNotificationClick(item)">
            <div class="notification-header-row">
              <span class="notification-title">{{ item.title }}</span>
              <span class="notification-time">{{ formatTime(item.createTime || item.create_time) }}</span>
            </div>
            <div class="notification-desc">{{ item.content }}</div>
            <div class="notification-type-tag">
              <el-tag :type="getTagType(item.type)" size="small">
                {{ getTypeName(item.type) }}
              </el-tag>
            </div>
          </div>

          <div class="notification-actions">
            <el-button
              v-if="!item.isRead && !item.is_read"
              type="primary"
              link
              @click.stop="handleMarkRead(item)"
            >
              标记已读
            </el-button>
            <el-button
              type="danger"
              link
              @click.stop="handleDelete(item)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  InfoFilled,
  SuccessFilled,
  WarningFilled,
} from '@element-plus/icons-vue';
import { getNotificationList, markNotificationRead, markAllNotificationsRead } from '@/api/notification';
import { del } from '@/utils/request';
import type { Notification } from '@/types/entities';
import { NOTIFICATION_TYPE } from '@/types/entities';

const router = useRouter();

const loading = ref(false);
const notifications = ref<Notification[]>([]);
const unreadCount = ref(0);
const filterType = ref<number | undefined>(undefined);
const filterRead = ref<boolean | undefined>(undefined);

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

// 获取标签类型
const getTagType = (type?: number): any => {
  switch (type) {
    case 1: return 'info';
    case 2: return 'success';
    case 3: return 'warning';
    default: return 'info';
  }
};

// 获取类型名称
const getTypeName = (type?: number) => {
  return NOTIFICATION_TYPE[type || 0] || '未知';
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
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true;
    const res = await getNotificationList({
      page: 1,
      pageSize: 100,
      type: filterType.value,
      isRead: filterRead.value,
    });
    notifications.value = res.records || [];
    unreadCount.value = res.unreadCount || 0;
  } catch (error) {
    console.error('加载通知失败:', error);
    ElMessage.error('加载通知失败');
  } finally {
    loading.value = false;
  }
};

// 筛选变化
const handleFilterChange = () => {
  loadNotifications();
};

// 标记单条通知为已读
const handleMarkRead = async (notification: Notification) => {
  try {
    await markNotificationRead(notification.id!);
    notification.isRead = true;
    notification.is_read = true;
    unreadCount.value = Math.max(0, unreadCount.value - 1);
    ElMessage.success('已标记为已读');
  } catch (error) {
    console.error('标记已读失败:', error);
    ElMessage.error('操作失败');
  }
};

// 标记所有通知为已读
const handleMarkAllRead = async () => {
  try {
    await markAllNotificationsRead();
    notifications.value.forEach(n => {
      n.isRead = true;
      n.is_read = true;
    });
    unreadCount.value = 0;
    ElMessage.success('已全部标记为已读');
  } catch (error) {
    console.error('标记全部已读失败:', error);
    ElMessage.error('操作失败');
  }
};

// 删除通知
const handleDelete = async (notification: Notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await del(`/jsyl/home/notification/delete/${notification.id}`);
    notifications.value = notifications.value.filter(n => n.id !== notification.id);
    if (!notification.isRead && !notification.is_read) {
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    }
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 处理通知点击
const handleNotificationClick = (notification: Notification) => {
  if (!notification.isRead && !notification.is_read) {
    handleMarkRead(notification);
  }

  // 根据通知类型跳转
  if (notification.type === 3 && notification.relatedId) {
    router.push('/user/orders');
  } else if (notification.type === 2 && notification.relatedId) {
    router.push(`/user/posts/${notification.relatedId}`);
  }
};

onMounted(() => {
  loadNotifications();
});
</script>

<style lang="scss" scoped>
.notifications-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-card {
  box-shadow: var(--shadow-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    margin: 0;
    font-size: var(--font-size-xl);
    color: var(--text-primary);
  }
}

.filter-bar {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  flex-wrap: wrap;
}

.notification-list {
  min-height: 400px;

  .notification-item {
    display: flex;
    gap: var(--spacing-lg);
    padding: var(--spacing-lg);
    border-bottom: 1px solid var(--border-light);
    transition: all var(--transition-fast);
    position: relative;

    &:hover {
      background: var(--bg-tertiary);
    }

    &.unread {
      background: rgba(64, 158, 255, 0.03);

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 4px;
        background: var(--primary-color);
      }
    }

    &:last-child {
      border-bottom: none;
    }
  }

  .notification-icon {
    width: 48px;
    height: 48px;
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

    &.warning {
      background: rgba(230, 162, 60, 0.1);
      color: var(--warning-color);
    }
  }

  .notification-content {
    flex: 1;
    min-width: 0;
    cursor: pointer;
  }

  .notification-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--spacing-xs);
  }

  .notification-title {
    font-size: var(--font-size-md);
    font-weight: 600;
    color: var(--text-primary);
  }

  .notification-time {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
    white-space: nowrap;
  }

  .notification-desc {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
    margin-bottom: var(--spacing-sm);
    word-break: break-word;
    line-height: 1.6;
  }

  .notification-type-tag {
    margin-top: var(--spacing-xs);
  }

  .notification-actions {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-xs);
    align-items: flex-end;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
  }

  .notification-item {
    flex-direction: column;
  }

  .notification-actions {
    flex-direction: row !important;
    justify-content: flex-end;
  }
}
</style>
