<template>
  <div class="user-home">
    <ChatBox
      v-model:visible="chatBoxVisible"
      :target-user-id="chatTargetUserId ?? undefined"
      :target-nickname="chatTargetNickname"
      @close="closeChatBox"
    />
    <div class="welcome-banner">
      <h1 class="welcome-title">欢迎回来！</h1>
      <p class="welcome-subtitle">{{ welcomeText }}</p>
    </div>

    <el-row :gutter="20" class="main-widgets">
      <el-col :xs="24" :lg="16">
        <el-row :gutter="20">
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

          <el-col :xs="24" :md="12">
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

          <el-col :xs="24" :md="12">
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
      </el-col>

      <el-col :xs="24" :lg="8">
        <SignInCard />
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-section">
      <el-col :xs="24" :lg="8">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-header-title">我发起的订单</span>
              <el-button type="primary" link size="small" @click="goToProfileTab('orders')"
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
                    order.orderStatus === 1 &&
                    (order.publisherCancel === 1 || order.acceptorCancel === 1)
                  "
                >
                  <el-tag type="warning" size="small">等待对方确认取消</el-tag>
                </div>
              </div>
              <div class="order-actions" v-if="order.orderStatus === 0">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleCancelOrder(order.id!)"
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
              <el-button type="primary" link size="small" @click="goToProfileTab('accepted-orders')"
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
                    order.orderStatus === 1 &&
                    (order.publisherCancel === 1 || order.acceptorCancel === 1)
                  "
                >
                  <el-tag type="warning" size="small">等待对方确认取消</el-tag>
                </div>
              </div>
              <div class="order-actions" v-if="order.orderStatus === 1">
                <el-button
                  type="success"
                  size="small"
                  @click="handleCompleteOrder(order.id!)"
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
              <el-button type="primary" link size="small" @click="goToProfileTab('posts')"
                >查看全部</el-button
              >
            </div>
          </template>
          <div class="my-posts">
            <div
              v-for="post in myPosts"
              :key="post.id"
              class="post-item"
              @click="goToPostDetail(post.id!)"
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

    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-header-title">最近联系</span>
        </div>
      </template>
      <div class="recent-list">
        <div
          v-for="conv in recentConversationsDisplay"
          :key="conv.id"
          class="recent-item"
          @click="goToChat(conv.targetUserId!, conv.targetUserNickname)"
        >
          <div class="recent-avatar">
            <el-avatar :size="40">{{ getUserInitial(conv.targetUserId!) }}</el-avatar>
            <el-badge
              v-if="(conv.unreadCount || 0) > 0"
              :value="conv.unreadCount"
              :max="99"
              class="recent-badge"
            />
          </div>
          <div class="recent-content">
            <div class="recent-top">
              <span class="recent-name">{{ conv.targetUserNickname || `用户${conv.targetUserId}` }}</span>
              <span class="recent-time">{{ formatChatTime(conv.updateTime || conv.createTime) }}</span>
            </div>
            <div class="recent-message">
              {{ conv.lastMessage || "暂无消息" }}
            </div>
          </div>
        </div>
        <el-empty
          v-if="recentConversationsDisplay.length === 0"
          description="暂无最近联系"
          :image-size="80"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Document, Money, ChatDotRound, Clock } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import ChatBox from "@/components/common/ChatBox.vue";
import SignInCard from "@/components/common/SignInCard.vue";
import {
  getUserStatistics,
  getMyPublishedOrders,
  getMyAcceptedOrders,
  cancelOrderByUser,
  completeOrder,
  type Order,
  type UserStatistics,
} from "@/api/order";
import { getMyPosts, type Post } from "@/api/post";
import { getConversationList } from "@/api/conversation";
import type { Conversation } from "@/types/entities";

const router = useRouter();

const currentTime = ref("");
const timer = ref<any>(null);
const myPublishedOrders = ref<Order[]>([]);
const myAcceptedOrders = ref<Order[]>([]);
const myPosts = ref<Post[]>([]);
const recentConversations = ref<Conversation[]>([]);
const loading = ref(false);
const userStats = ref<UserStatistics>({
  orderCount: 0,
  totalAmount: 0,
  postCount: 0,
  acceptedOrderCount: 0,
});

const acceptedOrderCount = ref(0);
const chatBoxVisible = ref(false);
const chatTargetUserId = ref<number | null>(null);
const chatTargetNickname = ref("");
const warnedOrderIds = ref(new Set<number>());
const warningThresholdMs = 60 * 60 * 1000;

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

const formatChatTime = (time: string | undefined) => {
  if (!time) return "";
  const date = new Date(time);
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${hours}:${minutes}`;
};

const getUserInitial = (userId: number) => {
  return userId.toString().charAt(0);
};

const recentConversationsDisplay = computed(() => {
  return recentConversations.value.slice(0, 5);
});

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
    const [publishedRes, acceptedRes, postsRes, statsRes, convRes] = await Promise.all([
      getMyPublishedOrders(),
      getMyAcceptedOrders(),
      getMyPosts(),
      getUserStatistics(),
      getConversationList(),
    ]);

    if (publishedRes?.code === 200 && publishedRes?.data) {
      myPublishedOrders.value = publishedRes.data;
    }

    if (acceptedRes?.code === 200 && acceptedRes?.data) {
      myAcceptedOrders.value = acceptedRes.data;
      checkAcceptedOrderWarnings();
    }

    if (postsRes?.code === 200 && postsRes?.data) {
      myPosts.value = postsRes.data;
    }

    if (statsRes?.code === 200 && statsRes?.data) {
      userStats.value = statsRes.data;
      acceptedOrderCount.value = statsRes.data.acceptedOrderCount || 0;
    }

    if (convRes?.code === 200 && convRes?.data) {
      recentConversations.value = convRes.data;
    }
  } catch (err) {
    console.error("加载数据失败:", err);
  } finally {
    loading.value = false;
  }
};

const goToProfileTab = (tab: string) => {
  router.push({ path: "/user/profile", query: { tab } });
};

const goToPostDetail = (postId: number) => {
  router.push(`/user/posts/${postId}`);
};

const goToChat = (userId: number, nickname?: string) => {
  chatTargetUserId.value = userId;
  chatTargetNickname.value = nickname || "";
  chatBoxVisible.value = true;
};

const closeChatBox = () => {
  chatBoxVisible.value = false;
  chatTargetUserId.value = null;
  chatTargetNickname.value = "";
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

const getRemainingMs = (requireTime: string | undefined) => {
  if (!requireTime) return 0;
  const target = new Date(requireTime).getTime();
  return target - Date.now();
};

const checkAcceptedOrderWarnings = () => {
  myAcceptedOrders.value.forEach((order) => {
    if (order.orderStatus !== 1 || !order.requireTime || !order.id) return;
    if (warnedOrderIds.value.has(order.id)) return;
    const remaining = getRemainingMs(order.requireTime);
    if (remaining > 0 && remaining <= warningThresholdMs) {
      const title = order.requirement || `订单${order.id}`;
      ElMessage.warning(`${title}剩余1小时以内，请尽快完成`);
      warnedOrderIds.value.add(order.id);
    }
  });
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
  timer.value = setInterval(() => {
    updateTime();
    checkAcceptedOrderWarnings();
  }, 1000);
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
  padding: 0;
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  border-radius: var(--border-radius-xl);
  padding: 40px;
  color: #fff;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-lg);

  &::before {
    content: "";
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  &::after {
    content: "";
    position: absolute;
    bottom: -30%;
    left: -5%;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }
}

.welcome-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
  position: relative;
  z-index: 1;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 400;
  position: relative;
  z-index: 1;
}

.main-widgets {
  margin-bottom: 24px;

  .el-col {
    margin-bottom: 20px;
  }

  .el-row {
    margin-bottom: 20px;
  }
}

.stat-card, .time-card {
  border: none;
  border-radius: var(--border-radius-xl);
  transition: all var(--transition-normal);
  height: 100%;
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
  
  :deep(.el-card__body) {
    padding: 24px;
    height: 100%;
    display: flex;
    align-items: center;
  }
}

.card-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 28px;
  transition: all var(--transition-normal);
  
  &.time-icon {
    background-color: var(--primary-light);
    color: var(--primary-color);
  }
  
  &.order-icon {
    background-color: #ecfdf5;
    color: var(--success-color);
  }
  
  &.accept-icon {
    background-color: #fffbeb;
    color: var(--warning-color);
  }
  
  &.post-icon {
    background-color: #fef2f2;
    color: var(--danger-color);
  }
}

.card-info {
  flex: 1;
  
  .card-title {
    font-size: 14px;
    color: var(--text-secondary);
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .card-value, .current-time {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.2;
    font-family: var(--font-family);
    letter-spacing: -0.5px;
  }
  
  .current-date {
    font-size: 13px;
    color: var(--text-tertiary);
    margin-top: 4px;
  }
}

/* 内容区块 */
.section-card {
  border: none;
  border-radius: var(--border-radius-xl);
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  background: var(--bg-primary);
  height: 360px;
  display: flex;
  flex-direction: column;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid var(--border-light);
    padding: 20px 24px;
    flex: 0 0 auto;
  }
  
  :deep(.el-card__body) {
    flex: 1 1 auto;
    overflow: hidden;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .card-header-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      position: relative;
      padding-left: 16px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 20px;
        background-color: var(--primary-color);
        border-radius: 2px;
      }
    }
  }
}

/* 列表项样式 */
.order-item, .post-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-radius: var(--border-radius-lg);
  transition: all var(--transition-fast);
  cursor: pointer;
  margin-bottom: 8px;
  border: 1px solid transparent;
  
  &:hover {
    background-color: var(--bg-secondary);
    border-color: var(--border-light);
  }
  
  .order-icon, .post-icon {
    width: 48px;
    height: 48px;
    border-radius: var(--border-radius-lg);
    background-color: var(--bg-secondary);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-tertiary);
    margin-right: 16px;
    flex-shrink: 0;
    transition: all var(--transition-fast);
  }
  
  &:hover .order-icon, &:hover .post-icon {
    background-color: var(--bg-primary);
    color: var(--primary-color);
    box-shadow: var(--shadow-sm);
  }
  
  .order-content, .post-content {
    flex: 1;
    min-width: 0;
    
    .order-title, .post-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .order-meta, .post-meta {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
      font-size: 12px;
      color: var(--text-secondary);
      
      .order-price {
        color: var(--danger-color);
        font-weight: 600;
        font-size: 14px;
      }
      
      .order-countdown {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        color: var(--warning-color);
        background: #fffbeb;
        padding: 2px 8px;
        border-radius: 4px;
        font-weight: 500;
      }
    }

    .order-users {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 8px;
      font-size: 12px;
      
      .user-label {
        color: var(--text-tertiary);
      }
      
      .user-name {
        color: var(--primary-color);
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 4px;
        font-weight: 500;
        transition: color 0.2s;
        
        &:hover {
          color: var(--primary-hover);
          text-decoration: underline;
        }
      }
    }
  }
  
  .order-actions {
    margin-left: 16px;
    flex-shrink: 0;
    align-self: center;
  }
}

/* 小贴士卡片 */
.tips-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-xl);
  margin-bottom: 32px;
  box-shadow: var(--shadow-sm);
  
  .tips-content {
    display: flex;
    align-items: center;
    padding: 12px;
    
    .tips-icon {
      color: var(--warning-color);
      margin-right: 20px;
      background: #fffbeb;
      padding: 16px;
      border-radius: 50%;
      box-shadow: var(--shadow-sm);
    }
    
    .tips-text {
      .tips-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .tips-desc {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.my-orders,
.my-posts {
  height: 100%;
  overflow-y: auto;
  padding-right: 4px;
}

.recent-item {
  display: flex;
  gap: 16px;
  padding: 12px;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  background: var(--bg-primary);
  transition: all var(--transition-fast);
  cursor: pointer;

  &:hover {
    background: var(--bg-secondary);
    border-color: var(--border-medium);
  }
}

.recent-avatar {
  position: relative;
  flex-shrink: 0;

  .recent-badge {
    position: absolute;
    top: -6px;
    right: -6px;
  }
}

.recent-content {
  flex: 1;
  min-width: 0;

  .recent-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  .recent-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary);
  }

  .recent-time {
    font-size: 12px;
    color: var(--text-tertiary);
  }

  .recent-message {
    font-size: 13px;
    color: var(--text-secondary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

@media screen and (max-width: 768px) {
  .welcome-banner {
    padding: 24px;
    
    .welcome-title {
      font-size: 24px;
    }
  }
  
  .card-content {
    flex-direction: column;
    text-align: center;
    
    .card-icon {
      margin-right: 0;
      margin-bottom: 16px;
    }
  }
}
</style>
