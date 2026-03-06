<template>
  <div class="user-chat-page">
    <ChatBox
      v-model:visible="chatVisible"
      :target-user-id="Number(targetUserId)"
      :target-nickname="targetNickname"
      @close="handleClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import ChatBox from '@/components/common/ChatBox.vue';

const route = useRoute();
const router = useRouter();

const targetUserId = ref<string>('');
const targetNickname = ref<string>('');
const chatVisible = ref(true);

watch(
  () => route.params.targetUserId,
  (newVal) => {
    if (newVal) {
      targetUserId.value = newVal as string;
    }
  },
  { immediate: true }
);

watch(
  () => route.query.nickname,
  (newVal) => {
    if (newVal) {
      targetNickname.value = newVal as string;
    }
  }
);

const handleClose = () => {
  router.back();
};

onMounted(() => {
  if (!targetUserId.value) {
    ElMessage.error('无效的用户');
    router.back();
  }
});
</script>

<style lang="scss" scoped>
.user-chat-page {
  width: 100%;
  height: 100vh;
  background: #f5f7fa;
}
</style>
