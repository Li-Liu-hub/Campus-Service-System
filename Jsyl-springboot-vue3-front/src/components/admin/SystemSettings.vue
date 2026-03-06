<template>
  <div class="system-settings">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">系统管理</span>
        </div>
      </template>

      <el-form :model="settingsForm" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settingsForm.systemName" placeholder="请输入系统名称" />
        </el-form-item>

        <el-form-item label="系统描述">
          <el-input
            v-model="settingsForm.systemDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入系统描述"
          />
        </el-form-item>

        <el-form-item label="维护模式">
          <el-switch
            v-model="settingsForm.maintenanceMode"
            active-text="开启"
            inactive-text="关闭"
          />
        </el-form-item>

        <el-form-item label="维护公告">
          <el-input
            v-model="settingsForm.maintenanceNotice"
            type="textarea"
            :rows="3"
            placeholder="请输入维护公告内容"
            :disabled="!settingsForm.maintenanceMode"
          />
        </el-form-item>

        <el-form-item label="用户注册">
          <el-switch
            v-model="settingsForm.allowRegister"
            active-text="允许"
            inactive-text="禁止"
          />
        </el-form-item>

        <el-form-item label="评论功能">
          <el-switch
            v-model="settingsForm.allowComment"
            active-text="开启"
            inactive-text="关闭"
          />
        </el-form-item>

        <el-form-item label="订单功能">
          <el-switch
            v-model="settingsForm.allowOrder"
            active-text="开启"
            inactive-text="关闭"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存设置
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { get, put } from '@/utils/request';
import { API_ADMIN_SYSTEM_SETTINGS } from '@/constants/api';

const loading = ref(false);
const saving = ref(false);

interface SystemSettings {
  id?: number;
  systemName: string;
  systemDesc: string;
  maintenanceMode: number;
  maintenanceNotice: string;
  allowRegister: number;
  allowComment: number;
  allowOrder: number;
}

const defaultSettings: SystemSettings = {
  systemName: '校园服务平台',
  systemDesc: '为广大师生提供便捷的校园服务',
  maintenanceMode: 0,
  maintenanceNotice: '',
  allowRegister: 1,
  allowComment: 1,
  allowOrder: 1
};

const settingsForm = reactive<SystemSettings>({ ...defaultSettings });

const loadSettings = async () => {
  loading.value = true;
  try {
    const res = await get<SystemSettings>(API_ADMIN_SYSTEM_SETTINGS);
    if (res?.code === 200 && res.data) {
      Object.assign(settingsForm, res.data);
    }
  } catch (error) {
    console.error('获取设置失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSave = async () => {
  saving.value = true;
  try {
    const res = await put(API_ADMIN_SYSTEM_SETTINGS, settingsForm);
    if (res?.code === 200) {
      ElMessage.success('保存成功');
    } else {
      ElMessage.error(res?.msg || '保存失败');
    }
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handleReset = () => {
  Object.assign(settingsForm, defaultSettings);
  ElMessage.info('已重置为默认值');
};

onMounted(() => {
  loadSettings();
});
</script>

<style lang="scss" scoped>
.system-settings {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .el-form {
    max-width: 600px;
  }
}
</style>
