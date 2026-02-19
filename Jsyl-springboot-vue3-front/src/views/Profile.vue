<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="profile-title">个人中心</div>
      </template>

      <div class="profile-content">
        <!-- 侧边导航 -->
        <ProfileSidebar
          :activeTab="activeTab"
          @menu-select="handleMenuSelect"
        />

        <!-- 主内容区 -->
        <div class="profile-main">
          <!-- 加载状态 -->
          <el-skeleton v-if="isLoading" :rows="10" animated />

          <!-- 用户信息模块 -->
          <div v-else-if="activeTab === 'info'" class="profile-section">
            <h2 class="section-title">用户信息</h2>
            <UserInfoCard :userInfo="userInfo" :avatarSize="80" />
            <el-button type="primary" @click="editUserInfo" class="edit-btn">
              <el-icon><Edit /></el-icon>
              编辑信息
            </el-button>
          </div>

          <!-- 常用地址模块 -->
          <div v-else-if="activeTab === 'address'" class="profile-section">
            <h2 class="section-title">常用地址</h2>
            <div class="address-actions">
              <el-button type="primary" @click="showAddAddressDialog">
                <el-icon><Plus /></el-icon>
                新增地址
              </el-button>
            </div>
            <div class="address-list">
              <div
                v-for="address in addresses"
                :key="address.id"
                class="address-item"
              >
                <div class="address-content">
                  <div class="address-header">
                    <span class="address-name">{{ address.contactName }}</span>
                    <span class="address-phone">{{
                      address.contactPhone
                    }}</span>
                    <el-tag
                      v-if="address.isDefault === 1"
                      type="success"
                      size="small"
                      >默认</el-tag
                    >
                  </div>
                  <div class="address-detail">{{ address.addressDetail }}</div>
                </div>
                <div class="address-actions-btn">
                  <el-button
                    type="primary"
                    link
                    size="small"
                    @click="editAddress(address)"
                    >编辑</el-button
                  >
                  <el-button
                    type="danger"
                    link
                    size="small"
                    @click="deleteAddress(address.id)"
                    >删除</el-button
                  >
                  <el-button
                    v-if="!address.isDefault"
                    type="warning"
                    link
                    size="small"
                    @click="setDefaultAddress(address.id)"
                  >
                    设为默认
                  </el-button>
                </div>
              </div>
              <el-empty
                v-if="addresses.length === 0"
                description="暂无常用地址"
                :image-size="80"
              />
            </div>
          </div>

          <!-- 安全管理模块 -->
          <div v-else-if="activeTab === 'security'" class="profile-section">
            <h2 class="section-title">安全管理</h2>
            <SecurityItem
              v-for="item in securityItems"
              :key="item.action"
              :item="item"
              @action="handleSecurityAction"
            />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 编辑用户信息对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户信息" width="500px">
      <el-form
        :model="editForm"
        class="edit-form"
        label-width="100px"
        :rules="editRules"
        ref="editFormRef"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="editForm.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="editForm.email"
            placeholder="请输入邮箱"
            type="email"
          />
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
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitEditForm"
            :loading="isSubmitting"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 地址编辑对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      :title="isEditingAddress ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form
        :model="addressForm"
        class="address-form"
        label-width="100px"
        :rules="addressRules"
        ref="addressFormRef"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="addressForm.receiverName"
            placeholder="请输入收货人姓名"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="receiverPhone">
          <el-input
            v-model="addressForm.receiverPhone"
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="addressDetail">
          <el-input
            v-model="addressForm.addressDetail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitAddressForm"
            :loading="isSubmittingAddress"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="changePasswordDialogVisible"
      title="修改密码"
      width="500px"
    >
      <el-form
        :model="passwordForm"
        class="password-form"
        label-width="120px"
        :rules="passwordRules"
        ref="passwordFormRef"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
            minlength="6"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false"
            >取消</el-button
          >
          <el-button
            type="primary"
            @click="submitPasswordForm"
            :loading="isSubmittingPassword"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElForm, ElMessageBox } from "element-plus";
import { Edit, Plus } from "@element-plus/icons-vue";
import { getUserInfo, updateUserInfo, changePassword } from "@/api/user";
import {
  getUserAddressList,
  addUserAddress,
  updateUserAddress,
  deleteUserAddress,
  setDefaultUserAddress,
} from "@/api/user-address";
import ProfileSidebar from "@/components/common/ProfileSidebar.vue";
import UserInfoCard from "@/components/common/UserInfoCard.vue";
import SecurityItem from "@/components/common/SecurityItem.vue";
import type { UserAddress } from "@/types/entities";

/**
 * 个人中心页面组件
 * 包含用户信息、常用地址、安全管理三个模块
 */

// 响应式数据
const activeTab = ref<string>("info");
const editDialogVisible = ref<boolean>(false);
const addressDialogVisible = ref<boolean>(false);
const changePasswordDialogVisible = ref<boolean>(false);
const isLoading = ref<boolean>(false);
const isSubmitting = ref<boolean>(false);
const isSubmittingAddress = ref<boolean>(false);
const isSubmittingPassword = ref<boolean>(false);
const isEditingAddress = ref<boolean>(false);
const editingAddressId = ref<number | null>(null);

// 表单引用
const editFormRef = ref<InstanceType<typeof ElForm>>();
const addressFormRef = ref<InstanceType<typeof ElForm>>();
const passwordFormRef = ref<InstanceType<typeof ElForm>>();

// 用户信息
const userInfo = reactive({
  username: "",
  email: "",
  phone: "",
  avatar: "",
  createdAt: "",
});

// 编辑表单
const editForm = reactive({
  username: "",
  email: "",
  phone: "",
});

// 地址表单
const addressForm = reactive({
  receiverName: "",
  receiverPhone: "",
  addressDetail: "",
  isDefault: false,
});

// 密码表单
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 地址列表
const addresses = ref<UserAddress[]>([]);

// 表单验证规则
const editRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 2,
      max: 20,
      message: "用户名长度在 2 到 20 个字符",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    {
      validator: (_rule: any, value: string, callback: any) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
          callback(new Error("请输入正确的邮箱地址"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
};

const addressRules = {
  receiverName: [
    { required: true, message: "请输入收货人姓名", trigger: "blur" },
    { min: 2, max: 20, message: "姓名长度在 2 到 20 个字符", trigger: "blur" },
  ],
  receiverPhone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  addressDetail: [
    { required: true, message: "请输入详细地址", trigger: "blur" },
    {
      min: 5,
      max: 200,
      message: "地址长度在 5 到 200 个字符",
      trigger: "blur",
    },
  ],
};

const passwordRules = {
  oldPassword: [{ required: true, message: "请输入当前密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在 6 到 20 个字符", trigger: "blur" },
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

// 安全管理项
const securityItems = computed(() => [
  {
    title: "修改密码",
    description: "定期修改密码可以提高账户安全性",
    buttonText: "修改密码",
    action: "changePassword",
  },
  {
    title: "绑定手机",
    description: "绑定手机可以接收安全通知和验证码",
    buttonText: userInfo.phone ? "更换手机" : "绑定手机",
    action: "bindPhone",
  },
  {
    title: "绑定邮箱",
    description: "绑定邮箱可以用于找回密码和接收通知",
    buttonText: userInfo.email ? "更换邮箱" : "绑定邮箱",
    action: "bindEmail",
  },
]);

/**
 * 处理菜单选择
 * @param key 选中的菜单项
 */
const handleMenuSelect = (key: string) => {
  activeTab.value = key;
  if (key === "address") {
    loadAddresses();
  }
};

/**
 * 处理安全管理操作
 * @param action 操作类型
 */
const handleSecurityAction = (action: string) => {
  switch (action) {
    case "changePassword":
      changePasswordDialogVisible.value = true;
      break;
    case "bindPhone":
      ElMessage.info("绑定手机功能开发中");
      break;
    case "bindEmail":
      ElMessage.info("绑定邮箱功能开发中");
      break;
    default:
      break;
  }
};

/**
 * 打开编辑用户信息对话框
 */
const editUserInfo = () => {
  Object.assign(editForm, userInfo);
  editDialogVisible.value = true;
};

/**
 * 提交编辑表单
 */
const submitEditForm = async () => {
  if (!editFormRef.value) return;

  try {
    await editFormRef.value.validate();

    isSubmitting.value = true;
    const res = await updateUserInfo(editForm);
    if (res && res.code === 200) {
      ElMessage.success("用户信息更新成功");
      editDialogVisible.value = false;
      fetchUserInfo();
    } else {
      ElMessage.error(res?.msg || "用户信息更新失败");
    }
  } catch (error: any) {
    if (error.name === "Error") {
      return;
    }
    console.error("更新用户信息失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  } finally {
    isSubmitting.value = false;
  }
};

/**
 * 加载地址列表
 */
const loadAddresses = async () => {
  try {
    const res = await getUserAddressList();
    if (res && res.code === 200 && res.data) {
      addresses.value = res.data;
    } else {
      ElMessage.error(res?.msg || "获取地址列表失败");
    }
  } catch (error) {
    console.error("获取地址列表失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  }
};

/**
 * 显示新增地址对话框
 */
const showAddAddressDialog = () => {
  isEditingAddress.value = false;
  editingAddressId.value = null;
  Object.assign(addressForm, {
    receiverName: "",
    receiverPhone: "",
    addressDetail: "",
    isDefault: false,
  });
  addressDialogVisible.value = true;
};

/**
 * 编辑地址
 */
const editAddress = (address: UserAddress) => {
  isEditingAddress.value = true;
  editingAddressId.value = address.id as number;
  Object.assign(addressForm, {
    receiverName: address.contactName,
    receiverPhone: address.contactPhone,
    addressDetail: address.addressDetail,
    isDefault: address.isDefault === 1,
  });
  addressDialogVisible.value = true;
};

/**
 * 提交地址表单
 */
const submitAddressForm = async () => {
  if (!addressFormRef.value) return;

  try {
    await addressFormRef.value.validate();

    isSubmittingAddress.value = true;

    const addressData = {
      contactName: addressForm.receiverName,
      contactPhone: addressForm.receiverPhone,
      addressDetail: addressForm.addressDetail,
      isDefault: addressForm.isDefault ? 1 : 0,
    };

    if (isEditingAddress.value) {
      const updateData = {
        id: editingAddressId.value,
        ...addressData,
      };
      const res = await updateUserAddress(updateData);
      if (res && res.code === 200) {
        ElMessage.success("地址更新成功");
        addressDialogVisible.value = false;
        loadAddresses();
      } else {
        ElMessage.error(res?.msg || "地址更新失败");
      }
    } else {
      const res = await addUserAddress(addressData);
      if (res && res.code === 200) {
        ElMessage.success("地址添加成功");
        addressDialogVisible.value = false;
        loadAddresses();
      } else {
        ElMessage.error(res?.msg || "地址添加失败");
      }
    }
  } catch (error: any) {
    if (error.name === "Error") {
      return;
    }
    console.error("操作地址失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  } finally {
    isSubmittingAddress.value = false;
  }
};

/**
 * 删除地址
 */
const deleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定要删除该地址吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await deleteUserAddress(id);
    if (res && res.code === 200) {
      ElMessage.success("地址删除成功");
      loadAddresses();
    } else {
      ElMessage.error(res?.msg || "地址删除失败");
    }
  } catch {
    // 用户取消
  }
};

/**
 * 设为默认地址
 */
const setDefaultAddress = async (id: number) => {
  try {
    const res = await setDefaultUserAddress(id);
    if (res && res.code === 200) {
      ElMessage.success("已设为默认地址");
      loadAddresses();
    } else {
      ElMessage.error(res?.msg || "设置失败");
    }
  } catch (error) {
    console.error("设置默认地址失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  }
};

/**
 * 提交密码表单
 */
const submitPasswordForm = async () => {
  if (!passwordFormRef.value) return;

  try {
    await passwordFormRef.value.validate();

    isSubmittingPassword.value = true;
    const res = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    });
    if (res && res.code === 200) {
      ElMessage.success("密码修改成功");
      changePasswordDialogVisible.value = false;
      Object.assign(passwordForm, {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
    } else {
      ElMessage.error(res?.msg || "密码修改失败");
    }
  } catch (error: any) {
    if (error.name === "Error") {
      return;
    }
    console.error("修改密码失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  } finally {
    isSubmittingPassword.value = false;
  }
};

/**
 * 获取用户信息
 */
const fetchUserInfo = async () => {
  try {
    isLoading.value = true;
    const res = await getUserInfo();
    if (res && res.code === 200 && res.data) {
      const data = res.data as any;
      Object.assign(userInfo, {
        username: data.nickname || data.username || data.account || "",
        email: data.email || "",
        phone: data.phone || "",
        avatar: data.avatar || "",
      });
    } else {
      ElMessage.error(res?.msg || "获取用户信息失败");
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    ElMessage.error("网络异常，请稍后重试");
  } finally {
    isLoading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchUserInfo();
  loadAddresses();
});
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  padding: 20px;
  background-color: #f8f9fa;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  animation: fadeIn 0.3s ease-in-out;

  @media (max-width: 768px) {
    padding: 10px;
  }
}

.profile-card {
  width: 100%;
  max-width: 1000px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  }

  @media (max-width: 768px) {
    margin-top: 20px;
  }
}

.profile-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.profile-content {
  display: flex;
  gap: 20px;

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.profile-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  animation: slideIn 0.3s ease-out;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
}

.edit-btn {
  margin-top: 24px;
  transition: all 0.3s ease;
}

.address-actions {
  margin-bottom: 20px;
}

.address-list {
  .address-item {
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    }

    .address-content {
      flex: 1;
      min-width: 0;

      .address-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;

        .address-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .address-phone {
          font-size: 14px;
          color: #606266;
        }
      }

      .address-detail {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
      }
    }

    .address-actions-btn {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
    }
  }
}

.edit-form {
  max-width: 100%;
}

.address-form {
  max-width: 100%;
}

.password-form {
  max-width: 100%;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

// 动画效果
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
