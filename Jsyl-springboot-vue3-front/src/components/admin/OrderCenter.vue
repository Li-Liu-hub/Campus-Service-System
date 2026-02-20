<template>
  <div class="order-center">
    <div class="page-header">
      <h2 class="page-title">订单管理中心</h2>
      <p class="page-subtitle">统一管理所有订单，高效处理业务流程</p>
    </div>

    <!-- 订单统计 - 条状设计 -->
    <div class="stats-section">
      <div class="stats-bars">
        <div class="stat-bar" v-for="stat in orderStats" :key="stat.label">
          <div class="stat-bar-left">
            <div class="stat-bar-icon" :class="stat.type">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-bar-info">
              <div class="stat-bar-label">{{ stat.label }}</div>
            </div>
          </div>
          <div class="stat-bar-right">
            <div class="stat-bar-value">{{ stat.value }}</div>
          </div>
        </div>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="订单号">
          <el-input
            v-model="filterForm.orderNo"
            placeholder="请输入订单号搜索"
            clearable
            style="width: 220px"
            @input="handleSearch"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
            v-model="filterForm.orderStatus"
            placeholder="全部状态"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="(text, value) in ORDER_STATUS_TEXT"
              :key="value"
              :label="text"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select
            v-model="filterForm.typeId"
            placeholder="全部类型"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="(text, value) in ORDER_TYPE_TEXT"
              :key="value"
              :label="text"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="list-header">
          <div class="header-left">
            <span class="header-title">订单列表</span>
            <span class="header-count">共 {{ pagination.total }} 条记录</span>
          </div>
          <div class="header-right">
            <el-button size="small" @click="handleBatchExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
            <el-button type="primary" size="small" @click="handleAddOrder">
              <el-icon><Plus /></el-icon>
              新增订单
            </el-button>
          </div>
        </div>
      </template>

      <div class="table-container">
        <el-table
          :data="orderList"
          stripe
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          :row-class-name="getRowClassName"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="orderNo" label="订单号" width="180" fixed="left">
            <template #default="scope">
              <span class="order-no">{{ scope.row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="typeId" label="订单类型" width="120">
            <template #default="scope">
              <el-tag size="small" type="info">
                {{ getTypeText(scope.row.typeId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="serviceAddress" label="服务地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="requirement" label="需求描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="contactPhone" label="联系电话" width="130" />
          <el-table-column prop="orderAmount" label="订单金额" width="120">
            <template #default="scope">
              <span class="amount">¥{{ formatAmount(scope.row.orderAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="orderStatus" label="订单状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.orderStatus)" size="small" effect="dark">
                {{ getStatusText(scope.row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              <span class="time">{{ formatTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                link
                size="small"
                @click="handleViewDetail(scope.row)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                type="success"
                link
                size="small"
                v-if="canAccept(scope.row.orderStatus)"
                @click="handleAcceptOrder(scope.row)"
              >
                <el-icon><CircleCheck /></el-icon>
                接单
              </el-button>
              <el-button
                type="warning"
                link
                size="small"
                v-if="canComplete(scope.row.orderStatus)"
                @click="handleCompleteOrder(scope.row)"
              >
                <el-icon><SuccessFilled /></el-icon>
                完成
              </el-button>
              <el-button
                type="danger"
                link
                size="small"
                v-if="canCancel(scope.row.orderStatus)"
                @click="handleCancelOrder(scope.row)"
              >
                <el-icon><Delete /></el-icon>
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="800px"
      :close-on-click-modal="false"
      class="order-detail-dialog"
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">
              <span class="highlight">{{ currentOrder.orderNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="订单类型">
              {{ getTypeText(currentOrder.typeId) }}
            </el-descriptions-item>
            <el-descriptions-item label="下单用户ID">
              {{ currentOrder.userId }}
            </el-descriptions-item>
            <el-descriptions-item label="接单用户ID">
              {{ currentOrder.acceptorId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="amount">¥{{ formatAmount(currentOrder.orderAmount) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusTagType(currentOrder.orderStatus)" effect="dark">
                {{ getStatusText(currentOrder.orderStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="2">
              {{ formatTime(currentOrder.createTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4 class="section-title">服务信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="服务地址">
              {{ currentOrder.serviceAddress }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ currentOrder.contactPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="需求描述">
              <div class="requirement-text">{{ currentOrder.requirement }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            v-if="currentOrder && canAccept(currentOrder.orderStatus)"
            @click="handleAcceptOrder(currentOrder)"
          >
            接单
          </el-button>
          <el-button
            type="success"
            v-if="currentOrder && canComplete(currentOrder.orderStatus)"
            @click="handleCompleteOrder(currentOrder)"
          >
            完成订单
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  RefreshLeft,
  Download,
  Plus,
  View,
  CircleCheck,
  SuccessFilled,
  Delete,
  Clock,
  Van,
  CircleCheckFilled,
  Close
} from "@element-plus/icons-vue";

import {
  type OrderPageQueryParams,
  type Order,
  type OrderPageResponse,
  getOrderPage,
  ORDER_STATUS_TEXT,
  ORDER_TYPE_TEXT,
  OrderStatus,
} from "@/api/order";
import type { ApiResponse } from "@/utils/request";

const filterForm = ref({
  orderNo: "",
  typeId: undefined as number | undefined,
  orderStatus: undefined as number | undefined,
});

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const loading = ref(false);
const orderList = ref<Order[]>([]);
const currentOrder = ref<Order | null>(null);
const selectedOrders = ref<Order[]>([]);
const dialogVisible = ref(false);

const orderStats = computed(() => {
  const stats = {
    pending: 0,
    accepted: 0,
    completed: 0,
    cancelled: 0,
  };
  
  orderList.value.forEach(order => {
    switch (order.orderStatus) {
      case OrderStatus.PENDING_ACCEPT:
        stats.pending++;
        break;
      case OrderStatus.ACCEPTED:
        stats.accepted++;
        break;
      case OrderStatus.COMPLETED:
        stats.completed++;
        break;
      case OrderStatus.CANCELLED:
        stats.cancelled++;
        break;
    }
  });

  return [
    { label: "待接单", value: stats.pending, icon: Clock, type: "warning" },
    { label: "已接单", value: stats.accepted, icon: Van, type: "primary" },
    { label: "已完成", value: stats.completed, icon: CircleCheckFilled, type: "success" },
    { label: "已取消", value: stats.cancelled, icon: Close, type: "danger" },
  ];
});

const formatAmount = (amount: number): string => {
  return amount.toFixed(2);
};

const formatTime = (timeArr: number[] | string | undefined): string => {
  if (!timeArr) return "-";
  if (typeof timeArr === "string") {
    return timeArr || "-";
  }
  if (Array.isArray(timeArr) && timeArr.length >= 6) {
    const [year = 0, month = 1, day = 1, hour = 0, minute = 0, second = 0] = timeArr;
    const padZero = (num: number): string => {
      const n = Number(num);
      return isNaN(n) ? "00" : n.toString().padStart(2, "0");
    };
    return `${year}-${padZero(month)}-${padZero(day)} ${padZero(hour)}:${padZero(minute)}:${padZero(second)}`;
  }
  return "-";
};

const getStatusText = (status: number): string => {
  return ORDER_STATUS_TEXT[status] || "未知状态";
};

const getTypeText = (typeId: number): string => {
  return ORDER_TYPE_TEXT[typeId] || "未知类型";
};

const getStatusTagType = (
  status: number
): "success" | "primary" | "warning" | "info" | "danger" => {
  const typeMap: Record<
    number,
    "success" | "primary" | "warning" | "info" | "danger"
  > = {
    [OrderStatus.PENDING_ACCEPT]: "warning",
    [OrderStatus.ACCEPTED]: "primary",
    [OrderStatus.COMPLETED]: "success",
    [OrderStatus.CANCELLED]: "danger",
  };
  return typeMap[status] || "info";
};

const getRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? "even-row" : "odd-row";
};

const canAccept = (status: number): boolean => {
  return status === OrderStatus.PENDING_ACCEPT;
};

const canComplete = (status: number): boolean => {
  return status === OrderStatus.ACCEPTED;
};

const canCancel = (status: number): boolean => {
  return status === OrderStatus.PENDING_ACCEPT || status === OrderStatus.ACCEPTED;
};

const getOrderList = async (): Promise<void> => {
  loading.value = true;
  try {
    const params: OrderPageQueryParams = {
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      orderNo: filterForm.value.orderNo || undefined,
      typeId: filterForm.value.typeId,
      orderStatus: filterForm.value.orderStatus,
    };

    const res: ApiResponse<OrderPageResponse> = await getOrderPage(params);

    if (res.code === 200) {
      orderList.value = Array.isArray(res.data?.records)
        ? res.data.records
        : [];
      pagination.value.total = Number(res.data?.total) || 0;
    } else {
      ElMessage.error(res.msg || "获取订单失败");
    }
  } catch (error: any) {
    console.error("订单请求失败：", error);
    ElMessage.error("网络异常，获取订单数据失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = (): void => {
  pagination.value.currentPage = 1;
  getOrderList();
};

const handleReset = (): void => {
  filterForm.value = {
    orderNo: "",
    typeId: undefined,
    orderStatus: undefined,
  };
  pagination.value.currentPage = 1;
  getOrderList();
};

const handleSizeChange = (val: number): void => {
  pagination.value.pageSize = val;
  getOrderList();
};

const handleCurrentChange = (val: number): void => {
  pagination.value.currentPage = val;
  getOrderList();
};

const handleViewDetail = (row: Order): void => {
  currentOrder.value = row;
  dialogVisible.value = true;
};

const handleAcceptOrder = async (_row: Order): Promise<void> => {
  try {
    await ElMessageBox.confirm("确定接受该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    loading.value = true;
    ElMessage.success("接单成功！");
    getOrderList();
    dialogVisible.value = false;
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请重试");
    }
  } finally {
    loading.value = false;
  }
};

const handleCompleteOrder = async (_row: Order): Promise<void> => {
  try {
    await ElMessageBox.confirm("确定完成该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    loading.value = true;
    ElMessage.success("订单已完成！");
    getOrderList();
    dialogVisible.value = false;
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请重试");
    }
  } finally {
    loading.value = false;
  }
};

const handleCancelOrder = async (_row: Order): Promise<void> => {
  try {
    await ElMessageBox.confirm("确定取消该订单吗？此操作不可恢复！", "警告", {
      confirmButtonText: "确定取消",
      cancelButtonText: "保留",
      type: "error",
    });
    loading.value = true;
    ElMessage.success("订单已取消！");
    getOrderList();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请重试");
    }
  } finally {
    loading.value = false;
  }
};

const handleAddOrder = (): void => {
  ElMessage.info("新增订单功能开发中");
};

const handleBatchExport = (): void => {
  ElMessage.info("导出功能开发中");
};

const handleSelectionChange = (val: Order[]): void => {
  selectedOrders.value = val;
};

onMounted(() => {
  getOrderList();
});
</script>

<style lang="scss" scoped>
.order-center {
  width: 100%;
  min-height: 100%;
  padding: var(--spacing-xl);
  box-sizing: border-box;
  background-color: var(--bg-secondary);
}

.page-header {
  margin-bottom: var(--spacing-xl);
  
  .page-title {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 var(--spacing-xs);
  }
  
  .page-subtitle {
    font-size: var(--font-size-sm);
    color: var(--text-tertiary);
    margin: 0;
  }
}

// 订单统计 - 条状设计
.stats-section {
  margin-bottom: var(--spacing-xl);
  
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
      color: #fff;
      
      &.warning {
        background: linear-gradient(135deg, #ffd89b, #e6a23c);
      }
      
      &.primary {
        background: linear-gradient(135deg, #66b1ff, #409eff);
      }
      
      &.success {
        background: linear-gradient(135deg, #95d475, #67c23a);
      }
      
      &.danger {
        background: linear-gradient(135deg, #f89898, #f56c6c);
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

.filter-card {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.list-card {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  background: var(--bg-primary);
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-tertiary);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.header-title {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-primary);
}

.header-count {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
}

.table-container {
  overflow-x: auto;
  
  :deep(.el-table) {
    --el-table-header-bg-color: var(--bg-tertiary);
    --el-table-header-text-color: var(--text-secondary);
    --el-table-row-hover-bg-color: rgba(64, 158, 255, 0.05);
    
    .order-no {
      font-weight: 500;
      color: var(--primary-color);
    }
    
    .amount {
      font-weight: 600;
      color: var(--danger-color);
    }
    
    .time {
      color: var(--text-secondary);
    }
  }
}

.pagination-wrapper {
  padding: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

.order-detail {
  .detail-section {
    margin-bottom: var(--spacing-xl);
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .section-title {
    font-size: var(--font-size-base);
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 var(--spacing-md);
    padding-left: var(--spacing-sm);
    border-left: 3px solid var(--primary-color);
  }
  
  .highlight {
    font-weight: 600;
    color: var(--primary-color);
  }
  
  .amount {
    font-weight: 600;
    color: var(--danger-color);
    font-size: var(--font-size-lg);
  }
  
  .requirement-text {
    white-space: pre-wrap;
    line-height: 1.6;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

@media (max-width: 1200px) {
  .order-center {
    padding: var(--spacing-lg);
  }
  
  .stats-row .el-col {
    margin-bottom: var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .order-center {
    padding: var(--spacing-md);
  }
  
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .header-right {
    width: 100%;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>
