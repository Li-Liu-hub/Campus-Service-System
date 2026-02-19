<template>
  <el-card class="security-card" shadow="hover">
    <div class="security-item">
      <!-- 安全项信息 -->
      <div class="security-info">
        <h3 class="security-title">{{ item.title }}</h3>
        <p class="security-description">{{ item.description }}</p>
      </div>
      
      <!-- 操作按钮 -->
      <el-button 
        type="primary" 
        @click="handleAction"
        size="small"
      >
        {{ item.buttonText }}
      </el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
/**
 * 安全管理项类型
 */
export interface SecurityItem {
  /**
   * 标题
   */
  title: string;
  /**
   * 描述
   */
  description: string;
  /**
   * 按钮文本
   */
  buttonText: string;
  /**
   * 操作类型
   */
  action: string;
}

/**
 * 组件属性
 */
const props = defineProps<{
  /**
   * 安全管理项数据
   */
  item: SecurityItem;
}>();

/**
 * 组件事件
 */
const emit = defineEmits<{
  /**
   * 操作事件
   * @param action 操作类型
   */
  (e: 'action', action: string): void;
}>();

/**
 * 处理操作点击
 */
const handleAction = () => {
  emit('action', props.item.action);
};
</script>

<style lang="scss" scoped>
.security-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.security-info {
  flex: 1;
  margin-right: 20px;
}

.security-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.security-description {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}
</style>
