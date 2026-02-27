<template>
  <div class="user-management">
    <div class="page-header">
      <h2 class="page-title">用户管理中心</h2>
      <p class="page-subtitle">管理所有用户信息及权限设置</p>
    </div>

    <el-card class="table-card" shadow="never">
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="account" label="账号" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column label="角色" min-width="150">
          <template #default="{ row }">
            <el-select
              v-model="row.role"
              placeholder="选择角色"
              @change="handleRoleChange(row)"
              :disabled="row.role === 4"
              class="role-select"
            >
              <el-option
                v-for="item in roleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.permission === 1 ? 'success' : 'danger'">
              {{ row.permission === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="toggleUserStatus(row)"
              :disabled="row.role === 4"
            >
              {{ row.permission === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserList, updateUserRole, updateUserStatus, type AdminUser } from '@/api/admin/user';

const loading = ref(false);
const userList = ref<AdminUser[]>([]);

const roleOptions = [
  { value: 0, label: '被限制用户' },
  { value: 1, label: '普通用户' },
  { value: 2, label: 'VIP用户' },
  { value: 3, label: '管理员' }
];

const loadUserList = async () => {
  loading.value = true;
  try {
    const res = await getUserList();
    if (res.code === 200 && res.data) {
      userList.value = res.data;
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

const handleRoleChange = async (user: AdminUser) => {
  try {
    await ElMessageBox.confirm(
      `确定要将用户 "${user.nickname || user.account}" 的角色修改为 "${getRoleLabel(user.role)}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const res = await updateUserRole(user.id, user.role);
    if (res.code === 200) {
      ElMessage.success('角色修改成功');
    } else {
      ElMessage.error(res.msg || '修改失败');
      loadUserList();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('修改失败');
    }
    loadUserList();
  }
};

const toggleUserStatus = async (user: AdminUser) => {
  const newStatus = user.permission === 1 ? 0 : 1;
  const action = newStatus === 1 ? '启用' : '禁用';
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.nickname || user.account}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const res = await updateUserStatus(user.id, newStatus);
    if (res.code === 200) {
      ElMessage.success(`${action}成功`);
      loadUserList();
    } else {
      ElMessage.error(res.msg || `${action}失败`);
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`);
    }
  }
};

const getRoleLabel = (role: number): string => {
  const option = roleOptions.find(item => item.value === role);
  return option?.label || '未知';
};

onMounted(() => {
  loadUserList();
});
</script>

<style lang="scss" scoped>
.user-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
}

.table-card {
  margin-top: 20px;
}

.role-select {
  width: 120px;
}
</style>
