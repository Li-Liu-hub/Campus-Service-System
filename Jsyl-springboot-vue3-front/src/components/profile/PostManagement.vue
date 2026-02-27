<template>
  <div class="post-management">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">我的帖子</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子..."
            prefix-icon="Search"
            clearable
            style="width: 200px"
            @input="handleSearch"
          />
        </div>
      </template>

      <div class="post-list">
        <div v-for="post in filteredPosts" :key="post.id" class="post-item">
          <div class="post-content">
            <div class="post-header">
              <span class="post-title">{{ post.title }}</span>
              <el-tag v-if="post.isTop" type="warning" size="small"
                >置顶</el-tag
              >
            </div>

            <div class="post-meta">
              <span class="post-time">{{ formatTime(post.createTime) }}</span>
              <span class="post-views">
                <el-icon><View /></el-icon>
                {{ post.viewCount || 0 }}
              </span>
              <span class="post-likes">
                <el-icon><Star /></el-icon>
                {{ post.likeCount || 0 }}
              </span>
              <span class="post-comments">
                <el-icon><ChatDotRound /></el-icon>
                {{ post.commentCount || 0 }}
              </span>
            </div>
          </div>

          <div class="post-actions">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleEdit(post)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              @click="handleTop(post)"
            >
              <el-icon><Top /></el-icon>
              {{ post.isTop ? "取消置顶" : "置顶" }}
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(post)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>

        <el-empty v-if="filteredPosts.length === 0" description="暂无帖子" />
      </div>

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  View,
  Star,
  ChatDotRound,
  Edit,
  Top,
  Delete,
} from "@element-plus/icons-vue";
import { getMyPosts, deletePost, type Post } from "@/api/post";

const posts = ref<Post[]>([]);
const loading = ref(false);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const filteredPosts = computed(() => {
  if (!searchKeyword.value) return posts.value;

  const keyword = searchKeyword.value.toLowerCase();
  return posts.value.filter(
    (post) =>
      post.title?.toLowerCase().includes(keyword) ||
      post.content?.toLowerCase().includes(keyword)
  );
});

const formatTime = (time: string | number[] | undefined): string => {
  if (!time) return "-";
  if (typeof time === "string") return time;
  if (Array.isArray(time)) {
    const [year, month, day, hour, minute] = time;
    return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
      2,
      "0"
    )} ${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
  }
  return "-";
};

const loadPosts = async () => {
  loading.value = true;
  try {
    const res = await getMyPosts();
    if (res?.code === 200 && res.data) {
      posts.value = res.data;
      total.value = res.data.length;
    }
  } catch (error) {
    console.error("加载帖子失败:", error);
    ElMessage.error("加载帖子失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
};

const handleEdit = (post: Post) => {
  ElMessage.info("编辑功能开发中");
};

const handleTop = async (post: Post) => {
  ElMessage.info("置顶功能开发中");
};

const handleDelete = async (post: Post) => {
  try {
    await ElMessageBox.confirm("确定要删除该帖子吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await deletePost(post.id!);
    if (res?.code === 200) {
      ElMessage.success("删除成功");
      loadPosts();
    } else {
      ElMessage.error(res?.msg || "删除失败");
    }
  } catch (err) {
    if (err !== "cancel") {
      console.error("删除失败:", err);
      ElMessage.error("删除失败");
    }
  }
};

onMounted(() => {
  loadPosts();
});
</script>

<style lang="scss" scoped>
.post-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .post-list {
    margin-top: 20px;
  }

  .post-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 12px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
    }
  }

  .post-content {
    flex: 1;
    min-width: 0;

    .post-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .post-title {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }

    .post-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 12px;
      color: #909399;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .post-actions {
    display: flex;
    gap: 8px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
