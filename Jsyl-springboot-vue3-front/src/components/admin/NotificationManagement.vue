<template>
  <div class="notification-management">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h2>通知管理</h2>
          <el-button type="primary" @click="showBroadcastDialog = true">
            <el-icon><Bell /></el-icon>
            发送全员通知
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="通知列表" name="list">
          <div class="filter-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索通知标题或内容"
              clearable
              style="width: 300px"
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>

            <el-select
              v-model="filterType"
              placeholder="通知类型"
              clearable
              style="width: 150px"
              @change="handleSearch"
            >
              <el-option label="系统通知" :value="1" />
              <el-option label="评论通知" :value="2" />
              <el-option label="订单通知" :value="3" />
            </el-select>

            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>

          <el-table
            v-loading="loading"
            :data="notifications"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" min-width="150" />
            <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTagType(row.type)">
                  {{ getTypeName(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="接收用户" width="120">
              <template #default="{ row }">
                <span v-if="row.userId">用户 {{ row.userId }}</span>
                <el-tag v-else type="info">全员</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isRead ? 'success' : 'warning'">
                  {{ row.isRead ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSearch"
              @current-change="handleSearch"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="发送记录" name="history">
          <el-empty description="功能开发中" :image-size="120" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 发送全员通知对话框 -->
    <el-dialog
      v-model="showBroadcastDialog"
      title="发送全员通知"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="broadcastFormRef"
        :model="broadcastForm"
        :rules="broadcastRules"
        label-width="100px"
      >
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="broadcastForm.type" placeholder="请选择通知类型">
            <el-option label="系统通知" :value="1" />
            <el-option label="评论通知" :value="2" />
            <el-option label="订单通知" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="通知标题" prop="title">
          <el-input
            v-model="broadcastForm.title"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="broadcastForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入通知内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showBroadcastDialog = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleBroadcast">
          发送
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Bell, Search } from '@element-plus/icons-vue';
import { post } from '@/utils/request';
import type { FormInstance, FormRules } from 'element-plus';
import type { Notification } from '@/types/entities';
import { NOTIFICATION_TYPE } from '@/types/entities';

const activeTab = ref('list');
const loading = ref(false);
const sending = ref(false);
const showBroadcastDialog = ref(false);

// 列表数据
const notifications = ref<Notification[]>([]);
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);
const searchKeyword = ref('');
const filterType = ref<number | undefined>(undefined);

// 广播表单
const broadcastFormRef = ref<FormInstance>();
const broadcastForm = reactive({
  type: 1,
  title: '',
  content: '',
});

const broadcastRules: FormRules = {
  type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 2, max: 500, message: '内容长度在 2 到 500 个字符', trigger: 'blur' },
  ],
};

// 获取标签类型
const getTagType = (type?: number): any => {
  switch (type) {
    case 1: return 'info';
    case 2: return 'success';
    case 3: return 'warning';
    default: return 'info';
  }
};

// 获取类型名称
const getTypeName = (type?: number) => {
  return NOTIFICATION_TYPE[type || 0] || '未知';
};

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true;
    // 这里应该调用管理员的通知列表API，暂时使用模拟数据
    notifications.value = [];
    total.value = 0;
  } catch (error) {
    console.error('加载通知列表失败:', error);
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadNotifications();
};

// 发送全员通知
const handleBroadcast = async () => {
  if (!broadcastFormRef.value) return;

  try {
    await broadcastFormRef.value.validate();

    await ElMessageBox.confirm(
      '确定要向所有用户发送此通知吗？',
      '确认发送',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    sending.value = true;
    await post('/jsyl/admin/notification/broadcast', {
      title: broadcastForm.title,
      content: broadcastForm.content,
      type: broadcastForm.type,
    });

    ElMessage.success('全员通知发送成功');
    showBroadcastDialog.value = false;
    broadcastFormRef.value.resetFields();
    loadNotifications();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发送通知失败:', error);
      ElMessage.error('发送失败');
    }
  } finally {
    sending.value = false;
  }
};

onMounted(() => {
  loadNotifications();
});
</script>

<style lang="scss" scoped>
.notification-management {
  .page-card {
    box-shadow: var(--shadow-light);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      margin: 0;
      font-size: var(--font-size-xl);
      color: var(--text-primary);
    }
  }

  .filter-bar {
    display: flex;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);
    flex-wrap: wrap;
  }

  .pagination {
    margin-top: var(--spacing-lg);
    display: flex;
    justify-content: flex-end;
  }
}
</style>
