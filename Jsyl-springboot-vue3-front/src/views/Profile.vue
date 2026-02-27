<template>
  <div class="profile-container">
    <div class="profile-layout">
      <ProfileSidebar
        :active-tab="activeTab"
        :user-info="sidebarUserInfo"
        :unread-count="unreadMessageCount"
        @menu-select="handleMenuSelect"
      />

      <div class="profile-main">
        <el-scrollbar>
          <div class="main-content">
            <transition name="fade-slide" mode="out-in">
              <div v-if="activeTab === 'info'" key="info">
                <PersonalInfo :user-info="userInfo" @edit="openEditDialog" />
              </div>

              <div v-else-if="activeTab === 'orders'" key="orders">
                <OrderHistory :is-accepted="false" title="我发起的订单" />
              </div>

              <div
                v-else-if="activeTab === 'accepted-orders'"
                key="accepted-orders"
              >
                <OrderHistory :is-accepted="true" title="我接受的订单" />
              </div>

              <div v-else-if="activeTab === 'posts'" key="posts">
                <PostManagement />
              </div>

              <div v-else-if="activeTab === 'wallet'" key="wallet">
                <WalletManage :balance="userInfo.accountBalance || 0" />
              </div>

              <div v-else-if="activeTab === 'messages'" key="messages">
                <MessageCenter />
              </div>

              <div v-else-if="activeTab === 'address'" key="address">
                <AddressManage @refresh="loadUserInfo" />
              </div>

              <div v-else-if="activeTab === 'settings'" key="settings">
                <PersonalSettings />
              </div>

              <div v-else-if="activeTab === 'security'" key="security">
                <SecuritySettings
                  :phone="userInfo.phone"
                  @update-password="openPasswordDialog"
                />
              </div>

              <div v-else-if="activeTab === 'help'" key="help">
                <HelpFeedback />
              </div>
            </transition>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getUserInfo } from "@/api/user";
import ProfileSidebar from "@/components/common/ProfileSidebar.vue";
import PersonalInfo from "@/components/profile/PersonalInfo.vue";
import OrderHistory from "@/components/profile/OrderHistory.vue";
import PostManagement from "@/components/profile/PostManagement.vue";
import WalletManage from "@/components/profile/WalletManage.vue";
import MessageCenter from "@/components/profile/MessageCenter.vue";
import AddressManage from "@/components/profile/AddressManage.vue";
import PersonalSettings from "@/components/profile/PersonalSettings.vue";
import SecuritySettings from "@/components/profile/SecuritySettings.vue";
import HelpFeedback from "@/components/profile/HelpFeedback.vue";

const activeTab = ref("info");
const unreadMessageCount = ref(0);

interface UserInfo {
  id?: number;
  username?: string;
  nickname?: string;
  phone?: string;
  avatar?: string;
  role?: number;
  campusId?: number;
  campusName?: string;
  createdAt?: string;
  accountBalance?: number;
}

const userInfo = reactive<UserInfo>({});

const sidebarUserInfo = computed(() => ({
  nickname: userInfo.nickname,
  avatar: userInfo.avatar,
  role: userInfo.role,
}));

const handleMenuSelect = (key: string) => {
  activeTab.value = key;
};

const openEditDialog = () => {
  activeTab.value = "info";
};

const openPasswordDialog = () => {
  activeTab.value = "security";
};

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo();
    if (res?.code === 200 && res.data) {
      Object.assign(userInfo, res.data);
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 20px;
  box-sizing: border-box;
}

.profile-layout {
  display: flex;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;

  @media (max-width: 992px) {
    flex-direction: column;
  }
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.main-content {
  padding: 20px;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
