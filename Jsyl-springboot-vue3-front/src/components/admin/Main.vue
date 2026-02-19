<template>
  <div class="main-page">
    <div class="page-header">
      <div class="welcome-section">
        <h1 class="welcome-title">欢迎回来，管理员</h1>
        <p class="welcome-subtitle">今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" size="large" @click="goToOrderCenter">
          <el-icon><Document /></el-icon>
          查看订单
        </el-button>
        <el-button size="large" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.label">
        <div class="stat-card" :class="stat.type">
          <div class="stat-icon-wrapper">
            <el-icon :size="32" class="stat-icon">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-trend" :class="stat.trendType">
              <el-icon>
                <component :is="stat.trendIcon" />
              </el-icon>
              <span>{{ stat.trendText }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">订单趋势</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域 - 可集成 ECharts/Chart.js" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近活动</span>
              <el-button link type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div
              v-for="item in activityList"
              :key="item.id"
              class="activity-item"
            >
              <div class="activity-avatar" :class="item.type">
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <div class="activity-info">
                <div class="activity-title">{{ item.title }}</div>
                <div class="activity-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="log-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">操作日志</span>
          <div class="header-actions">
            <el-button size="small">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="logTableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="operator" label="操作人" width="120" align="center" />
        <el-table-column prop="operation" label="操作内容" min-width="200" />
        <el-table-column prop="time" label="操作时间" width="180" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template #default>
            <el-button type="primary" link size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Document,
  Refresh,
  User,
  ShoppingCart,
  Money,
  ChatDotRound,
  ArrowUp,
  ArrowDown,
  Download,
  Check,
  Warning,
  InfoFilled
} from '@element-plus/icons-vue';

const router = useRouter();

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(56);
const chartPeriod = ref('week');

const currentDate = computed(() => {
  const now = new Date();
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  };
  return now.toLocaleDateString('zh-CN', options);
});

const statistics = computed(() => [
  {
    label: '总用户数',
    value: '1,258',
    icon: User,
    type: 'primary',
    trendType: 'up',
    trendIcon: ArrowUp,
    trendText: '+12% 较上月'
  },
  {
    label: '订单总数',
    value: '3,856',
    icon: ShoppingCart,
    type: 'success',
    trendType: 'up',
    trendIcon: ArrowUp,
    trendText: '+8% 较上月'
  },
  {
    label: '交易金额',
    value: '¥125,680',
    icon: Money,
    type: 'warning',
    trendType: 'down',
    trendIcon: ArrowDown,
    trendText: '-3% 较上月'
  },
  {
    label: '活跃用户',
    value: '428',
    icon: ChatDotRound,
    type: 'info',
    trendType: 'up',
    trendIcon: ArrowUp,
    trendText: '+15% 较昨日'
  }
]);

const activityList = ref([
  { id: 1, title: '新用户注册：test001', time: '2分钟前', type: 'success', icon: User },
  { id: 2, title: '订单完成：ORD20260216001', time: '15分钟前', type: 'primary', icon: Check },
  { id: 3, title: '系统配置已更新', time: '1小时前', type: 'warning', icon: Warning },
  { id: 4, title: '新订单待处理：ORD20260216002', time: '2小时前', type: 'info', icon: InfoFilled },
  { id: 5, title: '用户反馈已处理', time: '3小时前', type: 'success', icon: Check }
]);

const logTableData = ref([
  { id: 1, operator: '管理员', operation: '新增用户：test001', time: '2026-02-16 10:20:30' },
  { id: 2, operator: 'admin', operation: '修改系统基础配置', time: '2026-02-16 09:15:22' },
  { id: 3, operator: 'test001', operation: '查看数据报表', time: '2026-02-16 08:30:15' },
  { id: 4, operator: '管理员', operation: '删除过期日志', time: '2026-02-15 18:45:36' },
  { id: 5, operator: 'admin', operation: '配置角色权限', time: '2026-02-15 16:20:10' }
]);

const goToOrderCenter = () => {
  router.push('/home/ordercenter');
};

const handleRefresh = () => {
  ElMessage.success('数据已刷新');
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};

onMounted(() => {
  console.log('Main page mounted');
});
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
  background: linear-gradient(135deg, var(--primary-color), var(--primary-active));
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

.stats-row {
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal);
  margin-bottom: var(--spacing-lg);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-medium);
  }
}

.stat-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  
  .stat-card.primary & {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.2));
    .stat-icon {
      color: var(--primary-color);
    }
  }
  
  .stat-card.success & {
    background: linear-gradient(135deg, rgba(103, 194, 58, 0.1), rgba(103, 194, 58, 0.2));
    .stat-icon {
      color: var(--success-color);
    }
  }
  
  .stat-card.warning & {
    background: linear-gradient(135deg, rgba(230, 162, 60, 0.1), rgba(230, 162, 60, 0.2));
    .stat-icon {
      color: var(--warning-color);
    }
  }
  
  .stat-card.info & {
    background: linear-gradient(135deg, rgba(144, 147, 153, 0.1), rgba(144, 147, 153, 0.2));
    .stat-icon {
      color: var(--info-color);
    }
  }
}

.stat-content {
  flex: 1;
  
  .stat-value {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.2;
  }
  
  .stat-label {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
    margin: var(--spacing-xs) 0;
  }
  
  .stat-trend {
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
}

.content-row {
  margin-bottom: var(--spacing-xl);
}

.chart-card,
.activity-card,
.log-card {
  height: 100%;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  margin-bottom: var(--spacing-xl);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .card-title {
    font-size: var(--font-size-base);
    font-weight: 600;
    color: var(--text-primary);
  }
  
  .header-actions {
    display: flex;
    gap: var(--spacing-sm);
  }
}

.chart-placeholder {
  padding: var(--spacing-3xl) 0;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.activity-list {
  .activity-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    padding: var(--spacing-md) 0;
    border-bottom: 1px solid var(--border-light);
    transition: all var(--transition-fast);
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background-color: var(--bg-tertiary);
      margin: 0 calc(-1 * var(--spacing-lg));
      padding: var(--spacing-md) var(--spacing-lg);
      border-radius: var(--border-radius-md);
    }
  }
}

.activity-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  &.success {
    background: rgba(103, 194, 58, 0.1);
    color: var(--success-color);
  }
  
  &.primary {
    background: rgba(64, 158, 255, 0.1);
    color: var(--primary-color);
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

.activity-info {
  flex: 1;
  min-width: 0;
  
  .activity-title {
    font-size: var(--font-size-sm);
    color: var(--text-primary);
    font-weight: 500;
    margin-bottom: var(--spacing-xs);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .activity-time {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
  }
}

.pagination-wrapper {
  padding: var(--spacing-lg) 0 0;
  display: flex;
  justify-content: flex-end;
}

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
  
  .stat-card {
    padding: var(--spacing-lg);
  }
}
</style>
