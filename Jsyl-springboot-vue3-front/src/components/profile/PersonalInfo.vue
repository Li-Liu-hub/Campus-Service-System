<template>
  <div class="personal-info">
    <el-card shadow="hover" class="info-card">
      <template #header>
        <div class="card-header">
          <span class="title">个人资料</span>
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </template>

      <div class="info-content">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userInfo.avatar" class="avatar">
            {{ userInfo.nickname?.charAt(0) || "U" }}
          </el-avatar>
          <div class="avatar-tip">点击头像可更换</div>
        </div>

        <el-descriptions :column="2" border class="info-descriptions">
          <el-descriptions-item label="昵称">
            {{ userInfo.nickname || "未设置" }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ userInfo.username || "未设置" }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ userInfo.phone || "未绑定" }}
          </el-descriptions-item>
          <el-descriptions-item label="校区">
            {{ userInfo.campusName || "未选择" }}
          </el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag :type="statusType">{{ statusText }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ userInfo.createdAt || "未知" }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <el-card shadow="hover" class="stats-card">
      <template #header>
        <span class="title">数据概览</span>
      </template>

      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon order-icon">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ userStats.orderCount }}</div>
            <div class="stat-label">发起订单</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon accept-icon">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ userStats.acceptCount }}</div>
            <div class="stat-label">接受订单</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon post-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ userStats.postCount }}</div>
            <div class="stat-label">发布帖子</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon amount-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ userStats.totalAmount }}</div>
            <div class="stat-label">消费总额</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="editVisible"
      title="编辑个人资料"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="editForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="editForm.nickname"
            placeholder="请输入昵称"
            maxlength="20"
          />
        </el-form-item>

        <el-form-item label="校区" prop="campusId">
          <el-select
            v-model="editForm.campusId"
            placeholder="请选择校区"
            style="width: 100%"
          >
            <el-option
              v-for="campus in campusList"
              :key="campus.id"
              :label="campus.campusName"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="editForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave"
          >保存</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox, FormInstance } from "element-plus";
import { Edit, List, Check, Document, Money } from "@element-plus/icons-vue";
import { getUserInfo, updateUserInfo } from "@/api/user";
import { getUserStatistics } from "@/api/order";
import { getCampusList, type CampusInfo } from "@/api/common";

interface UserInfoData {
  nickname?: string;
  username?: string;
  phone?: string;
  avatar?: string;
  role?: number;
  campusId?: number;
  campusName?: string;
  createdAt?: string;
  accountBalance?: number;
}

interface Props {
  userInfo: UserInfoData;
}

interface Emits {
  (e: "edit"): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const editVisible = ref(false);
const saving = ref(false);
const formRef = ref<FormInstance>();
const campusList = ref<CampusInfo[]>([]);

const editForm = reactive({
  nickname: "",
  campusId: null as number | null,
  phone: "",
});

const userStats = reactive({
  orderCount: 0,
  acceptCount: 0,
  postCount: 0,
  totalAmount: 0,
});

const rules = {
  nickname: [
    { required: true, message: "请输入昵称", trigger: "blur" },
    { min: 2, max: 20, message: "昵称长度为2-20个字符", trigger: "blur" },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
};

const statusType = computed(() => {
  const types: Record<number, string> = {
    0: "danger",
    1: "success",
    2: "warning",
    3: "primary",
    4: "danger",
  };
  return types[props.userInfo.role || 1] || "info";
});

const statusText = computed(() => {
  const texts: Record<number, string> = {
    0: "已限制",
    1: "正常",
    2: "VIP",
    3: "管理员",
    4: "超级管理员",
  };
  return texts[props.userInfo.role || 1] || "正常";
});

const handleEdit = () => {
  editForm.nickname = props.userInfo.nickname || "";
  editForm.campusId = props.userInfo.campusId || null;
  editForm.phone = props.userInfo.phone || "";
  loadCampusList();
  editVisible.value = true;
};

const loadCampusList = async () => {
  try {
    const res = await getCampusList();
    if (res?.code === 200 && res.data) {
      campusList.value = res.data;
    }
  } catch (error) {
    console.error("获取校区列表失败:", error);
  }
};

const handleSave = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    saving.value = true;

    const res = await updateUserInfo(editForm);
    if (res?.code === 200) {
      ElMessage.success("保存成功");
      editVisible.value = false;
      emit("edit");
    } else {
      ElMessage.error(res?.msg || "保存失败");
    }
  } catch (error: any) {
    if (error.name !== "Error") {
      ElMessage.error("保存失败，请重试");
    }
  } finally {
    saving.value = false;
  }
};

const loadUserStats = async () => {
  try {
    const res = await getUserStatistics();
    if (res?.code === 200 && res.data) {
      userStats.orderCount = res.data.orderCount || 0;
      userStats.acceptCount = res.data.acceptCount || 0;
      userStats.postCount = res.data.postCount || 0;
      userStats.totalAmount = res.data.totalAmount || 0;
    }
  } catch (error) {
    console.error("加载用户统计失败:", error);
  }
};

onMounted(() => {
  loadUserStats();
});
</script>

<style lang="scss" scoped>
.personal-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .info-content {
    display: flex;
    gap: 40px;

    @media (max-width: 768px) {
      flex-direction: column;
      align-items: center;
    }
  }

  .avatar-section {
    text-align: center;

    .avatar {
      font-size: 36px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .avatar-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
    }
  }

  .info-descriptions {
    flex: 1;
  }
}

.stats-card {
  .title {
    font-size: 18px;
    font-weight: 600;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: white;

    &.order-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    &.accept-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    &.post-icon {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    &.amount-icon {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    }
  }

  .stat-info {
    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}
</style>
