<template>
  <div class="user-home">
    <div class="welcome-banner">
      <h1 class="welcome-title">欢迎回来！</h1>
      <p class="welcome-subtitle">{{ welcomeText }}</p>
    </div>

    <el-row :gutter="20" class="info-cards">
      <el-col :xs="24" :md="6">
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

      <el-col :xs="24" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon order-icon">
              <el-icon :size="48"><Document /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">我发起的订单</div>
              <div class="card-value">{{ userStats.orderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon accept-icon">
              <el-icon :size="48"><Money /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">我接受的订单</div>
              <div class="card-value">{{ acceptedOrderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon post-icon">
              <el-icon :size="48"><ChatDotRound /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">我发起的帖子</div>
              <div class="card-value">{{ userStats.postCount }}</div>
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
      <el-col :xs="24" :lg="8">
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
                  <span v-if="order.requireTime" class="order-countdown">
                    <el-icon :size="12"><Clock /></el-icon>
                    {{ getCountdown(order.requireTime) }}
                  </span>
                </div>
                <div class="order-users" v-if="order.acceptorId">
                  <span class="user-label">接单人：</span>
                  <span
                    class="user-name"
                    @click="goToChat(order.acceptorId!, order.acceptorNickname)"
                  >
                    {{ order.acceptorNickname || "用户" + order.acceptorId }}
                    <el-icon :size="12"><ChatDotRound /></el-icon>
                  </span>
                </div>
                <div
                  class="order-users"
                  v-if="
                    order.publisherCancel === 1 || order.acceptorCancel === 1
                  "
                >
                  <el-tag type="warning" size="small">等待对方确认取消</el-tag>
                </div>
              </div>
              <div class="order-actions" v-if="order.orderStatus === 0">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleCancelOrder(order.id)"
                  >放弃</el-button
                >
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

      <el-col :xs="24" :lg="8">
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
                  <span
                    v-if="order.requireTime && order.orderStatus === 1"
                    class="order-countdown"
                  >
                    <el-icon :size="12"><Clock /></el-icon>
                    {{ getCountdown(order.requireTime) }}
                  </span>
                </div>
                <div class="order-users">
                  <span class="user-label">发布人：</span>
                  <span
                    class="user-name"
                    @click="goToChat(order.userId!, order.publisherNickname)"
                  >
                    {{ order.publisherNickname || "用户" + order.userId }}
                    <el-icon :size="12"><ChatDotRound /></el-icon>
                  </span>
                </div>
                <div
                  class="order-users"
                  v-if="
                    order.publisherCancel === 1 || order.acceptorCancel === 1
                  "
                >
                  <el-tag type="warning" size="small">等待对方确认取消</el-tag>
                </div>
              </div>
              <div class="order-actions" v-if="order.orderStatus === 1">
                <el-button
                  type="success"
                  size="small"
                  @click="handleCompleteOrder(order.id)"
                  >完成</el-button
                >
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

      <el-col :xs="24" :lg="8">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">我发起的帖子</span>
              <el-button type="primary" link size="small" @click="goToOrders"
                >查看全部</el-button
              >
            </div>
          </template>
          <div class="my-posts">
            <div
              v-for="post in myPosts"
              :key="post.id"
              class="post-item"
              @click="goToPostDetail(post.id)"
            >
              <div class="post-icon">
                <el-icon :size="24"><ChatDotRound /></el-icon>
              </div>
              <div class="post-content">
                <div class="post-title">{{ post.title || "帖子" }}</div>
                <div class="post-meta">
                  <span class="post-time">{{
                    formatTime(post.createTime)
                  }}</span>
                </div>
              </div>
            </div>
            <el-empty
              v-if="myPosts.length === 0"
              description="暂无发起的帖子"
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
import { Document, Money, ChatDotRound, Clock } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getOrderPage,
  getUserStatistics,
  getMyPublishedOrders,
  getMyAcceptedOrders,
  cancelOrderByUser,
  completeOrder,
  type Order,
  type UserStatistics,
} from "@/api/order";
import { getMyPosts, type Post } from "@/api/post";

const router = useRouter();

const currentTime = ref("");
const timer = ref<any>(null);
const myPublishedOrders = ref<Order[]>([]);
const myAcceptedOrders = ref<Order[]>([]);
const myPosts = ref<Post[]>([]);
const loading = ref(false);
const userStats = ref<UserStatistics>({
  orderCount: 0,
  totalAmount: 0,
  postCount: 0,
});

const acceptedOrderCount = ref(0);

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

const formatTime = (time: string | undefined) => {
  if (!time) return "";
  const date = new Date(time);
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${month}-${day}`;
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
    const [publishedRes, acceptedRes, postsRes, statsRes] = await Promise.all([
      getMyPublishedOrders(),
      getMyAcceptedOrders(),
      getMyPosts(),
      getUserStatistics(),
    ]);

    if (publishedRes?.code === 200 && publishedRes?.data) {
      myPublishedOrders.value = publishedRes.data;
    }

    if (acceptedRes?.code === 200 && acceptedRes?.data) {
      myAcceptedOrders.value = acceptedRes.data;
    }

    if (postsRes?.code === 200 && postsRes?.data) {
      myPosts.value = postsRes.data;
    }

    if (statsRes?.code === 200 && statsRes?.data) {
      userStats.value = statsRes.data;
      acceptedOrderCount.value = statsRes.data.acceptedOrderCount || 0;
    }
  } catch (err) {
    console.error("加载数据失败:", err);
  } finally {
    loading.value = false;
  }
};

const goToOrders = () => {
  router.push("/user/orders");
};

const goToPostDetail = (postId: number) => {
  router.push(`/user/posts/${postId}`);
};

const goToChat = (userId: number, nickname?: string) => {
  router.push({
    path: `/user/chat/${userId}`,
    query: { nickname: nickname || "" },
  });
};

const getCountdown = (requireTime: string) => {
  if (!requireTime) return "";
  const now = new Date().getTime();
  const target = new Date(requireTime).getTime();
  const diff = target - now;

  if (diff <= 0) return "已超时";

  const hours = Math.floor(diff / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

  if (hours > 24) {
    const days = Math.floor(hours / 24);
    return `${days}天${hours % 24}小时`;
  }
  return `${hours}小时${minutes}分钟`;
};

const handleCancelOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定要放弃该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await cancelOrderByUser(orderId);
    if (res?.code === 200) {
      ElMessage.success("订单已放弃");
      loadMyOrders();
    } else {
      ElMessage.error(res?.msg || "操作失败");
    }
  } catch (err) {
    if (err !== "cancel") {
      console.error("放弃订单失败:", err);
      ElMessage.error("操作失败");
    }
  }
};

const handleCompleteOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定要完成该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "success",
    });

    const res = await completeOrder(orderId);
    if (res?.code === 200) {
      ElMessage.success("订单已完成");
      loadMyOrders();
    } else {
      ElMessage.error(res?.msg || "操作失败");
    }
  } catch (err) {
    if (err !== "cancel") {
      console.error("完成订单失败:", err);
      ElMessage.error("操作失败");
    }
  }
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
.stat-card {
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

.card-icon.order-icon {
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.1),
    rgba(64, 158, 255, 0.2)
  );
  color: #409eff;
}

.card-icon.accept-icon {
  background: linear-gradient(
    135deg,
    rgba(103, 194, 58, 0.1),
    rgba(103, 194, 58, 0.2)
  );
  color: #67c23a;
}

.card-icon.post-icon {
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

.card-value {
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

.my-posts {
  padding: 0 20px;
}

.post-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f7fa;
  }

  &:last-child {
    border-bottom: none;
  }
}

.post-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: linear-gradient(
    135deg,
    rgba(230, 162, 60, 0.1),
    rgba(230, 162, 60, 0.2)
  );
  color: #e6a23c;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.post-content {
  flex: 1;
  min-width: 0;
}

.post-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-meta {
  margin-top: 4px;
}

.post-time {
  font-size: 12px;
  color: #909399;
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

.order-countdown {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: 8px;
  font-size: 12px;
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.order-actions {
  display: flex;
  gap: 8px;
  margin-left: 8px;
}

.order-users {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
}

.user-label {
  color: #909399;
}

.user-name {
  color: #409eff;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.user-name:hover {
  text-decoration: underline;
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
