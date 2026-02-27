<template>
  <div class="message-center">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">消息通知</span>
          <el-button type="primary" link @click="markAllRead" v-if="unreadCount > 0">
            全部标为已读
          </el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all">
          <template #label>
            <span class="tab-label">
              全部
              <el-badge v-if="unreadCount > 0" :value="unreadCount" :max="9" />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="系统通知" name="system" />
        <el-tab-pane label="互动消息" name="互动" />
        <el-tab-pane label="订单消息" name="order" />
      </el-tabs>
      
      <div class="message-list">
        <div
          v-for="msg in filteredMessages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: !msg.isRead }"
          @click="handleRead(msg)"
        >
          <div class="message-icon" :class="msg.type">
            <el-icon v-if="msg.type === 'system'"><Bell /></el-icon>
            <el-icon v-else-if="msg.type === '互动'"><ChatDotRound /></el-icon>
            <el-icon v-else-if="msg.type === 'order'"><List /></el-icon>
          </div>
          
          <div class="message-content">
            <div class="message-title">{{ msg.title }}</div>
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
          
          <div class="message-actions" v-if="!msg.isRead">
            <el-badge is-dot />
          </div>
        </div>
        
        <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Bell, ChatDotRound, List } from '@element-plus/icons-vue';

interface Message {
  id: number;
  title: string;
  content: string;
  type: 'system' | '互动' | 'order';
  time: string;
  isRead: boolean;
}

const activeTab = ref('all');
const messages = ref<Message[]>([
  { id: 1, title: '系统升级通知', content: '系统将于今晚22:00-23:00进行升级维护', type: 'system', time: '2024-01-15 14:30', isRead: false },
  { id: 2, title: '订单已接取', content: '您的订单已被用户张三接取', type: 'order', time: '2024-01-15 10:20', isRead: false },
  { id: 3, title: '评论回复', content: '用户李四回复了您的帖子', type: '互动', time: '2024-01-14 16:45', isRead: true },
  { id: 4, title: '账户安全提醒', content: '您的账号在新设备登录', type: 'system', time: '2024-01-14 09:00', isRead: true },
]);

const unreadCount = computed(() => messages.value.filter(m => !m.isRead).length);

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') return messages.value;
  return messages.value.filter(m => m.type === activeTab.value);
});

const handleTabChange = () => {};

const handleRead = (msg: Message) => {
  if (!msg.isRead) {
    msg.isRead = true;
  }
};

const markAllRead = () => {
  messages.value.forEach(m => m.isRead = true);
  ElMessage.success('已全部标为已读');
};

onMounted(() => {});
</script>

<style lang="scss" scoped>
.message-center {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }
  
  .tab-label {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  .message-list {
    .message-item {
      display: flex;
      gap: 12px;
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: #f5f7fa;
      }
      
      &:last-child {
        border-bottom: none;
      }
      
      &.unread {
        background: rgba(64, 158, 255, 0.05);
        
        .message-title {
          font-weight: 600;
        }
      }
    }
    
    .message-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      flex-shrink: 0;
      
      &.system {
        background: rgba(64, 158, 255, 0.1);
        color: #409eff;
      }
      
      &.互动 {
        background: rgba(230, 162, 60, 0.1);
        color: #e6a23c;
      }
      
      &.order {
        background: rgba(103, 194, 58, 0.1);
        color: #67c23a;
      }
    }
    
    .message-content {
      flex: 1;
      min-width: 0;
      
      .message-title {
        font-size: 14px;
        margin-bottom: 4px;
        color: #303133;
      }
      
      .message-text {
        font-size: 12px;
        color: #909399;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .message-time {
        font-size: 12px;
        color: #c0c4cc;
      }
    }
    
    .message-actions {
      display: flex;
      align-items: center;
    }
  }
}
</style>
