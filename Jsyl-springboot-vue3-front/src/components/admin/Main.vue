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

    <!-- 用户管理 -->
    <el-card class="management-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户管理</span>
          <div class="header-actions">
            <el-input
              v-model="userSearchQuery"
              placeholder="搜索用户"
              prefix-icon="Search"
              size="small"
              style="width: 200px"
            />
          </div>
        </div>
      </template>
      <el-table :data="userTableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'primary' : 'info'">
              {{ scope.row.role === "admin" ? "管理员" : "普通用户" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'active' ? 'success' : 'danger'"
            >
              {{ scope.row.status === "active" ? "活跃" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="注册时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleUserRoleChange(scope.row)"
            >
              修改权限
            </el-button>
            <el-button
              :type="scope.row.status === 'active' ? 'danger' : 'success'"
              link
              size="small"
              @click="handleUserStatusChange(scope.row)"
            >
              {{ scope.row.status === "active" ? "禁用" : "启用" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="userCurrentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="userPageSize"
          :total="userTotal"
          layout="total, prev, pager, next"
          @size-change="handleUserSizeChange"
          @current-change="handleUserCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单管理 -->
    <el-card class="management-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">订单管理</span>
          <div class="header-actions">
            <el-input
              v-model="orderSearchQuery"
              placeholder="搜索订单"
              prefix-icon="Search"
              size="small"
              style="width: 200px"
            />
          </div>
        </div>
      </template>
      <el-table :data="orderTableData" stripe style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="150" align="center" />
        <el-table-column
          prop="userId"
          label="用户ID"
          width="100"
          align="center"
        />
        <el-table-column
          prop="type"
          label="订单类型"
          width="120"
          align="center"
        >
          <template #default="scope">
            <el-tag
              :type="scope.row.type === 'service' ? 'primary' : 'success'"
            >
              {{ scope.row.type === "service" ? "服务" : "交易" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100" align="center">
          <template #default="scope"> ¥{{ scope.row.amount }} </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="100" align="center">
          <template #default>
            <el-button type="primary" link size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="orderCurrentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="orderPageSize"
          :total="orderTotal"
          layout="total, prev, pager, next"
          @size-change="handleOrderSizeChange"
          @current-change="handleOrderCurrentChange"
        />
      </div>
    </el-card>

    <!-- 讨论管理 -->
    <el-card class="management-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">讨论管理</span>
          <div class="header-actions">
            <el-input
              v-model="postSearchQuery"
              placeholder="搜索讨论"
              prefix-icon="Search"
              size="small"
              style="width: 200px"
            />
          </div>
        </div>
      </template>
      <el-table :data="postTableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column
          prop="userId"
          label="用户ID"
          width="100"
          align="center"
        />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.type === 'service' ? 'primary' : 'success'"
            >
              {{ scope.row.type === "service" ? "服务" : "交易" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'active' ? 'success' : 'danger'"
            >
              {{ scope.row.status === "active" ? "活跃" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="viewCount"
          label="浏览量"
          width="100"
          align="center"
        />
        <el-table-column
          prop="commentCount"
          label="评论数"
          width="100"
          align="center"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="100" align="center">
          <template #default>
            <el-button type="primary" link size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="postCurrentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="postPageSize"
          :total="postTotal"
          layout="total, prev, pager, next"
          @size-change="handlePostSizeChange"
          @current-change="handlePostCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Refresh,
  User,
  ShoppingCart,
  Money,
  ChatDotRound,
  ArrowUp,
  ArrowDown,
  Search,
} from "@element-plus/icons-vue";

const router = useRouter();

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
const statistics = computed(() => [
  {
    label: "总用户数",
    value: "1,258",
    icon: User,
    type: "primary",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "+12% 较上月",
  },
  {
    label: "订单总数",
    value: "3,856",
    icon: ShoppingCart,
    type: "success",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "+8% 较上月",
  },
  {
    label: "交易金额",
    value: "¥125,680",
    icon: Money,
    type: "warning",
    trendType: "down",
    trendIcon: ArrowDown,
    trendText: "-3% 较上月",
  },
  {
    label: "活跃用户",
    value: "428",
    icon: ChatDotRound,
    type: "info",
    trendType: "up",
    trendIcon: ArrowUp,
    trendText: "+15% 较昨日",
  },
]);

// 用户管理数据
const userSearchQuery = ref("");
const userCurrentPage = ref(1);
const userPageSize = ref(10);
const userTotal = ref(1258);
const userTableData = ref([
  {
    id: 1,
    username: "admin",
    email: "admin@example.com",
    role: "admin",
    status: "active",
    createTime: "2026-01-01 00:00:00",
  },
  {
    id: 2,
    username: "user001",
    email: "user001@example.com",
    role: "user",
    status: "active",
    createTime: "2026-01-02 10:30:00",
  },
  {
    id: 3,
    username: "user002",
    email: "user002@example.com",
    role: "user",
    status: "active",
    createTime: "2026-01-03 14:20:00",
  },
  {
    id: 4,
    username: "user003",
    email: "user003@example.com",
    role: "user",
    status: "active",
    createTime: "2026-01-04 09:15:00",
  },
  {
    id: 5,
    username: "user004",
    email: "user004@example.com",
    role: "user",
    status: "disabled",
    createTime: "2026-01-05 16:45:00",
  },
]);

// 订单管理数据
const orderSearchQuery = ref("");
const orderCurrentPage = ref(1);
const orderPageSize = ref(10);
const orderTotal = ref(3856);
const orderTableData = ref([
  {
    id: "ORD20260216001",
    userId: 2,
    type: "service",
    amount: 100,
    status: "completed",
    createTime: "2026-02-16 10:20:30",
  },
  {
    id: "ORD20260216002",
    userId: 3,
    type: "trade",
    amount: 250,
    status: "pending",
    createTime: "2026-02-16 11:30:45",
  },
  {
    id: "ORD20260216003",
    userId: 4,
    type: "service",
    amount: 150,
    status: "completed",
    createTime: "2026-02-16 12:15:20",
  },
  {
    id: "ORD20260216004",
    userId: 2,
    type: "trade",
    amount: 80,
    status: "cancelled",
    createTime: "2026-02-16 13:45:10",
  },
  {
    id: "ORD20260216005",
    userId: 3,
    type: "service",
    amount: 200,
    status: "pending",
    createTime: "2026-02-16 14:30:55",
  },
]);

// 讨论管理数据
const postSearchQuery = ref("");
const postCurrentPage = ref(1);
const postPageSize = ref(10);
const postTotal = ref(1520);
const postTableData = ref([
  {
    id: 1,
    userId: 2,
    title: "寻找校园代取快递服务",
    type: "service",
    status: "active",
    viewCount: 120,
    commentCount: 15,
    createTime: "2026-02-16 09:00:00",
  },
  {
    id: 2,
    userId: 3,
    title: "出售二手 textbooks",
    type: "trade",
    status: "active",
    viewCount: 85,
    commentCount: 8,
    createTime: "2026-02-16 10:15:00",
  },
  {
    id: 3,
    userId: 4,
    title: "校园周边美食推荐",
    type: "service",
    status: "active",
    viewCount: 200,
    commentCount: 25,
    createTime: "2026-02-16 11:30:00",
  },
  {
    id: 4,
    userId: 2,
    title: "转让健身房会员卡",
    type: "trade",
    status: "active",
    viewCount: 60,
    commentCount: 5,
    createTime: "2026-02-16 12:45:00",
  },
  {
    id: 5,
    userId: 3,
    title: "招聘校园兼职",
    type: "service",
    status: "disabled",
    viewCount: 150,
    commentCount: 20,
    createTime: "2026-02-16 14:00:00",
  },
]);

// 刷新数据
const handleRefresh = () => {
  ElMessage.success("数据已刷新");
};

// 用户管理方法
const handleUserRoleChange = (user: any) => {
  ElMessageBox.confirm(
    `确定要将 ${user.username} 的权限修改为 ${
      user.role === "admin" ? "普通用户" : "管理员"
    } 吗？`,
    "权限修改",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(() => {
      user.role = user.role === "admin" ? "user" : "admin";
      ElMessage.success("权限修改成功");
    })
    .catch(() => {
      // 取消操作
    });
};

const handleUserStatusChange = (user: any) => {
  const action = user.status === "active" ? "禁用" : "启用";
  ElMessageBox.confirm(
    `确定要${action}用户 ${user.username} 吗？`,
    `${action}用户`,
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: user.status === "active" ? "danger" : "success",
    }
  )
    .then(() => {
      user.status = user.status === "active" ? "disabled" : "active";
      ElMessage.success(`${action}成功`);
    })
    .catch(() => {
      // 取消操作
    });
};

const handleUserSizeChange = (val: number) => {
  userPageSize.value = val;
};

const handleUserCurrentChange = (val: number) => {
  userCurrentPage.value = val;
};

// 订单管理方法
const getOrderStatusType = (status: string): string => {
  switch (status) {
    case "completed":
      return "success";
    case "pending":
      return "warning";
    case "cancelled":
      return "danger";
    default:
      return "info";
  }
};

const getOrderStatusText = (status: string): string => {
  switch (status) {
    case "completed":
      return "已完成";
    case "pending":
      return "待处理";
    case "cancelled":
      return "已取消";
    default:
      return "未知";
  }
};

const handleOrderSizeChange = (val: number) => {
  orderPageSize.value = val;
};

const handleOrderCurrentChange = (val: number) => {
  orderCurrentPage.value = val;
};

// 讨论管理方法
const handlePostSizeChange = (val: number) => {
  postPageSize.value = val;
};

const handlePostCurrentChange = (val: number) => {
  postCurrentPage.value = val;
};

onMounted(() => {
  console.log("管理端首页已加载");
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

// 管理卡片
.management-card {
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .card-title {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--text-primary);
    }
    
    .header-actions {
      display: flex;
      gap: var(--spacing-sm);
    }
  }
  
  .el-table {
    margin: var(--spacing-md) 0;
  }
}

.pagination-wrapper {
  padding: var(--spacing-lg) 0 0;
  display: flex;
  justify-content: flex-end;
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
