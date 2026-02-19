<template>
  <div class="profile-sidebar">
    <el-menu
      :default-active="activeTab"
      class="profile-menu"
      @select="handleMenuSelect"
      :router="false"
    >
      <!-- 用户信息菜单项 -->
      <el-menu-item index="info">
        <el-icon><User /></el-icon>
        <span>用户信息</span>
      </el-menu-item>
      
      <!-- 常用地址菜单项 -->
      <el-menu-item index="address">
        <el-icon><Location /></el-icon>
        <span>常用地址</span>
      </el-menu-item>
      
      <!-- 安全管理菜单项 -->
      <el-menu-item index="security">
        <el-icon><Lock /></el-icon>
        <span>安全管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { User, Location, Lock } from '@element-plus/icons-vue';

/**
 * 组件属性
 */
const props = defineProps<{
  /**
   * 当前激活的标签
   */
  activeTab: string;
}>();

/**
 * 组件事件
 */
const emit = defineEmits<{
  /**
   * 菜单选择事件
   * @param key 选中的菜单键值
   */
  (e: 'menu-select', key: string): void;
}>();

/**
 * 处理菜单选择
 * @param key 选中的菜单键值
 */
const handleMenuSelect = (key: string) => {
  emit('menu-select', key);
};
</script>

<style lang="scss" scoped>
.profile-sidebar {
  width: 200px;

  // 响应式布局
  @media (max-width: 768px) {
    width: 100%;
  }
}

.profile-menu {
  border-radius: 4px;
  
  // 菜单样式优化
  .el-menu-item {
    transition: all 0.3s ease;
    
    &:hover {
      background-color: rgba(0, 0, 0, 0.05);
    }
    
    &.is-active {
      background-color: rgba(64, 158, 255, 0.1);
    }
  }
}
</style>
