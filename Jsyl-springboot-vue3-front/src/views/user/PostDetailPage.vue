<template>
  <div class="post-detail-page">
    <PostDetail :post-id="postId" @back="handleBack" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import PostDetail from "@/components/common/PostDetail.vue";

const route = useRoute();
const router = useRouter();
const postId = ref<number>(0);

const handleBack = () => {
  router.push("/user/posts");
};

const updatePostId = () => {
  console.log("PostDetailPage - 路由参数:", route.params);
  const id = route.params.id;
  console.log("PostDetailPage - 从路由获取的id:", id);
  if (id) {
    postId.value = Number(id);
    console.log("PostDetailPage - 转换后的postId:", postId.value);
  }
};

onMounted(() => {
  updatePostId();
});

watch(
  () => route.params,
  () => {
    updatePostId();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.post-detail-page {
  width: 100%;
  min-height: 100%;
  background-color: #f5f7fa;
}
</style>
