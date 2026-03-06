<template>
  <div class="operation-logs">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">操作日志</span>
          <div class="header-actions">
            <el-button type="danger" @click="handleClear" :loading="clearing">
              清空日志
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="操作人">
            <el-input
              v-model="searchForm.operator"
              placeholder="请输入操作人"
              clearable
            />
          </el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="searchForm.operationType" placeholder="请选择" clearable>
              <el-option label="全部" value="" />
              <el-option label="用户管理" value="user" />
              <el-option label="订单管理" value="order" />
              <el-option label="帖子管理" value="post" />
              <el-option label="系统设置" value="system" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 日志列表 -->
      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeByOperation(row.operationType)">
              {{ getTypeName(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作描述" min-width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="操作详情" width="500px">
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="操作人">
          {{ currentLog.operator }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          {{ getTypeName(currentLog.operationType) }}
        </el-descriptions-item>
        <el-descriptions-item label="操作描述">
          {{ currentLog.operation }}
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">
          {{ currentLog.ip }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ currentLog.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="请求参数">
          {{ currentLog.requestParams || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="响应结果">
          {{ currentLog.responseResult || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { get, del } from '@/utils/request';
import { API_ADMIN_LOGS_LIST, API_ADMIN_LOGS_DETAIL, API_ADMIN_LOGS_CLEAR } from '@/constants/api';

interface LogItem {
  id: number;
  operatorId: number;
  operatorName: string;
  operationType: string;
  operation: string;
  ipAddress: string;
  createTime: string;
  requestParams?: string;
  responseResult?: string;
}

const loading = ref(false);
const clearing = ref(false);
const detailVisible = ref(false);
const currentLog = ref<LogItem | null>(null);

const searchForm = reactive({
  operator: '',
  operationType: '',
  dateRange: []
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

const logList = ref<LogItem[]>([]);

const getTypeByOperation = (type: string): string => {
  const typeMap: Record<string, string> = {
    user: 'primary',
    order: 'success',
    post: 'warning',
    system: 'info'
  };
  return typeMap[type] || 'info';
};

const getTypeName = (type: string): string => {
  const nameMap: Record<string, string> = {
    user: '用户管理',
    order: '订单管理',
    post: '帖子管理',
    system: '系统设置'
  };
  return nameMap[type] || type;
};

const loadLogs = async () => {
  loading.value = true;
  try {
    let startTime = '';
    let endTime = '';
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      startTime = searchForm.dateRange[0];
      endTime = searchForm.dateRange[1];
    }

    const res = await get(API_ADMIN_LOGS_LIST, {
      operatorName: searchForm.operator || undefined,
      operationType: searchForm.operationType || undefined,
      startTime: startTime || undefined,
      endTime: endTime || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize
    });

    if (res?.code === 200 && res.data) {
      logList.value = res.data.records || [];
      pagination.total = res.data.total || 0;
    }
  } catch (error) {
    console.error('获取日志列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  loadLogs();
};

const handleReset = () => {
  searchForm.operator = '';
  searchForm.operationType = '';
  searchForm.dateRange = [];
  pagination.page = 1;
  loadLogs();
};

const handleDetail = async (row: LogItem) => {
  try {
    const res = await get(`${API_ADMIN_LOGS_DETAIL}/${row.id}`);
    if (res?.code === 200 && res.data) {
      currentLog.value = res.data;
      detailVisible.value = true;
    }
  } catch (error) {
    console.error('获取日志详情失败:', error);
  }
};

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有操作日志吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    clearing.value = true;
    const res = await del(API_ADMIN_LOGS_CLEAR);
    if (res?.code === 200) {
      ElMessage.success('日志已清空');
      loadLogs();
    }
  } catch {
    // 用户取消
  } finally {
    clearing.value = false;
  }
};

const handleSizeChange = () => {
  loadLogs();
};

const handleCurrentChange = () => {
  loadLogs();
};

onMounted(() => {
  loadLogs();
});
</script>

<style lang="scss" scoped>
.operation-logs {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .search-bar {
    margin-bottom: 20px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
