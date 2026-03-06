<template>
  <div class="order-center-wrapper">
    <SeckillSection />

    <div class="order-content">
      <h2>订单中心</h2>
      <p>订单功能开发中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import SeckillSection from './common/SeckillSection.vue';
</script>

<style lang="scss" scoped>
.order-center-wrapper {
  width: 100%;
  min-height: 100%;
  padding: 24px;
}

.order-content {
  h2 {
    color: #303133;
    margin-bottom: 16px;
  }

  p {
    color: #909399;
  }
}
</style>
