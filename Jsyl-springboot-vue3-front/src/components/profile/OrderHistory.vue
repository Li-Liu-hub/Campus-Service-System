<template>
  <div class="order-history">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">{{ title }}</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索订单..."
              prefix-icon="Search"
              clearable
              style="width: 200px"
              @input="handleSearch"
            />
          </div>
        </div>
      </template>
      
      <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
        <el-tab-pane label="全部" name="-1" />
        <el-tab-pane label="待接单" name="0" />
        <el-tab-pane label="进行中" name="1" />
        <el-tab-pane label="已完成" name="2" />
        <el-tab-pane label="已取消" name="3" />
      </el-tabs>
      
      <div class="order-list">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="order-item"
          @click="showOrderDetail(order)"
        >
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          
          <div class="order-body">
            <div class="order-info">
              <div class="order-requirement">{{ order.requirement || '订单详情' }}</div>
              <div class="order-meta">
                <el-tag :type="getStatusType(order.orderStatus)" size="small">
                  {{ getStatusText(order.orderStatus) }}
                </el-tag>
                <span class="order-amount">¥{{ order.orderAmount }}</span>
              </div>
            </div>
            
            <div class="order-actions" @click.stop>
              <el-button
                v-if="order.orderStatus === 0 && !isAccepted"
                type="danger"
                size="small"
                @click="handleCancel(order)"
              >
                取消订单
              </el-button>
              <el-button
                v-if="order.orderStatus === 1 && isAccepted"
                type="success"
                size="small"
                @click="handleComplete(order)"
              >
                完成订单
              </el-button>
            </div>
          </div>
          
          <div class="order-footer" v-if="order.requireTime">
            <el-icon><Clock /></el-icon>
            <span>要求完成时间: {{ formatTime(order.requireTime) }}</span>
          </div>
        </div>
        
        <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
      </div>
      
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <el-dialog
      v-model="detailVisible"
      :title="'订单详情'"
      width="600px"
    >
      <el-descriptions v-if="currentOrder" :column="1" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ getTypeText(currentOrder.typeId) }}</el-descriptions-item>
        <el-descriptions-item label="服务地址">{{ currentOrder.serviceAddress }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="需求描述">{{ currentOrder.requirement }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="amount">¥{{ currentOrder.orderAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.orderStatus)">
            {{ getStatusText(currentOrder.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.requireTime" label="要求完成时间">
          {{ formatTime(currentOrder.requireTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.completeTime" label="完成时间">
          {{ formatTime(currentOrder.completeTime) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder?.orderStatus === 0 && !isAccepted"
          type="danger"
          @click="handleCancel(currentOrder!)"
        >
          取消订单
        </el-button>
        <el-button
          v-if="currentOrder?.orderStatus === 1 && isAccepted"
          type="success"
          @click="handleComplete(currentOrder!)"
        >
          完成订单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Clock, Search } from '@element-plus/icons-vue';
import { getMyPublishedOrders, getMyAcceptedOrders, cancelOrderByUser, completeOrder, ORDER_STATUS_TEXT, ORDER_TYPE_TEXT, type Order } from '@/api/order';

interface Props {
  isAccepted?: boolean;
  title?: string;
}

const props = withDefaults(defineProps<Props>(), {
  isAccepted: false,
  title: '我的订单',
});

const orders = ref<Order[]>([]);
const loading = ref(false);
const searchKeyword = ref('');
const activeStatus = ref('-1');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const detailVisible = ref(false);
const currentOrder = ref<Order | null>(null);

const filteredOrders = computed(() => {
  let result = orders.value;
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(order => 
      order.orderNo?.toLowerCase().includes(keyword) ||
      order.requirement?.toLowerCase().includes(keyword)
    );
  }
  
  if (activeStatus.value !== '-1') {
    const status = parseInt(activeStatus.value);
    result = result.filter(order => order.orderStatus === status);
  }
  
  return result;
});

const getStatusType = (status: number): string => {
  const types: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info',
  };
  return types[status] || 'info';
};

const getStatusText = (status: number): string => {
  return ORDER_STATUS_TEXT[status] || '未知';
};

const getTypeText = (typeId: number | undefined): string => {
  if (!typeId) return '未知';
  return ORDER_TYPE_TEXT[typeId] || '未知';
};

const formatTime = (time: string | number[] | undefined): string => {
  if (!time) return '-';
  if (typeof time === 'string') return time;
  if (Array.isArray(time)) {
    const [year, month, day, hour, minute] = time;
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
  }
  return '-';
};

const loadOrders = async () => {
  loading.value = true;
  try {
    const res = props.isAccepted 
      ? await getMyAcceptedOrders()
      : await getMyPublishedOrders();
    
    if (res?.code === 200 && res.data) {
      orders.value = res.data;
      total.value = res.data.length;
    }
  } catch (error) {
    console.error('加载订单失败:', error);
    ElMessage.error('加载订单失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
};

const handleStatusChange = () => {
  currentPage.value = 1;
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
};

const showOrderDetail = (order: Order) => {
  currentOrder.value = order;
  detailVisible.value = true;
};

const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    
    const res = await cancelOrderByUser(order.id!);
    if (res?.code === 200) {
      ElMessage.success('订单已取消');
      loadOrders();
      detailVisible.value = false;
    } else {
      ElMessage.error(res?.msg || '操作失败');
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('取消订单失败:', err);
      ElMessage.error('操作失败');
    }
  }
};

const handleComplete = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success',
    });
    
    const res = await completeOrder(order.id!);
    if (res?.code === 200) {
      ElMessage.success('订单已完成');
      loadOrders();
      detailVisible.value = false;
    } else {
      ElMessage.error(res?.msg || '操作失败');
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('完成订单失败:', err);
      ElMessage.error('操作失败');
    }
  }
};

onMounted(() => {
  loadOrders();
});
</script>

<style lang="scss" scoped>
.order-history {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    
    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .order-list {
    margin-top: 20px;
  }
  
  .order-item {
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
    }
  }
  
  .order-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 12px;
    color: #909399;
    
    .order-no {
      font-weight: 500;
    }
  }
  
  .order-body {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .order-info {
      flex: 1;
      
      .order-requirement {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 8px;
        color: #303133;
      }
      
      .order-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .order-amount {
          font-size: 16px;
          font-weight: 600;
          color: #f56c6c;
        }
      }
    }
  }
  
  .order-footer {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
    font-size: 12px;
    color: #e6a23c;
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .amount {
    font-size: 18px;
    font-weight: 600;
    color: #f56c6c;
  }
}
</style>
