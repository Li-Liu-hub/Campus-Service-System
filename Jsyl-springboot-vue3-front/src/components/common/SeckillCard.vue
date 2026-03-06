<template>
  <div class="seckill-card" @click="handleClick">
    <div class="seckill-badge">限时秒杀</div>
    <div class="goods-image">
      <img :src="goods.imageUrl" :alt="goods.title" />
    </div>
    <div class="goods-info">
      <div class="goods-title">{{ goods.title }}</div>
      <div class="goods-desc">{{ goods.description }}</div>
      <div class="price-section">
        <div class="seckill-price">
          <span class="currency">¥</span>
          <span class="amount">{{ goods.seckillPrice }}</span>
        </div>
        <div class="original-price">¥{{ goods.originalPrice }}</div>
      </div>
      <div class="stock-section">
        <el-progress
          :percentage="stockPercentage"
          :color="stockColor"
          :show-text="false"
        />
        <div class="stock-text">
          仅剩 <span class="stock-num">{{ goods.stock }}</span> 件
        </div>
      </div>
      <div class="countdown-section">
        <el-icon><Clock /></el-icon>
        <span class="countdown-text">{{ countdownText }}</span>
      </div>
    </div>
    <el-button
      type="danger"
      class="buy-button"
      :disabled="!canBuy"
      :loading="buying"
      @click.stop="handleBuy"
    >
      {{ buttonText }}
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Clock } from '@element-plus/icons-vue';
import { seckillBuy, type SeckillGoods } from '@/api/seckill';

const props = defineProps<{
  goods: SeckillGoods;
}>();

const emit = defineEmits<{
  (e: 'success'): void;
  (e: 'click', goods: SeckillGoods): void;
}>();

const buying = ref(false);
const remainingSeconds = ref(props.goods.remainingSeconds);
const timer = ref<any>(null);

const stockPercentage = computed(() => {
  const total = props.goods.stock + props.goods.soldCount;
  return total > 0 ? Math.round((props.goods.soldCount / total) * 100) : 0;
});

const stockColor = computed(() => {
  if (stockPercentage.value >= 90) return '#f56c6c';
  if (stockPercentage.value >= 70) return '#e6a23c';
  return '#67c23a';
});

const canBuy = computed(() => {
  return (
    props.goods.stock > 0 &&
    props.goods.userPurchasedCount < props.goods.limitPerUser &&
    remainingSeconds.value > 0
  );
});

const buttonText = computed(() => {
  if (props.goods.stock === 0) return '已抢光';
  if (props.goods.userPurchasedCount >= props.goods.limitPerUser) return '已达限购';
  if (remainingSeconds.value === 0) return '已结束';
  return '立即抢购';
});

const countdownText = computed(() => {
  if (remainingSeconds.value <= 0) return '已结束';

  const days = Math.floor(remainingSeconds.value / 86400);
  const hours = Math.floor((remainingSeconds.value % 86400) / 3600);
  const minutes = Math.floor((remainingSeconds.value % 3600) / 60);
  const seconds = remainingSeconds.value % 60;

  if (days > 0) return `${days}天${hours}小时`;
  if (hours > 0) return `${hours}小时${minutes}分`;
  if (minutes > 0) return `${minutes}分${seconds}秒`;
  return `${seconds}秒`;
});

const handleClick = () => {
  emit('click', props.goods);
};

const handleBuy = async () => {
  if (!canBuy.value) return;

  buying.value = true;
  try {
    const res = await seckillBuy(props.goods.id, 1);
    if (res.code === 200) {
      ElMessage.success(res.data.message);
      emit('success');
    }
  } catch (error: any) {
    ElMessage.error(error.msg || '抢购失败');
  } finally {
    buying.value = false;
  }
};

onMounted(() => {
  timer.value = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--;
    } else {
      clearInterval(timer.value);
    }
  }, 1000);
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
  }
});
</script>

<style lang="scss" scoped>
.seckill-card {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--primary-color);
  }
}

.seckill-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.goods-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  margin-bottom: 12px;
  background: var(--bg-secondary);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.goods-info {
  margin-bottom: 12px;
}

.goods-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.seckill-price {
  color: #ff4d4f;
  font-weight: 700;

  .currency {
    font-size: 16px;
  }

  .amount {
    font-size: 24px;
  }
}

.original-price {
  font-size: 14px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.stock-section {
  margin-bottom: 12px;
}

.stock-text {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;

  .stock-num {
    color: #ff4d4f;
    font-weight: 600;
  }
}

.countdown-section {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.countdown-text {
  color: #ff4d4f;
  font-weight: 600;
}

.buy-button {
  width: 100%;
  height: 40px;
  font-size: 15px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  border: none;

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #ff5252, #e04e5f);
  }

  &:disabled {
    background: var(--bg-tertiary);
    color: var(--text-tertiary);
  }
}
</style>
