<template>
  <div class="user-orders">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">订单中心</h2>
        <p class="page-subtitle">发现附近订单，开启接单之旅</p>
      </div>
      <el-button type="primary" @click="handlePublishOrder">
        <el-icon><Plus /></el-icon>
        发布订单
      </el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="订单号">
          <el-input
            v-model="filterForm.orderNo"
            placeholder="请输入订单号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input
            v-model="filterForm.userName"
            placeholder="请输入用户姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select
            v-model="filterForm.typeId"
            placeholder="全部类型"
            clearable
            @change="handleSearch"
          >
            <el-option label="其他" :value="1" />
            <el-option label="替课" :value="2" />
            <el-option label="帮助打印" :value="3" />
            <el-option label="帮忙购物" :value="4" />
            <el-option label="帮取外卖" :value="5" />
            <el-option label="帮拿快递" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-loading="loading" class="orders-grid">
      <el-empty v-if="orders.length === 0 && !loading" description="暂无订单" />

      <div v-else class="cards-container">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="card-header">
            <div class="order-type">
              <el-icon><Document /></el-icon>
              <span>{{ getTypeText(order.typeId) }}</span>
            </div>
            <el-tag :type="getStatusTagType(order.orderStatus)" size="small">
              {{ getStatusText(order.orderStatus) }}
            </el-tag>
          </div>

          <div class="card-content">
            <h3 class="order-title">订单 #{{ order.orderNo }}</h3>
            <div class="order-detail">
              <div class="detail-item">
                <el-icon><User /></el-icon>
                <span>发布者：{{ order.publisherNickname || "未知" }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Location /></el-icon>
                <span>{{ order.serviceAddress }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Phone /></el-icon>
                <span>{{ order.contactPhone }}</span>
              </div>
              <div class="detail-item requirement">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ order.requirement }}</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <div class="order-price">
              <span class="price-label">酬金</span>
              <span class="price-value"
                >¥{{ formatAmount(order.orderAmount) }}</span
              >
            </div>
            <div class="order-actions">
              <el-button
                type="primary"
                size="small"
                v-if="canAccept(order.orderStatus)"
                @click="handleAcceptOrder(order)"
              >
                <el-icon><CircleCheck /></el-icon>
                立即接单
              </el-button>
              <el-button size="small" link @click="handleViewDetail(order)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">
            <span class="highlight">{{ currentOrder.orderNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单类型">
            {{ getTypeText(currentOrder.typeId) }}
          </el-descriptions-item>
          <el-descriptions-item label="服务地址">
            {{ currentOrder.serviceAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ currentOrder.contactPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="需求描述">
            {{ currentOrder.requirement }}
          </el-descriptions-item>
          <el-descriptions-item label="订单酬金">
            <span class="amount"
              >¥{{ formatAmount(currentOrder.orderAmount) }}</span
            >
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(currentOrder.orderStatus)">
              {{ getStatusText(currentOrder.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ formatTime(currentOrder.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            v-if="currentOrder && canAccept(currentOrder.orderStatus)"
            @click="handleAcceptOrder(currentOrder)"
          >
            立即接单
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="publishDialogVisible"
      title="发布订单"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="publishFormRef"
        :model="publishForm"
        :rules="publishRules"
        label-width="100px"
      >
        <el-form-item label="订单类型" prop="typeId">
          <el-select v-model="publishForm.typeId" placeholder="请选择订单类型">
            <el-option label="其他" :value="1" />
            <el-option label="替课" :value="2" />
            <el-option label="帮助打印" :value="3" />
            <el-option label="帮忙购物" :value="4" />
            <el-option label="帮取外卖" :value="5" />
            <el-option label="帮拿快递" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务地址" prop="serviceAddress">
          <el-select
            v-if="userAddresses.length > 0"
            v-model="selectedAddressId"
            placeholder="选择常用地址"
            clearable
            style="width: 100%; margin-bottom: 8px"
            @change="handleSelectAddress"
          >
            <el-option
              v-for="addr in userAddresses"
              :key="addr.id"
              :label="`${addr.contactName} - ${addr.addressDetail}`"
              :value="addr.id"
            >
              <div style="display: flex; justify-content: space-between">
                <span>{{ addr.contactName }} - {{ addr.addressDetail }}</span>
                <el-tag
                  v-if="addr.isDefault === 1"
                  type="success"
                  size="small"
                  style="margin-left: 8px"
                  >默认</el-tag
                >
              </div>
            </el-option>
          </el-select>
          <el-input
            v-model="publishForm.serviceAddress"
            placeholder="请输入服务地址"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="publishForm.contactPhone"
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="需求描述" prop="requirement">
          <el-input
            v-model="publishForm.requirement"
            type="textarea"
            :rows="4"
            placeholder="请描述您的需求"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="订单酬金" prop="orderAmount">
          <el-input-number
            v-model="publishForm.orderAmount"
            :min="1"
            :max="10000"
            :precision="2"
            :step="5"
            placeholder="请输入订单酬金"
            style="width: 100%"
          />
          <span style="margin-left: 8px; color: #909399">元</span>
        </el-form-item>
        <el-form-item label="要求完成时间" prop="requireTime">
          <el-date-picker
            v-model="publishForm.requireTime"
            type="datetime"
            placeholder="选择要求完成时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            :shortcuts="shortcuts"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitPublish"
            :loading="publishLoading"
          >
            发布订单
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import {
  Search,
  RefreshLeft,
  Document,
  Location,
  Phone,
  ChatDotRound,
  CircleCheck,
  Plus,
  User,
} from "@element-plus/icons-vue";
import {
  getOrderPage,
  publishOrder,
  acceptOrder,
  type Order,
  ORDER_STATUS_TEXT,
  ORDER_TYPE_TEXT,
  OrderStatus,
} from "@/api/order";
import { getUserAddressList } from "@/api/user-address";
import type { UserAddress } from "@/types/entities";

const filterForm = ref({
  typeId: undefined as number | undefined,
  orderNo: "",
  userName: "",
});

const loading = ref(false);
const orders = ref<Order[]>([]);
const currentOrder = ref<Order | null>(null);
const dialogVisible = ref(false);
const publishDialogVisible = ref(false);
const publishFormRef = ref<FormInstance>();
const publishLoading = ref(false);
const total = ref(0);
const userAddresses = ref<UserAddress[]>([]);
const selectedAddressId = ref<number | null>(null);

const pagination = ref({
  page: 1,
  pageSize: 10,
});

const publishForm = ref({
  typeId: undefined as number | undefined,
  serviceAddress: "",
  contactPhone: "",
  requirement: "",
  orderAmount: undefined as number | undefined,
  requireTime: "",
});

const publishRules: FormRules = {
  typeId: [{ required: true, message: "请选择订单类型", trigger: "change" }],
  serviceAddress: [
    { required: true, message: "请输入服务地址", trigger: "blur" },
  ],
  contactPhone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  requirement: [{ required: true, message: "请输入需求描述", trigger: "blur" }],
  orderAmount: [{ required: true, message: "请输入订单酬金", trigger: "blur" }],
};

const shortcuts = [
  {
    text: "1小时后",
    value: () => {
      const date = new Date();
      date.setHours(date.getHours() + 1);
      return date;
    },
  },
  {
    text: "3小时后",
    value: () => {
      const date = new Date();
      date.setHours(date.getHours() + 3);
      return date;
    },
  },
  {
    text: "今天中午前",
    value: () => {
      const date = new Date();
      date.setHours(12, 0, 0, 0);
      return date;
    },
  },
  {
    text: "明天中午前",
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() + 1);
      date.setHours(12, 0, 0, 0);
      return date;
    },
  },
  {
    text: "3天内",
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() + 3);
      return date;
    },
  },
  {
    text: "7天内",
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() + 7);
      return date;
    },
  },
];

const filteredOrders = computed(() => {
  if (!filterForm.value.typeId) {
    return orders.value;
  }
  return orders.value.filter(
    (order) => order.typeId === filterForm.value.typeId
  );
});

const formatAmount = (amount?: number): string => {
  return amount ? amount.toFixed(2) : "0.00";
};

const formatTime = (timeArr: number[] | string | undefined): string => {
  if (!timeArr) return "-";
  if (typeof timeArr === "string") {
    return timeArr || "-";
  }
  if (Array.isArray(timeArr) && timeArr.length >= 6) {
    const [year = 0, month = 1, day = 1, hour = 0, minute = 0, second = 0] =
      timeArr;
    const padZero = (num: number): string => {
      const n = Number(num);
      return isNaN(n) ? "00" : n.toString().padStart(2, "0");
    };
    return `${year}-${padZero(month)}-${padZero(day)} ${padZero(
      hour
    )}:${padZero(minute)}:${padZero(second)}`;
  }
  return "-";
};

const getStatusText = (status?: number): string => {
  if (status === undefined || status === null) return "未知状态";
  return ORDER_STATUS_TEXT[status] || "未知状态";
};

const getTypeText = (typeId?: number): string => {
  return typeId ? ORDER_TYPE_TEXT[typeId] || "未知类型" : "未知类型";
};

const getStatusTagType = (
  status?: number
): "success" | "primary" | "warning" | "info" | "danger" => {
  if (status === undefined || status === null) return "info";
  const typeMap: Record<
    number,
    "success" | "primary" | "warning" | "info" | "danger"
  > = {
    [OrderStatus.PENDING_ACCEPT]: "warning",
    [OrderStatus.ACCEPTED]: "primary",
    [OrderStatus.COMPLETED]: "success",
  };
  return typeMap[status] || "info";
};

const canAccept = (status?: number): boolean => {
  return status === OrderStatus.PENDING_ACCEPT;
};

const loadOrders = async (): Promise<void> => {
  loading.value = true;
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      typeId: filterForm.value.typeId,
      orderNo: filterForm.value.orderNo,
      userName: filterForm.value.userName,
    };
    const res = await getOrderPage(params);
    if (res?.data?.records) {
      orders.value = res.data.records;
      total.value = res.data.total || 0;
    }
  } catch (error: any) {
    console.error("订单请求失败：", error);
    ElMessage.error("网络异常，获取订单数据失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = (): void => {
  pagination.value.page = 1;
  loadOrders();
};

const handleReset = (): void => {
  filterForm.value = {
    typeId: undefined,
    orderNo: "",
    userName: "",
  };
  pagination.value.page = 1;
  loadOrders();
};

const handleSizeChange = (size: number): void => {
  pagination.value.pageSize = size;
  pagination.value.page = 1;
  loadOrders();
};

const handleCurrentChange = (page: number): void => {
  pagination.value.page = page;
  loadOrders();
};

const handleViewDetail = (order: Order): void => {
  currentOrder.value = order;
  dialogVisible.value = true;
};

const handleAcceptOrder = async (order: Order): Promise<void> => {
  if (!order.id) return;

  try {
    await ElMessageBox.confirm(
      `确定接下此订单吗？\n订单酬金：¥${formatAmount(order.orderAmount)}`,
      "接单确认",
      {
        confirmButtonText: "确定接单",
        cancelButtonText: "取消",
        type: "success",
      }
    );

    await acceptOrder(order.id);
    ElMessage.success("接单成功！快去完成订单吧！");
    dialogVisible.value = false;
    loadOrders();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请重试");
    }
  }
};

const handlePublishOrder = (): void => {
  publishForm.value = {
    typeId: undefined,
    serviceAddress: "",
    contactPhone: "",
    requirement: "",
    orderAmount: undefined,
  };
  selectedAddressId.value = null;
  loadUserAddresses();
  publishDialogVisible.value = true;
};

const loadUserAddresses = async () => {
  try {
    const res = await getUserAddressList();
    if (res && res.code === 200 && res.data) {
      userAddresses.value = res.data;
    }
  } catch (error) {
    console.error("获取地址列表失败:", error);
  }
};

const handleSelectAddress = (addressId: number) => {
  const address = userAddresses.value.find((a) => a.id === addressId);
  if (address) {
    publishForm.value.serviceAddress = address.addressDetail || "";
    publishForm.value.contactPhone = address.contactPhone || "";
  }
};

const handleSubmitPublish = async (): Promise<void> => {
  if (!publishFormRef.value) return;

  await publishFormRef.value.validate(async (valid) => {
    if (valid) {
      publishLoading.value = true;
      try {
        await publishOrder({
          typeId: publishForm.value.typeId!,
          serviceAddress: publishForm.value.serviceAddress,
          contactPhone: publishForm.value.contactPhone,
          requirement: publishForm.value.requirement,
          orderAmount: publishForm.value.orderAmount!,
          requireTime: publishForm.value.requireTime || undefined,
        });

        ElMessage.success("订单发布成功！");
        publishDialogVisible.value = false;
        loadOrders();
      } catch (error) {
        ElMessage.error("发布失败，请重试");
      } finally {
        publishLoading.value = false;
      }
    }
  });
};

onMounted(() => {
  loadOrders();
});
</script>

<style lang="scss" scoped>
.user-orders {
  width: 100%;
  min-height: 100%;
  padding: 24px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 4px;
  }

  .page-subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.filter-card {
  background-color: #fff;
  padding: 24px;
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.filter-form {
  :deep(.el-form-item__label) {
    color: #303133 !important;
    font-size: 14px;
  }

  :deep(.el-select) {
    width: 200px;
  }

  :deep(.el-input__inner) {
    color: #303133 !important;
  }

  :deep(.el-select-dropdown__item) {
    color: #303133 !important;
  }
}

.orders-grid {
  min-height: 300px;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.order-type {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.card-content {
  padding: 24px;
  flex: 1;
}

.order-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #606266;

  &.requirement {
    .el-icon {
      margin-top: 2px;
    }
  }
}

.card-footer {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-price {
  display: flex;
  align-items: baseline;
  gap: 4px;

  .price-label {
    color: #909399;
    font-size: 14px;
  }

  .price-value {
    font-size: 20px;
    font-weight: 700;
    color: #f56c6c;
  }
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.order-detail {
  .highlight {
    font-weight: 600;
    color: #409eff;
  }

  .amount {
    font-weight: 600;
    color: #f56c6c;
    font-size: 18px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 768px) {
  .user-orders {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .cards-container {
    grid-template-columns: 1fr;
  }
}
</style>
