<template>
  <div class="user-home">
    <div class="welcome-banner">
      <h1 class="welcome-title">欢迎回来！</h1>
      <p class="welcome-subtitle">{{ welcomeText }}</p>
    </div>

    <el-row :gutter="20" class="info-cards">
      <el-col :xs="24" :md="12">
        <el-card class="time-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon time-icon">
              <el-icon :size="48"><Clock /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">当前时间</div>
              <div class="current-time">{{ currentTime }}</div>
              <div class="current-date">{{ currentDate }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="weather-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon weather-icon">
              <el-icon :size="48"><Sunny /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">今日天气</div>
              <div class="weather-temp">23°C</div>
              <div class="weather-desc">晴朗 · 微风</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="tips-card" shadow="hover">
      <div class="tips-content">
        <el-icon class="tips-icon" :size="32"><Star /></el-icon>
        <div class="tips-text">
          <div class="tips-title">今日小贴士</div>
          <div class="tips-desc">
            今天天气不错，适合出门散步，记得做好防晒哦！
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="content-section">
      <el-col :xs="24" :lg="12">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">我发起的订单</span>
              <el-button type="primary" link size="small" @click="goToOrders"
                >查看全部</el-button
              >
            </div>
          </template>
          <div class="my-orders">
            <div
              v-for="order in myPublishedOrders"
              :key="order.id"
              class="order-item"
            >
              <div class="order-icon">
                <el-icon :size="24"><Document /></el-icon>
              </div>
              <div class="order-content">
                <div class="order-title">{{ order.requirement || "订单" }}</div>
                <div class="order-meta">
                  <span class="order-price">¥{{ order.orderAmount }}</span>
                  <el-tag
                    :type="getOrderStatusType(order.orderStatus)"
                    size="small"
                  >
                    {{ getOrderStatusText(order.orderStatus) }}
                  </el-tag>
                </div>
              </div>
            </div>
            <el-empty
              v-if="myPublishedOrders.length === 0"
              description="暂无发起的订单"
              :image-size="80"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">我接受的订单</span>
              <el-button type="primary" link size="small" @click="goToOrders"
                >查看全部</el-button
              >
            </div>
          </template>
          <div class="my-orders">
            <div
              v-for="order in myAcceptedOrders"
              :key="order.id"
              class="order-item"
            >
              <div class="order-icon">
                <el-icon :size="24"><Document /></el-icon>
              </div>
              <div class="order-content">
                <div class="order-title">{{ order.requirement || "订单" }}</div>
                <div class="order-meta">
                  <span class="order-price">¥{{ order.orderAmount }}</span>
                  <el-tag
                    :type="getOrderStatusType(order.orderStatus)"
                    size="small"
                  >
                    {{ getOrderStatusText(order.orderStatus) }}
                  </el-tag>
                </div>
              </div>
            </div>
            <el-empty
              v-if="myAcceptedOrders.length === 0"
              description="暂无接受的订单"
              :image-size="80"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Clock, Sunny, Star, Document } from "@element-plus/icons-vue";
import { getOrderPage, type Order } from "@/api/order";

const router = useRouter();

const currentTime = ref("");
const timer = ref<any>(null);
const myPublishedOrders = ref<Order[]>([]);
const myAcceptedOrders = ref<Order[]>([]);
const loading = ref(false);

const updateTime = () => {
  const now = new Date();
  const hours = now.getHours().toString().padStart(2, "0");
  const minutes = now.getMinutes().toString().padStart(2, "0");
  const seconds = now.getSeconds().toString().padStart(2, "0");
  currentTime.value = `${hours}:${minutes}:${seconds}`;
};

const currentDate = computed(() => {
  const now = new Date();
  const options: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  };
  return now.toLocaleDateString("zh-CN", options);
});

const welcomeText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return "夜深了，早点休息哦";
  if (hour < 9) return "早上好，新的一天开始了！";
  if (hour < 12) return "上午好，工作学习顺利！";
  if (hour < 14) return "中午好，记得吃午饭！";
  if (hour < 17) return "下午好，来杯咖啡吧！";
  if (hour < 20) return "傍晚好，辛苦了一天！";
  return "晚上好，放松一下吧！";
});

const getOrderStatusType = (status: number | undefined) => {
  const map: Record<number, any> = {
    0: "warning",
    1: "primary",
    2: "success",
    3: "info",
  };
  return map[status || 0] || "info";
};

const getOrderStatusText = (status: number | undefined) => {
  const map: Record<number, string> = {
    0: "待接单",
    1: "已接单",
    2: "已完成",
    3: "已取消",
  };
  return map[status || 0] || "未知";
};

const loadMyOrders = async () => {
  loading.value = true;
  try {
    const res = await getOrderPage({ page: 1, pageSize: 10 });
    if (res?.code === 200 && res?.data?.records) {
      const allOrders = res.data.records;
      myPublishedOrders.value = allOrders.slice(0, 5);
      myAcceptedOrders.value = allOrders.slice(0, 5);
    } else {
      myPublishedOrders.value = [
        {
          id: 1,
          requirement: "帮我取个快递",
          orderAmount: 10,
          orderStatus: 0,
        },
        {
          id: 2,
          requirement: "打印一份资料",
          orderAmount: 5,
          orderStatus: 2,
        },
      ];
      myAcceptedOrders.value = [
        {
          id: 3,
          requirement: "食堂带饭",
          orderAmount: 8,
          orderStatus: 1,
        },
      ];
    }
  } catch (err) {
    console.error("加载订单失败:", err);
    myPublishedOrders.value = [
      {
        id: 1,
        requirement: "帮我取个快递",
        orderAmount: 10,
        orderStatus: 0,
      },
      {
        id: 2,
        requirement: "打印一份资料",
        orderAmount: 5,
        orderStatus: 2,
      },
    ];
    myAcceptedOrders.value = [
      {
        id: 3,
        requirement: "食堂带饭",
        orderAmount: 8,
        orderStatus: 1,
      },
    ];
  } finally {
    loading.value = false;
  }
};

const goToOrders = () => {
  router.push("/user/orders");
};

onMounted(() => {
  updateTime();
  timer.value = setInterval(updateTime, 1000);
  loadMyOrders();
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
  }
});
</script>

<style lang="scss" scoped>
.user-home {
  min-height: 100%;
}

.welcome-banner {
  padding: 20px;
  background: linear-gradient(135deg, #409eff, #3a8ee6);
  border-radius: 8px;
  color: #fff;
  margin-bottom: 20px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px;
}

.welcome-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.info-cards {
  margin-bottom: 20px;
}

.time-card,
.weather-card {
  height: 100%;
  border-radius: 8px;
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.card-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.time-icon {
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.1),
    rgba(64, 158, 255, 0.2)
  );
  color: #409eff;
}

.weather-icon {
  background: linear-gradient(
    135deg,
    rgba(230, 162, 60, 0.1),
    rgba(230, 162, 60, 0.2)
  );
  color: #e6a23c;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.current-time {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
  font-family: "Courier New", monospace;
}

.current-date {
  font-size: 16px;
  color: #606266;
}

.weather-temp {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.weather-desc {
  font-size: 16px;
  color: #606266;
}

.tips-card {
  border-radius: 8px;
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.tips-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.tips-icon {
  color: #e6a23c;
  flex-shrink: 0;
  margin-top: 4px;
}

.tips-text {
  flex: 1;
}

.tips-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.tips-desc {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
}

.content-section {
  margin-bottom: 20px;
}

.section-card {
  height: 100%;
  border-radius: 8px;
  margin-bottom: 16px;

  :deep(.el-card__header) {
    padding: 16px 20px;
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.my-orders {
  padding: 0 20px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;

  &:last-child {
    border-bottom: none;
  }
}

.order-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.1),
    rgba(64, 158, 255, 0.2)
  );
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.order-content {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.order-price {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

@media (max-width: 768px) {
  .welcome-banner {
    padding: 16px;
  }

  .welcome-title {
    font-size: 20px;
  }

  .card-content {
    flex-direction: column;
    text-align: center;
  }

  .current-time,
  .weather-temp {
    font-size: 20px;
  }

  .order-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
