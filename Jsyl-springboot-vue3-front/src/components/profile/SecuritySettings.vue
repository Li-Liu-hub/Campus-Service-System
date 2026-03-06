<template>
  <div class="security-settings">
    <el-card shadow="hover">
      <template #header>
        <span class="title">账号安全</span>
      </template>

      <div class="security-list">
        <div class="security-item">
          <div class="security-icon password-icon">
            <el-icon><Lock /></el-icon>
          </div>
          <div class="security-info">
            <div class="security-title">登录密码</div>
            <div class="security-desc">定期修改密码可以提高账号安全性</div>
          </div>
          <el-button type="primary" @click="showPasswordDialog"
            >修改密码</el-button
          >
        </div>

        <div class="security-item">
          <div class="security-icon phone-icon">
            <el-icon><Iphone /></el-icon>
          </div>
          <div class="security-info">
            <div class="security-title">手机绑定</div>
            <div class="security-desc">{{ phone || "未绑定手机" }}</div>
          </div>
          <el-button
            :type="phone ? 'warning' : 'primary'"
            @click="showBindPhoneDialog"
          >
            {{ phone ? "更换手机" : "绑定手机" }}
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <span class="title">登录日志</span>
      </template>

      <div class="login-log">
        <el-table :data="loginLogs" style="width: 100%">
          <el-table-column prop="time" label="登录时间" width="200" />
          <el-table-column prop="ip" label="IP地址" width="150" />
          <el-table-column prop="device" label="登录设备" />
          <el-table-column prop="location" label="登录地点" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag
                :type="row.status === '成功' ? 'success' : 'danger'"
                size="small"
              >
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请确认新密码"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="savingPassword"
          @click="handlePasswordSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="phoneDialogVisible" title="绑定手机" width="450px">
      <el-form
        ref="phoneFormRef"
        :model="phoneForm"
        :rules="phoneRules"
        label-width="100px"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="phoneForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input
            v-model="phoneForm.code"
            placeholder="请输入验证码"
            maxlength="6"
          >
            <template #append>
              <el-button @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? `${countdown}s` : "获取验证码" }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="savingPhone"
          @click="handlePhoneSubmit"
          >确定</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import { Lock, Iphone } from "@element-plus/icons-vue";
import { changePassword } from "@/api/user";

interface Props {
  phone?: string;
}

interface Emits {
  (e: "update-password"): void;
}

const props = withDefaults(defineProps<Props>(), {
  phone: "",
});

const emit = defineEmits<Emits>();

const passwordDialogVisible = ref(false);
const phoneDialogVisible = ref(false);
const savingPassword = ref(false);
const savingPhone = ref(false);
const countdown = ref(0);

const passwordFormRef = ref<FormInstance>();
const phoneFormRef = ref<FormInstance>();

const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const phoneForm = reactive({
  phone: "",
  code: "",
});

const loginLogs = ref([
  {
    time: "2024-01-15 14:30:22",
    ip: "192.168.1.100",
    device: "Chrome 120",
    location: "广东广州",
    status: "成功",
  },
  {
    time: "2024-01-14 09:15:10",
    ip: "192.168.1.101",
    device: "Chrome 120",
    location: "广东广州",
    status: "成功",
  },
  {
    time: "2024-01-13 20:45:33",
    ip: "192.168.1.102",
    device: "Mobile Safari",
    location: "广东深圳",
    status: "成功",
  },
]);

const passwordRules = {
  oldPassword: [{ required: true, message: "请输入当前密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度为6-20个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

const phoneRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
};

const showPasswordDialog = () => {
  Object.assign(passwordForm, {
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  passwordDialogVisible.value = true;
};

const showBindPhoneDialog = () => {
  Object.assign(phoneForm, { phone: "", code: "" });
  phoneDialogVisible.value = true;
};

const sendCode = () => {
  if (!phoneForm.phone) {
    ElMessage.warning("请先输入手机号");
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    ElMessage.warning("请输入正确的手机号");
    return;
  }

  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);

  ElMessage.success("验证码已发送");
};

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return;

  try {
    await passwordFormRef.value.validate();
    savingPassword.value = true;

    const res = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    });

    if (res?.code === 200) {
      ElMessage.success("密码修改成功");
      passwordDialogVisible.value = false;
      emit("update-password");
    } else {
      ElMessage.error(res?.msg || "修改失败");
    }
  } catch (error: any) {
    if (error.name !== "Error") {
      ElMessage.error("修改失败，请重试");
    }
  } finally {
    savingPassword.value = false;
  }
};

const handlePhoneSubmit = async () => {
  if (!phoneFormRef.value) return;

  try {
    await phoneFormRef.value.validate();
    savingPhone.value = true;

    setTimeout(() => {
      ElMessage.success("手机绑定成功");
      phoneDialogVisible.value = false;
      savingPhone.value = false;
    }, 1000);
  } catch (error: any) {
    if (error.name !== "Error") {
      ElMessage.error("绑定失败，请重试");
    }
    savingPhone.value = false;
  }
};

onMounted(() => {});
</script>

<style lang="scss" scoped>
.security-settings {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .title {
    font-size: 18px;
    font-weight: 600;
  }

  .security-list {
    .security-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }
    }

    .security-icon {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;

      &.password-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
      }

      &.phone-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
      }
    }

    .security-info {
      flex: 1;

      .security-title {
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 4px;
      }

      .security-desc {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .login-log {
    :deep(.el-table) {
      font-size: 12px;
    }
  }
}
</style>
