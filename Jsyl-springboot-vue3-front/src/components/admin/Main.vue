<template>
  <div class="main-page">
    <div class="page-header">
      <div class="welcome-section">
        <h1 class="welcome-title">欢迎回来，管理员</h1>
        <p class="welcome-subtitle">今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" size="large" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计数据 - 条状设计 -->
    <div class="stats-section">
      <div class="stats-header">
        <h2 class="section-title">系统概览</h2>
      </div>
      <div class="stats-bars">
        <div class="stat-bar" v-for="stat in statistics" :key="stat.label">
          <div class="stat-bar-left">
            <div class="stat-bar-icon" :class="stat.type">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-bar-info">
              <div class="stat-bar-label">{{ stat.label }}</div>
              <div class="stat-bar-trend" :class="stat.trendType">
                <el-icon :size="14">
                  <component :is="stat.trendIcon" />
                </el-icon>
                <span>{{ stat.trendText }}</span>
              </div>
            </div>
          </div>
          <div class="stat-bar-right">
            <div class="stat-bar-value">{{ stat.value }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据变化趋势图表 -->
    <div class="charts-section">
      <div class="charts-header">
        <h2 class="section-title">数据变化趋势</h2>
      </div>
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-card-header">
                <span class="chart-title">用户数变化</span>
              </div>
            </template>
            <v-chart class="chart" :option="userChartOption" autoresize />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-card-header">
                <span class="chart-title">订单总数变化</span>
              </div>
            </template>
            <v-chart class="chart" :option="orderChartOption" autoresize />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-card-header">
                <span class="chart-title">帖子总数变化</span>
              </div>
            </template>
            <v-chart class="chart" :option="postChartOption" autoresize />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-card-header">
                <span class="chart-title">交易金额变化</span>
              </div>
            </template>
            <v-chart class="chart" :option="amountChartOption" autoresize />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import {
  Refresh,
  User,
  ShoppingCart,
  Money,
  ChatDotRound,
  ArrowUp,
  ArrowDown,
} from "@element-plus/icons-vue";
import { getUserList, getOrderList, getPostList, getStatisticsOverview, getStatisticsTrend } from "@/api/admin/user";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
} from "echarts/components";

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
]);

// 日期计算
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

// 统计数据
const statistics = ref([
  {
    label: "总用户数",
    value: "0",
    icon: User,
    type: "primary",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "加载中",
  },
  {
    label: "订单总数",
    value: "0",
    icon: ShoppingCart,
    type: "success",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "加载中",
  },
  {
    label: "交易金额",
    value: "¥0",
    icon: Money,
    type: "warning",
    trendType: "down",
    trendIcon: ArrowDown,
    trendText: "加载中",
  },
  {
    label: "帖子总数",
    value: "0",
    icon: ChatDotRound,
    type: "info",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "加载中",
  },
]);

const loadStatistics = async () => {
  try {
    // 加载概览数据
    const overviewRes = await getStatisticsOverview();
    if (overviewRes.code === 200 && overviewRes.data) {
      const data = overviewRes.data;
      statistics.value[0].value = String(data.userCount || 0);
      statistics.value[1].value = String(data.orderCount || 0);
      statistics.value[2].value = "¥" + String(data.totalAmount || 0);
      statistics.value[3].value = String(data.postCount || 0);
    }

    // 加载趋势数据
    const trendRes = await getStatisticsTrend();
    if (trendRes.code === 200 && trendRes.data) {
      const trendData = trendRes.data;

      // 更新图表数据
      userChartOption.value = getChartOption("用户数", trendData.userTrend, "#409EFF");
      orderChartOption.value = getChartOption("订单数", trendData.orderTrend, "#67C23A");
      postChartOption.value = getChartOption("帖子数", trendData.postTrend, "#909399");
      amountChartOption.value = getAmountChartOption("交易金额", trendData.amountTrend, "#E6A23C");
    }
  } catch (error) {
    console.error("加载统计数据失败:", error);
  }
};

// 通用图表配置
const getChartOption = (name: string, data: any[], color: string) => {
  return {
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: { type: "category", boundaryGap: false, data: data.map((item: any) => item.date) },
    yAxis: { type: "value" },
    series: [{
      name,
      type: "line",
      smooth: true,
      data: data.map((item: any) => item.value),
      areaStyle: { color: color + "33" },
      itemStyle: { color },
      lineStyle: { width: 3 },
    }],
  };
};

// 金额图表配置
const getAmountChartOption = (name: string, data: any[], color: string) => {
  return {
    tooltip: {
      trigger: "axis",
      formatter: (params: any) => `${params[0].name}<br/>交易金额: ¥${params[0].value}`
    },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: { type: "category", boundaryGap: false, data: data.map((item: any) => item.date) },
    yAxis: { type: "value" },
    series: [{
      name,
      type: "line",
      smooth: true,
      data: data.map((item: any) => item.value),
      areaStyle: { color: color + "33" },
      itemStyle: { color },
      lineStyle: { width: 3 },
    }],
  };
};

// 用户管理数据
const userSearchQuery = ref("");
const userCurrentPage = ref(1);
const userPageSize = ref(10);
const userTotal = ref(0);
const userTableData = ref<any[]>([]);
const userLoading = ref(false);

const loadUserList = async () => {
  userLoading.value = true;
  try {
    const res = await getUserList();
    if (res.data) {
      userTableData.value = res.data.map((user: any) => ({
        id: user.id,
        username: user.account,
        nickname: user.nickname,
        phone: user.phone,
        role: user.role,
        status: user.role === 0 ? "disabled" : "active",
      }));
      userTotal.value = res.data.length;
    }
  } catch (error) {
    console.error("加载用户列表失败:", error);
    ElMessage.error("加载用户列表失败");
  } finally {
    userLoading.value = false;
  }
};

onMounted(() => {
  loadStatistics();
});

// 刷新数据
const handleRefresh = () => {
  loadStatistics();
  ElMessage.success("数据已刷新");
};

// 图表数据 - 最近7天
const last7Days = computed(() => {
  const days = [];
  for (let i = 6; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    days.push(`${date.getMonth() + 1}/${date.getDate()}`);
  }
  return days;
});

// 生成模拟数据（实际项目中应从后端获取历史数据）
// 图表配置 - 使用ref
const userChartOption = ref({});
const orderChartOption = ref({});
const postChartOption = ref({});
const amountChartOption = ref({});
</script>

<style lang="scss" scoped>
.main-page {
  padding: var(--spacing-xl);
  background-color: var(--bg-secondary);
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-xl);
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--primary-active)
  );
  border-radius: var(--border-radius-lg);
  color: #fff;
  box-shadow: var(--shadow-medium);
}

.welcome-section {
  .welcome-title {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    margin: 0 0 var(--spacing-xs);
  }

  .welcome-subtitle {
    font-size: var(--font-size-sm);
    opacity: 0.9;
    margin: 0;
  }
}

.quick-actions {
  display: flex;
  gap: var(--spacing-md);

  .el-button {
    border-radius: var(--border-radius-md);
    font-weight: 500;
  }
}

// 统计数据区域 - 条状设计
.stats-section {
  margin-bottom: var(--spacing-xl);

  .stats-header {
    margin-bottom: var(--spacing-lg);

    .section-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .stats-bars {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .stat-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: var(--spacing-lg);
    background: var(--bg-primary);
    border-radius: var(--border-radius-md);
    box-shadow: var(--shadow-light);
    transition: all var(--transition-normal);

    &:hover {
      box-shadow: var(--shadow-medium);
      transform: translateX(4px);
    }

    .stat-bar-left {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
    }

    .stat-bar-icon {
      width: 48px;
      height: 48px;
      border-radius: var(--border-radius-md);
      display: flex;
      align-items: center;
      justify-content: center;

      &.primary {
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

      &.info {
        background: rgba(144, 147, 153, 0.1);
        color: var(--info-color);
      }
    }

    .stat-bar-info {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-xs);
    }

    .stat-bar-label {
      font-size: var(--font-size-base);
      font-weight: 500;
      color: var(--text-primary);
    }

    .stat-bar-trend {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
      font-size: var(--font-size-xs);
      font-weight: 500;

      &.up {
        color: var(--success-color);
      }

      &.down {
        color: var(--danger-color);
      }
    }

    .stat-bar-right {
      display: flex;
      align-items: center;
    }

    .stat-bar-value {
      font-size: var(--font-size-xl);
      font-weight: 700;
      color: var(--text-primary);
    }
  }
}

// 图表区域
.charts-section {
  margin-bottom: var(--spacing-xl);

  .charts-header {
    margin-bottom: var(--spacing-lg);

    .section-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .el-row {
    margin-bottom: var(--spacing-lg);
  }

  .chart-card {
    border-radius: var(--border-radius-lg);
    box-shadow: var(--shadow-light);

    .chart-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .chart-title {
        font-size: var(--font-size-lg);
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .chart {
      height: 280px;
      width: 100%;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .main-page {
    padding: var(--spacing-lg);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-lg);
  }

  .quick-actions {
    width: 100%;

    .el-button {
      flex: 1;
    }
  }

  .stat-bar {
    padding: var(--spacing-md);

    .stat-bar-value {
      font-size: var(--font-size-lg);
    }
  }
}

@media (max-width: 768px) {
  .main-page {
    padding: var(--spacing-md);
  }

  .page-header {
    padding: var(--spacing-lg);
  }

  .welcome-title {
    font-size: var(--font-size-xl);
  }

  .section-title {
    font-size: var(--font-size-lg);
  }

  .stat-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);

    .stat-bar-right {
      align-self: flex-end;
    }
  }

  .management-card {
    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: var(--spacing-md);

      .header-actions {
        width: 100%;

        .el-input {
          width: 100% !important;
        }
      }
    }
  }

  .pagination-wrapper {
    justify-content: center;
  }
}
</style>
