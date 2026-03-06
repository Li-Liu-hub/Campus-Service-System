<template>
  <div class="seckill-section">
    <div class="section-header">
      <div class="header-left">
        <div class="header-icon">
          <el-icon :size="28"><Lightning /></el-icon>
        </div>
        <div class="header-text">
          <div class="header-title">限时秒杀</div>
          <div class="header-subtitle">超值好物 限量抢购</div>
        </div>
      </div>
      <el-button type="primary" link @click="loadGoods">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="goodsList.length === 0" class="empty-container">
      <el-empty description="暂无秒杀商品" />
    </div>

    <div v-else class="goods-grid">
      <SeckillCard
        v-for="goods in goodsList"
        :key="goods.id"
        :goods="goods"
        @success="handleSuccess"
        @click="handleGoodsClick"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Lightning, Refresh } from '@element-plus/icons-vue';
import SeckillCard from './SeckillCard.vue';
import { getSeckillGoodsList, type SeckillGoods } from '@/api/seckill';

const goodsList = ref<SeckillGoods[]>([]);
const loading = ref(false);

const loadGoods = async () => {
  loading.value = true;
  try {
    const res = await getSeckillGoodsList();
    if (res.code === 200) {
      goodsList.value = res.data;
    }
  } catch (error) {
    ElMessage.error('加载秒杀商品失败');
  } finally {
    loading.value = false;
  }
};

const handleSuccess = () => {
  loadGoods();
};

const handleGoodsClick = (goods: SeckillGoods) => {
  console.log('点击商品', goods);
};

onMounted(() => {
  loadGoods();
});
</script>

<style lang="scss" scoped>
.seckill-section {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe8e8 100%);
  border-radius: var(--border-radius-xl);
  padding: 24px;
  margin-bottom: 24px;
  border: 2px solid #ffcdd2;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.header-title {
  font-size: 22px;
  font-weight: 700;
  color: #ff4d4f;
  margin-bottom: 2px;
}

.header-subtitle {
  font-size: 13px;
  color: #ff7875;
}

.loading-container,
.empty-container {
  padding: 40px 0;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

@media (max-width: 768px) {
  .goods-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>
