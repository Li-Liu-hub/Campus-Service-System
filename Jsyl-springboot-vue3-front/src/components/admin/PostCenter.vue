<template>
  <div class="post-center">
    <div class="page-header">
      <h2 class="page-title">帖子管理</h2>
      <p class="page-subtitle">管理社区帖子，维护良好的交流环境</p>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card primary">
          <div class="stat-icon">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">156</div>
            <div class="stat-label">总帖子数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card success">
          <div class="stat-icon">
            <el-icon :size="24"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">892</div>
            <div class="stat-label">总评论数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card warning">
          <div class="stat-icon">
            <el-icon :size="24"><View /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">12,580</div>
            <div class="stat-label">总浏览量</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card info">
          <div class="stat-icon">
            <el-icon :size="24"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">28</div>
            <div class="stat-label">今日新增</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="关键词搜索">
          <el-input
            v-model="filterForm.keyword"
            placeholder="请输入帖子标题关键词"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="list-header">
          <span class="header-title">帖子列表</span>
          <el-button type="primary" size="small" @click="handleAddPost">
            <el-icon><Plus /></el-icon>
            发布帖子
          </el-button>
        </div>
      </template>

      <el-table :data="postList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="帖子标题" min-width="250" show-overflow-tooltip>
          <template #default="scope">
            <span class="post-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="发布者ID" width="100" align="center" />
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center">
          <template #default="scope">
            <span class="view-count">
              <el-icon><View /></el-icon>
              {{ scope.row.viewCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="replyCount" label="评论数" width="100" align="center">
          <template #default="scope">
            <span class="reply-count">
              <el-icon><ChatDotRound /></el-icon>
              {{ scope.row.replyCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleViewDetail(scope.row)">
              查看
            </el-button>
            <el-button type="primary" link size="small" @click="handleEditPost(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDeletePost(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isAdd ? '发布帖子' : (isEdit ? '编辑帖子' : '帖子详情')"
      width="700px"
      class="post-detail-dialog"
    >
      <div class="post-detail">
        <el-form :model="postForm" label-width="80px">
          <el-form-item label="帖子标题">
            <el-input v-model="postForm.title" :disabled="!isEdit" />
          </el-form-item>
          <el-form-item label="帖子内容">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="8"
              :disabled="!isEdit"
            />
          </el-form-item>
        </el-form>
        <div class="post-meta" v-if="!isEdit && !isAdd && currentPost">
          <div class="meta-item">
            <el-icon><User /></el-icon>
            <span>发布者ID: {{ currentPost.userId }}</span>
          </div>
          <div class="meta-item">
            <el-icon><View /></el-icon>
            <span>浏览量: {{ currentPost.viewCount }}</span>
          </div>
          <div class="meta-item">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论数: {{ currentPost.replyCount }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>发布时间: {{ formatTime(currentPost.createTime) }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" v-if="isEdit" @click="handleSavePost">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Document,
  ChatDotRound,
  View,
  Calendar,
  Search,
  RefreshLeft,
  Plus,
  User
} from '@element-plus/icons-vue';
import type { Post } from '@/types/entities';
import { getPostPage, publishPost, updatePost, deletePost } from '@/api/post';

const filterForm = ref({
  keyword: ''
});

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const loading = ref(false);
const postList = ref<Post[]>([]);
const currentPost = ref<Post | null>(null);
const dialogVisible = ref(false);
const isEdit = ref(false);
const isAdd = ref(false);

const postForm = ref({
  title: '',
  content: ''
});

const formatTime = (timeArr: number[] | string | undefined): string => {
  if (!timeArr) return '-';
  if (typeof timeArr === 'string') {
    return timeArr || '-';
  }
  if (Array.isArray(timeArr) && timeArr.length >= 6) {
    const [year = 0, month = 1, day = 1, hour = 0, minute = 0] = timeArr;
    const padZero = (num: number): string => {
      const n = Number(num);
      return isNaN(n) ? '00' : n.toString().padStart(2, '0');
    };
    return `${year}-${padZero(month)}-${padZero(day)} ${padZero(hour)}:${padZero(minute)}`;
  }
  return '-';
};

const getPostList = async () => {
  loading.value = true;
  try {
    const res = await getPostPage({
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      keyword: filterForm.value.keyword || undefined
    });
    if (res.data) {
      postList.value = res.data.records || [];
      pagination.value.total = res.data.total || 0;
    }
  } catch (err) {
    console.error('获取帖子列表失败:', err);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.value.currentPage = 1;
  getPostList();
};

const handleReset = () => {
  filterForm.value = { keyword: '' };
  pagination.value.currentPage = 1;
  getPostList();
};

const handleSizeChange = (val: number) => {
  pagination.value.pageSize = val;
  getPostList();
};

const handleCurrentChange = (val: number) => {
  pagination.value.currentPage = val;
  getPostList();
};

const handleViewDetail = (row: Post) => {
  currentPost.value = row;
  postForm.value = {
    title: row.title,
    content: row.content
  };
  isEdit.value = false;
  isAdd.value = false;
  dialogVisible.value = true;
};

const handleEditPost = (row: Post) => {
  currentPost.value = row;
  postForm.value = {
    title: row.title,
    content: row.content
  };
  isEdit.value = true;
  isAdd.value = false;
  dialogVisible.value = true;
};

const handleSavePost = async () => {
  try {
    if (isAdd.value) {
      await publishPost(postForm.value);
      ElMessage.success('发布成功');
    } else if (isEdit.value && currentPost.value?.id) {
      await updatePost(currentPost.value.id, postForm.value);
      ElMessage.success('更新成功');
    }
    dialogVisible.value = false;
    getPostList();
  } catch (err) {
    console.error('保存帖子失败:', err);
  }
};

const handleDeletePost = async (row: Post) => {
  if (!row.id) return;
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    await deletePost(row.id);
    ElMessage.success('删除成功');
    getPostList();
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除帖子失败:', err);
    }
  }
};

const handleAddPost = () => {
  currentPost.value = null;
  postForm.value = {
    title: '',
    content: ''
  };
  isEdit.value = true;
  isAdd.value = true;
  dialogVisible.value = true;
};

onMounted(() => {
  getPostList();
});
</script>

<style lang="scss" scoped>
.post-center {
  padding: var(--spacing-xl);
  background-color: var(--bg-secondary);
  min-height: 100%;
}

.page-header {
  margin-bottom: var(--spacing-xl);
  
  .page-title {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 var(--spacing-xs);
  }
  
  .page-subtitle {
    font-size: var(--font-size-sm);
    color: var(--text-tertiary);
    margin: 0;
  }
}

.stats-row {
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  margin-bottom: var(--spacing-lg);
  transition: all var(--transition-normal);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
  }
  
  &.primary {
    border-left: 4px solid var(--primary-color);
    .stat-icon {
      color: var(--primary-color);
      background: rgba(64, 158, 255, 0.1);
    }
  }
  
  &.success {
    border-left: 4px solid var(--success-color);
    .stat-icon {
      color: var(--success-color);
      background: rgba(103, 194, 58, 0.1);
    }
  }
  
  &.warning {
    border-left: 4px solid var(--warning-color);
    .stat-icon {
      color: var(--warning-color);
      background: rgba(230, 162, 60, 0.1);
    }
  }
  
  &.info {
    border-left: 4px solid var(--info-color);
    .stat-icon {
      color: var(--info-color);
      background: rgba(144, 147, 153, 0.1);
    }
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
  
  .stat-value {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.2;
  }
  
  .stat-label {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
    margin-top: var(--spacing-xs);
  }
}

.filter-card {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
}

.list-card {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-light);
  background: var(--bg-primary);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-title {
    font-size: var(--font-size-base);
    font-weight: 600;
    color: var(--text-primary);
  }
}

.post-title {
  font-weight: 500;
  color: var(--text-primary);
}

.view-count,
.reply-count {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--text-secondary);
}

.pagination-wrapper {
  padding: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

.post-detail {
  .post-meta {
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-lg);
    margin-top: var(--spacing-lg);
    padding-top: var(--spacing-lg);
    border-top: 1px solid var(--border-light);
  }
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
  }
}

@media (max-width: 1200px) {
  .post-center {
    padding: var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .post-center {
    padding: var(--spacing-md);
  }
  
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>
