<template>
  <div class="post-detail-container">
    <div v-loading="loading" class="post-detail-content">
      <ChatBox
        v-model:visible="chatBoxVisible"
        :target-user-id="chatTargetUserId"
        :target-nickname="chatTargetNickname"
        @close="closeChatBox"
      />
      <el-button type="primary" link @click="$emit('back')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>

      <el-card class="post-card" shadow="hover">
        <template #header>
          <div class="post-header">
            <h1 class="post-title">{{ post?.title }}</h1>
            <div class="post-meta">
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span
                  >发布者：{{
                    post?.author?.nickname ||
                    post?.author ||
                    "用户" + (post?.userId || "")
                  }}</span
                >
              </div>
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>发布时间：{{ formatTime(post?.createTime) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><View /></el-icon>
                <span>浏览量：{{ post?.viewCount }}</span>
              </div>
              <div class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>评论数：{{ post?.replyCount }}</span>
              </div>
            </div>
          </div>
        </template>
        <div class="post-content">
          {{ post?.content }}
        </div>
      </el-card>

      <el-card class="comments-card" shadow="hover">
        <template #header>
          <div class="comments-header">
            <span class="comments-title"
              >评论 ({{ topLevelComments.length }})</span
            >
          </div>
        </template>

        <div class="comment-form">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
          <div class="comment-form-actions">
            <el-button
              type="primary"
              @click="handlePublishComment"
              :loading="publishing"
            >
              发表评论
            </el-button>
          </div>
        </div>

        <div class="comments-list">
          <div
            v-for="comment in topLevelComments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-header">
              <div class="comment-author">
                <el-avatar
                  :size="32"
                  class="clickable-avatar"
                  @click="
                    startPrivateChat(comment.userId!, comment.userNickname)
                  "
                  >{{ getUserInitial(comment.userId!) }}</el-avatar
                >
                <div class="author-info">
                  <span
                    class="author-name clickable-name"
                    @click="
                      startPrivateChat(comment.userId!, comment.userNickname)
                    "
                    >{{
                      comment.userNickname || getUserNickname(comment.userId!)
                    }}</span
                  >
                  <span class="comment-time">{{
                    formatTime(comment.createTime)
                  }}</span>
                </div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button
                type="primary"
                link
                @click="showReplyInput(comment.id!)"
              >
                回复
              </el-button>
            </div>

            <div v-if="replyingTo === comment.id" class="reply-form">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                placeholder="回复评论..."
              />
              <div class="reply-form-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button
                  size="small"
                  type="primary"
                  @click="handlePublishReply(comment.id!)"
                  :loading="publishingReply"
                >
                  回复
                </el-button>
              </div>
            </div>

            <div
              v-if="getReplies(comment.id!).length > 0"
              class="replies-container"
            >
              <div
                v-for="reply in getReplies(comment.id!)"
                :key="reply.id"
                class="reply-item"
              >
                <div class="reply-header">
                  <div class="reply-author">
                    <el-avatar
                      :size="24"
                      class="clickable-avatar"
                      @click="
                        startPrivateChat(reply.userId!, reply.userNickname)
                      "
                      >{{ getUserInitial(reply.userId!) }}</el-avatar
                    >
                    <div class="author-info">
                      <span
                        class="author-name clickable-name"
                        @click="
                          startPrivateChat(reply.userId!, reply.userNickname)
                        "
                        >{{
                          reply.userNickname || getUserNickname(reply.userId!)
                        }}</span
                      >
                      <span class="comment-time">{{
                        formatTime(reply.createTime)
                      }}</span>
                    </div>
                  </div>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="comment-actions">
                  <el-button
                    type="primary"
                    link
                    size="small"
                    @click="showReplyInput(comment.id!, reply.userId!)"
                  >
                    回复
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <el-empty
            v-if="topLevelComments.length === 0"
            description="暂无评论"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  ArrowLeft,
  User,
  Calendar,
  View,
  ChatDotRound,
} from "@element-plus/icons-vue";
import { getPostDetail } from "@/api/post";
import { getCommentList, publishComment } from "@/api/comment";
import type { PostDetailVO, CommentInfo } from "@/types/entities";
import ChatBox from "./ChatBox.vue";

interface Props {
  postId: number;
}

const props = defineProps<Props>();
const emit = defineEmits(["back"]);

const loading = ref(false);
const post = ref<PostDetailVO | null>(null);
const comments = ref<CommentInfo[]>([]);
const newComment = ref("");
const publishing = ref(false);
const replyingTo = ref<number | null>(null);
const replyContent = ref("");
const publishingReply = ref(false);
const replyToUserId = ref<number | null>(null);
const chatBoxVisible = ref(false);
const chatTargetUserId = ref<number | null>(null);
const chatTargetNickname = ref<string>("");

const formatTime = (timeArr: number[] | string | undefined): string => {
  if (!timeArr) return "-";
  if (typeof timeArr === "string") {
    return timeArr || "-";
  }
  if (Array.isArray(timeArr) && timeArr.length >= 6) {
    const [year = 0, month = 1, day = 1, hour = 0, minute = 0] = timeArr;
    const padZero = (num: number): string => {
      const n = Number(num);
      return isNaN(n) ? "00" : n.toString().padStart(2, "0");
    };
    return `${year}-${padZero(month)}-${padZero(day)} ${padZero(
      hour
    )}:${padZero(minute)}`;
  }
  return "-";
};

const getUserInitial = (userId: number | undefined): string => {
  if (!userId) return "U";
  return userId.toString().charAt(0);
};

const getUserNickname = (userId: number | undefined): string => {
  if (!userId) return "用户";
  return "用户" + userId;
};

const startPrivateChat = (userId: number, nickname?: string) => {
  console.log("开始私聊:", userId, nickname);
  chatTargetUserId.value = userId;
  chatTargetNickname.value = nickname || `用户${userId}`;
  chatBoxVisible.value = true;
};

const closeChatBox = () => {
  chatBoxVisible.value = false;
  chatTargetUserId.value = null;
  chatTargetNickname.value = "";
};

const topLevelComments = computed(() => {
  return comments.value.filter((c) => !c.parentId || c.parentId === 0);
});

const getReplies = (parentId: number): CommentInfo[] => {
  return comments.value.filter((c) => c.parentId === parentId);
};

const loadData = async () => {
  loading.value = true;
  console.log("帖子详情加载，postId:", props.postId);
  try {
    const [postRes, commentsRes] = await Promise.all([
      getPostDetail(props.postId),
      getCommentList({ postId: props.postId }),
    ]);
    console.log("帖子详情响应:", postRes);
    console.log("评论响应:", commentsRes);
    if (postRes.data) {
      post.value = postRes.data;
    }
    if (commentsRes.data) {
      comments.value = commentsRes.data as unknown as CommentInfo[];
    }
  } catch (err) {
    console.error("加载数据失败:", err);
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }
};

const handlePublishComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }
  publishing.value = true;
  try {
    await publishComment({
      postId: props.postId,
      parentId: 0,
      content: newComment.value,
    });
    ElMessage.success("评论发布成功");
    newComment.value = "";
    await loadData();
  } catch (err) {
    console.error("发布评论失败:", err);
  } finally {
    publishing.value = false;
  }
};

const showReplyInput = (commentId: number, toUserId?: number) => {
  replyingTo.value = commentId;
  replyToUserId.value = toUserId || null;
  replyContent.value = "";
};

const cancelReply = () => {
  replyingTo.value = null;
  replyToUserId.value = null;
  replyContent.value = "";
};

const handlePublishReply = async (parentId: number) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }
  publishingReply.value = true;
  try {
    let content = replyContent.value;
    if (replyToUserId.value) {
      content = `@${getUserNickname(replyToUserId.value)} ${content}`;
    }
    await publishComment({
      postId: props.postId,
      parentId: parentId,
      content: content,
    });
    ElMessage.success("回复成功");
    cancelReply();
    await loadData();
  } catch (err) {
    console.error("回复失败:", err);
  } finally {
    publishingReply.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
.post-detail-container {
  padding: var(--spacing-xl);
  background-color: var(--bg-secondary);
  min-height: 100%;
}

.clickable-avatar {
  cursor: pointer;
  transition: transform 0.2s ease;
  &:hover {
    transform: scale(1.1);
  }
}

.clickable-name {
  cursor: pointer;
  transition: color 0.2s ease;
  &:hover {
    color: var(--el-color-primary);
  }
}

.post-detail-content {
  max-width: 900px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: var(--spacing-lg);
  padding: 0;
}

.post-card,
.comments-card {
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
}

.post-header {
  .post-title {
    margin: 0 0 var(--spacing-md);
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--text-primary);
  }

  .post-meta {
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-lg);
    color: var(--text-secondary);
    font-size: var(--font-size-sm);

    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
    }
  }
}

.post-content {
  line-height: 1.8;
  color: var(--text-primary);
  font-size: var(--font-size-base);
  white-space: pre-wrap;
}

.comments-header {
  .comments-title {
    font-size: var(--font-size-lg);
    font-weight: 600;
    color: var(--text-primary);
  }
}

.comment-form {
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--border-light);

  .comment-form-actions {
    margin-top: var(--spacing-md);
    display: flex;
    justify-content: flex-end;
  }
}

.comments-list {
  .comment-item {
    padding: var(--spacing-lg) 0;
    border-bottom: 1px solid var(--border-light);

    &:last-child {
      border-bottom: none;
    }

    .comment-header {
      margin-bottom: var(--spacing-sm);

      .comment-author {
        display: flex;
        align-items: center;
        gap: var(--spacing-md);

        .author-info {
          display: flex;
          flex-direction: column;
          gap: 2px;

          .author-name {
            font-weight: 500;
            color: var(--text-primary);
          }

          .comment-time {
            font-size: var(--font-size-sm);
            color: var(--text-tertiary);
          }
        }
      }
    }

    .comment-content {
      color: var(--text-primary);
      line-height: 1.6;
      margin-left: 48px;
      margin-bottom: var(--spacing-sm);
    }

    .comment-actions {
      margin-left: 48px;
    }

    .reply-form {
      margin-left: 48px;
      margin-top: var(--spacing-md);
      padding: var(--spacing-md);
      background-color: var(--bg-tertiary);
      border-radius: var(--border-radius);

      .reply-form-actions {
        margin-top: var(--spacing-sm);
        display: flex;
        justify-content: flex-end;
        gap: var(--spacing-sm);
      }
    }

    .replies-container {
      margin-left: 48px;
      margin-top: var(--spacing-md);

      .reply-item {
        padding: var(--spacing-md);
        background-color: var(--bg-tertiary);
        border-radius: var(--border-radius);
        margin-bottom: var(--spacing-sm);

        &:last-child {
          margin-bottom: 0;
        }

        .reply-header {
          margin-bottom: var(--spacing-xs);

          .reply-author {
            display: flex;
            align-items: center;
            gap: var(--spacing-sm);

            .author-info {
              display: flex;
              flex-direction: column;
              gap: 2px;

              .author-name {
                font-weight: 500;
                color: var(--text-primary);
                font-size: var(--font-size-sm);
              }

              .comment-time {
                font-size: 12px;
                color: var(--text-tertiary);
              }
            }
          }
        }

        .reply-content {
          color: var(--text-primary);
          line-height: 1.6;
          margin-left: 32px;
          font-size: var(--font-size-sm);
          margin-bottom: var(--spacing-xs);
        }

        .comment-actions {
          margin-left: 32px;
        }
      }
    }
  }
}
</style>
