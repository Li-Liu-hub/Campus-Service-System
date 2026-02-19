<template>
  <div class="user-posts">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">交流广场</h2>
        <p class="page-subtitle">分享生活，发现精彩</p>
      </div>
      <div class="header-actions">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子标题"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handlePublish">
          <el-icon><Plus /></el-icon>
          发布帖子
        </el-button>
        <el-button :icon="Refresh" :loading="refreshing" @click="handleRefresh">
          刷新
        </el-button>
      </div>
    </div>

    <div class="posts-container">
      <div
        v-loading="loading"
        element-loading-text="加载中..."
        class="posts-list"
      >
        <div v-if="posts.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无帖子" />
        </div>

        <div v-else class="posts-grid">
          <div
            v-for="post in posts"
            :key="post.id"
            class="post-card"
            @click="handlePostClick(post)"
          >
            <div class="post-header">
              <el-avatar :size="40" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{
                  post.author || "用户" + (post.userId || "")
                }}</span>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>

            <div class="post-content">
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-text">{{ post.content }}</p>
            </div>

            <div class="post-footer">
              <div class="post-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ post.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ post.commentCount || post.replyCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 24]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="publishDialogVisible"
      title="发布帖子"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="publishFormRef"
        :model="publishForm"
        :rules="publishRules"
        label-width="80px"
      >
        <el-form-item label="帖子标题" prop="title">
          <el-input
            v-model="publishForm.title"
            placeholder="请输入帖子标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="帖子内容" prop="content">
          <el-input
            v-model="publishForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入帖子内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitPublish"
            :loading="publishLoading"
          >
            发布
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import {
  Refresh,
  User,
  View,
  ChatDotRound,
  Plus,
  Search,
} from "@element-plus/icons-vue";
import {
  getPostPage,
  publishPost,
  type Post,
  type PostPageQueryParams,
  type PostPublishParams,
} from "@/api/post";

const router = useRouter();

const loading = ref(false);
const refreshing = ref(false);
const posts = ref<Post[]>([]);
const page = ref(1);
const pageSize = ref(6);
const total = ref(0);
const searchKeyword = ref("");

const publishDialogVisible = ref(false);
const publishFormRef = ref<FormInstance>();
const publishLoading = ref(false);
const publishForm = ref<PostPublishParams>({
  title: "",
  content: "",
});

const publishRules: FormRules = {
  title: [
    { required: true, message: "请输入帖子标题", trigger: "blur" },
    {
      min: 2,
      max: 100,
      message: "标题长度在 2 到 100 个字符",
      trigger: "blur",
    },
  ],
  content: [
    { required: true, message: "请输入帖子内容", trigger: "blur" },
    {
      min: 5,
      max: 2000,
      message: "内容长度在 5 到 2000 个字符",
      trigger: "blur",
    },
  ],
};

const formatTime = (timeArr: number[] | string | undefined): string => {
  if (!timeArr) return "-";
  if (typeof timeArr === "string") return timeArr;
  if (Array.isArray(timeArr) && timeArr.length >= 5) {
    const [, month, day, hour, minute] = timeArr;
    const h = hour ?? 0;
    const m = minute ?? 0;
    return `${month}-${day} ${h.toString().padStart(2, "0")}:${m
      .toString()
      .padStart(2, "0")}`;
  }
  return "-";
};

const loadPosts = async () => {
  loading.value = true;
  try {
    const params: PostPageQueryParams = {
      page: page.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
    };
    const res = await getPostPage(params);
    console.log("帖子列表响应:", res);

    if (res?.code === 200 && res?.data) {
      const data = res.data.records || res.data;
      console.log("解析后的帖子数据:", data);
      posts.value = data;
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("加载帖子失败：", error);
    ElMessage.error("加载帖子失败");
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

const handleRefresh = () => {
  page.value = 1;
  loadPosts();
};

const handleSearch = () => {
  page.value = 1;
  loadPosts();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  page.value = 1;
  loadPosts();
};

const handleCurrentChange = (currentPage: number) => {
  page.value = currentPage;
  loadPosts();
};

const handlePublish = () => {
  publishForm.value = {
    title: "",
    content: "",
  };
  publishDialogVisible.value = true;
};

const handleSubmitPublish = async () => {
  if (!publishFormRef.value) return;

  await publishFormRef.value.validate(async (valid) => {
    if (valid) {
      publishLoading.value = true;
      try {
        const res = await publishPost(publishForm.value);
        if (res?.code === 200) {
          ElMessage.success("发布成功！");
          publishDialogVisible.value = false;
          handleRefresh();
        } else {
          ElMessage.error(res?.msg || "发布失败");
        }
      } catch (error) {
        console.error("发布失败：", error);
        ElMessage.error("发布失败，请稍后重试");
      } finally {
        publishLoading.value = false;
      }
    }
  });
};

const handlePostClick = (post: Post) => {
  console.log("点击的帖子对象:", post);
  const postId = post.id;
  console.log("帖子ID:", postId);
  if (postId) {
    router.push(`/user/posts/${postId}`);
  } else {
    ElMessage.error("帖子ID不存在");
  }
};

onMounted(() => {
  loadPosts();
});
</script>

<style lang="scss" scoped>
.user-posts {
  width: 100%;
  min-height: 100%;
  padding: 24px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;

  .header-left {
    .page-title {
      font-size: 24px;
      font-weight: 700;
      color: #303133;
      margin: 0 0 4px;
    }

    .page-subtitle {
      font-size: 14px;
      color: #909399;
      margin: 0;
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
    align-items: center;

    .search-box {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }
}

.posts-container {
  max-width: 1200px;
  margin: 0 auto;
}

.posts-list {
  min-height: 300px;
}

.empty-state {
  padding: 80px 0;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.post-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .user-name {
    font-weight: 600;
    color: #303133;
  }

  .post-time {
    font-size: 12px;
    color: #909399;
  }
}

.post-content {
  margin-bottom: 16px;

  .post-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .post-text {
    font-size: 14px;
    color: #606266;
    margin: 0;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.post-footer {
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.post-stats {
  display: flex;
  gap: 24px;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #909399;
    font-size: 13px;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 16px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 768px) {
  .user-posts {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .header-actions {
      width: 100%;
      flex-direction: column;
    }
  }

  .posts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
