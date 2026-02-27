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

    <!-- 订单管理 -->
    <el-card class="management-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">订单管理</span>
        </div>
      </template>
      <el-table
        :data="orderTableData"
        v-loading="orderLoading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column
          prop="userId"
          label="用户ID"
          width="80"
          align="center"
        />
        <el-table-column
          prop="serviceAddress"
          label="服务地址"
          min-width="150"
        />
        <el-table-column
          prop="orderAmount"
          label="金额"
          width="100"
          align="center"
        >
          <template #default="scope"> ¥{{ scope.row.orderAmount }} </template>
        </el-table-column>
        <el-table-column
          prop="orderStatus"
          label="状态"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.orderStatus)">
              {{ getOrderStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="160"
          align="center"
        />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
              v-if="scope.row.orderStatus !== 3 && scope.row.orderStatus !== 2"
              type="danger"
              link
              size="small"
              @click="handleOrderCancel(scope.row)"
            >
              取消
            </el-button>
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
          <span class="card-title">帖子管理</span>
        </div>
      </template>
      <el-table
        :data="postTableData"
        v-loading="postLoading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column
          prop="userId"
          label="用户ID"
          width="80"
          align="center"
        />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="typeId" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.typeId === 1 ? 'primary' : 'success'">
              {{ scope.row.typeId === 1 ? "服务" : "交易" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getPostStatusType(scope.row.status)">
              {{ getPostStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="160"
          align="center"
        />
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button
              type="danger"
              link
              size="small"
              @click="handlePostDelete(scope.row)"
            >
              删除
            </el-button>
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
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Refresh,
  User,
  ShoppingCart,
  Money,
  ChatDotRound,
  ArrowUp,
  ArrowDown,
} from "@element-plus/icons-vue";
import {
  getUserList,
  updateUserRole,
  getOrderList,
  cancelOrder,
  getPostList,
  deletePost,
} from "@/api/admin/user";
import { RoleText } from "@/constants/role";

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
    const [userRes, orderRes, postRes] = await Promise.all([
      getUserList(),
      getOrderList({ page: 1, pageSize: 1 }),
      getPostList({ page: 1, pageSize: 1 }),
    ]);

    if (
      statistics.value &&
      statistics.value[0] &&
      statistics.value[1] &&
      statistics.value[2] &&
      statistics.value[3]
    ) {
      const userCount = userRes.data?.length || 0;
      const orderTotalVal = orderRes.data?.total || 0;
      const postTotalVal = postRes.data?.total || 0;
      statistics.value[0].value = String(userCount);
      statistics.value[1].value = String(orderTotalVal);
      statistics.value[2].value = "¥" + String(orderTotalVal * 100);
      statistics.value[3].value = String(postTotalVal);
    }
  } catch (error) {
    console.error("加载统计数据失败:", error);
  }
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
  loadUserList();
  loadOrderList();
  loadPostList();
  loadStatistics();
});

// 订单管理数据
const orderCurrentPage = ref(1);
const orderPageSize = ref(10);
const orderTotal = ref(0);
const orderTableData = ref<any[]>([]);
const orderLoading = ref(false);

const loadOrderList = async () => {
  orderLoading.value = true;
  try {
    const res = await getOrderList({
      page: orderCurrentPage.value,
      pageSize: orderPageSize.value,
    });
    if (res.data) {
      orderTableData.value = res.data.records || [];
      orderTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("加载订单列表失败:", error);
    ElMessage.error("加载订单列表失败");
  } finally {
    orderLoading.value = false;
  }
};

const getOrderStatusType = (status: number): string => {
  switch (status) {
    case 0:
      return "warning";
    case 1:
      return "success";
    case 2:
      return "info";
    case 3:
      return "danger";
    default:
      return "info";
  }
};

const getOrderStatusText = (status: number): string => {
  switch (status) {
    case 0:
      return "待接单";
    case 1:
      return "已接单";
    case 2:
      return "已完成";
    case 3:
      return "已取消";
    default:
      return "未知";
  }
};

const handleOrderCancel = (order: any) => {
  ElMessageBox.confirm(`确定要取消订单 ${order.orderNo} 吗？`, "取消订单", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelOrder(order.id);
        order.orderStatus = 3;
        ElMessage.success("订单取消成功");
      } catch (error) {
        console.error("取消订单失败:", error);
        ElMessage.error("取消订单失败");
      }
    })
    .catch(() => {});
};

const handleOrderSizeChange = (val: number) => {
  orderPageSize.value = val;
  loadOrderList();
};

const handleOrderCurrentChange = (val: number) => {
  orderCurrentPage.value = val;
  loadOrderList();
};

// 帖子管理数据
const postCurrentPage = ref(1);
const postPageSize = ref(10);
const postTotal = ref(0);
const postTableData = ref<any[]>([]);
const postLoading = ref(false);

const loadPostList = async () => {
  postLoading.value = true;
  try {
    const res = await getPostList({
      page: postCurrentPage.value,
      pageSize: postPageSize.value,
    });
    if (res.data) {
      postTableData.value = res.data.records || [];
      postTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("加载帖子列表失败:", error);
    ElMessage.error("加载帖子列表失败");
  } finally {
    postLoading.value = false;
  }
};

const getPostStatusType = (status: number): string => {
  return status === 1 ? "success" : "danger";
};

const getPostStatusText = (status: number): string => {
  return status === 1 ? "正常" : "禁用";
};

const handlePostDelete = (post: any) => {
  ElMessageBox.confirm(`确定要删除帖子《${post.title}》吗？`, "删除帖子", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deletePost(post.id);
        ElMessage.success("帖子删除成功");
        loadPostList();
      } catch (error) {
        console.error("删除帖子失败:", error);
        ElMessage.error("删除帖子失败");
      }
    })
    .catch(() => {});
};

const handlePostSizeChange = (val: number) => {
  postPageSize.value = val;
  loadPostList();
};

const handlePostCurrentChange = (val: number) => {
  postCurrentPage.value = val;
  loadPostList();
};

// 刷新数据
const handleRefresh = () => {
  loadUserList();
  loadOrderList();
  loadPostList();
  loadStatistics();
  ElMessage.success("数据已刷新");
};

// 用户管理方法
const handleUserRoleChange = (user: any) => {
  const currentRole = user.role;
  const currentRoleText = RoleText[currentRole] || "未知";

  ElMessageBox.confirm(
    `确定要将用户 ${user.username} 的角色修改为 ${
      currentRoleText === "管理员" ? "普通用户" : "管理员"
    } 吗？`,
    "角色修改",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(async () => {
      const newRole = currentRole >= 3 ? 1 : 3;
      try {
        await updateUserRole(user.id, newRole);
        user.role = newRole;
        ElMessage.success("角色修改成功");
      } catch (error) {
        console.error("修改角色失败:", error);
        ElMessage.error("修改角色失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleUserStatusChange = (user: any) => {
  const isDisabled = user.role !== 0;
  const action = isDisabled ? "禁用" : "启用";
  ElMessageBox.confirm(
    `确定要${action}用户 ${user.username} 吗？`,
    `${action}用户`,
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: isDisabled ? "warning" : "success",
    }
  )
    .then(async () => {
      const newRole = isDisabled ? 0 : 1;
      try {
        await updateUserRole(user.id, newRole);
        user.role = newRole;
        user.status = newRole === 0 ? "disabled" : "active";
        ElMessage.success(`${action}成功`);
      } catch (error) {
        console.error("修改状态失败:", error);
        ElMessage.error("修改状态失败");
      }
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
