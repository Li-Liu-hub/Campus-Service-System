<template>
  <div v-if="visible" class="chat-box-overlay" @click.self="handleClose">
    <div class="chat-box">
      <div class="chat-header">
        <div class="chat-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ currentTargetNickname || "聊天" }}</span>
        </div>
        <el-button type="primary" link @click="handleClose">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <div class="chat-content">
        <div v-if="!currentTargetUserId" class="conversation-list">
          <div class="list-header">会话列表</div>
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conversation-item"
            :class="{ active: conv.targetUserId === currentTargetUserId }"
            @click="selectConversation(conv)"
          >
            <el-avatar :size="40">
              {{ getUserInitial(conv.targetUserId!) }}
            </el-avatar>
            <div class="conv-info">
              <div class="conv-name">
                {{ conv.targetUserNickname || `用户${conv.targetUserId}` }}
              </div>
              <div class="conv-last-msg">
                {{ conv.lastMessage || "暂无消息" }}
              </div>
            </div>
            <div v-if="conv.unreadCount" class="unread-badge">
              {{ conv.unreadCount > 99 ? "99+" : conv.unreadCount }}
            </div>
          </div>
          <el-empty v-if="conversations.length === 0" description="暂无会话" />
        </div>

        <div v-else class="messages-container">
          <div class="messages-header">
            <el-button type="primary" link @click="backToList">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <span class="target-name">{{
              currentTargetNickname || `用户${currentTargetUserId}`
            }}</span>
          </div>
          <div class="messages-list" ref="messagesListRef">
            <div
              v-for="msg in messages"
              :key="msg.id || msg.createTime"
              class="message-item"
              :class="{ 'message-self': isCurrentUser((msg as any).senderId!) }"
            >
              <el-avatar
                v-if="!isCurrentUser((msg as any).senderId!)"
                :size="32"
              >
                {{ getUserInitial((msg as any).senderId!) }}
              </el-avatar>
              <div class="message-content-wrapper">
                <div class="message-bubble">{{ (msg as any).content }}</div>
                <div class="message-time">
                  {{ formatTime((msg as any).createTime) }}
                </div>
              </div>
              <el-avatar
                v-if="isCurrentUser((msg as any).senderId!)"
                :size="32"
              >
                {{ getUserInitial((msg as any).senderId!) }}
              </el-avatar>
            </div>
          </div>
          <div class="message-input-area">
            <el-input
              v-model="newMessage"
              type="textarea"
              :rows="2"
              placeholder="输入消息..."
              @keydown.enter.prevent="handleSendMessage"
            />
            <el-button
              type="primary"
              @click="handleSendMessage"
              :loading="sending"
            >
              发送
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";
import { ChatDotRound, Close, ArrowLeft } from "@element-plus/icons-vue";
import {
  getConversationList,
  getMessages,
  markAsRead,
} from "@/api/conversation";
import type { Conversation, PrivateMessage } from "@/types/entities";
import { webSocketManager, type WebSocketMessage } from "@/utils/websocket";

interface Props {
  visible: boolean;
  targetUserId?: number;
  targetNickname?: string;
}

const props = defineProps<Props>();
const emit = defineEmits(["close", "update:visible"]);

const conversations = ref<Conversation[]>([]);
const messages = ref<(PrivateMessage | WebSocketMessage)[]>([]);
const currentTargetUserId = ref<number | null>(null);
const currentTargetNickname = ref<string>("");
const newMessage = ref("");
const sending = ref(false);
const messagesListRef = ref<HTMLElement | null>(null);
const currentUserId = ref<number | null>(null);
let unsubscribe: (() => void) | null = null;

const isCurrentUser = (userId: number): boolean => {
  return userId === currentUserId.value;
};

const getUserInitial = (userId: number): string => {
  if (!userId) return "U";
  return userId.toString().charAt(0);
};

const formatTime = (timeStr: string | Date | number[] | undefined): string => {
  if (!timeStr) return "-";
  let date: Date;
  if (typeof timeStr === "string") {
    date = new Date(timeStr);
  } else if (timeStr instanceof Date) {
    date = timeStr;
  } else if (Array.isArray(timeStr) && timeStr.length >= 6) {
    const [year = 0, month = 1, day = 1, hour = 0, minute = 0] = timeStr;
    date = new Date(year, month - 1, day, hour, minute);
  } else {
    return "-";
  }
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${hours}:${minutes}`;
};

const loadConversations = async () => {
  try {
    const res = await getConversationList();
    if (res.data) {
      conversations.value = res.data;
    }
  } catch (err) {
    console.error("加载会话列表失败:", err);
  }
};

const loadMessages = async (targetUserId: number) => {
  console.log("开始加载历史消息，targetUserId:", targetUserId);
  try {
    const res = await getMessages(targetUserId);
    console.log("加载消息API响应:", res);
    if (res.data) {
      console.log("历史消息数据:", res.data);
      messages.value = res.data.map((msg: any) => ({
        ...msg,
        messageId: msg.id,
      }));
      console.log("处理后的消息列表:", messages.value);
      await nextTick();
      scrollToBottom();
    }
  } catch (err) {
    console.error("加载消息失败:", err);
  }
};

const selectConversation = (conv: Conversation) => {
  currentTargetUserId.value = conv.targetUserId!;
  currentTargetNickname.value =
    conv.targetUserNickname || `用户${conv.targetUserId}`;
  loadMessages(conv.targetUserId!);
  if (conv.unreadCount && conv.unreadCount > 0) {
    markAsRead(conv.targetUserId!);
    conv.unreadCount = 0;
  }
};

const backToList = () => {
  currentTargetUserId.value = null;
  currentTargetNickname.value = "";
  messages.value = [];
};

const scrollToBottom = () => {
  if (messagesListRef.value) {
    messagesListRef.value.scrollTop = messagesListRef.value.scrollHeight;
  }
};

const handleSendMessage = async () => {
  console.log("handleSendMessage被调用");
  console.log("newMessage:", newMessage.value);
  console.log("currentTargetUserId:", currentTargetUserId.value);
  console.log("currentUserId:", currentUserId.value);

  if (!newMessage.value.trim()) {
    console.log("消息内容为空");
    ElMessage.warning("请输入消息内容");
    return;
  }
  if (!currentTargetUserId.value) {
    console.log("没有选择聊天对象");
    ElMessage.warning("请选择聊天对象");
    return;
  }
  if (!currentUserId.value) {
    console.log("当前用户ID为空");
    ElMessage.warning("请重新登录");
    return;
  }

  if (!webSocketManager.isConnected()) {
    console.log("WebSocket未连接，尝试连接...");
    ElMessage.warning("正在连接聊天服务...");
    webSocketManager.connect();
    return;
  }

  const content = newMessage.value.trim();
  console.log("准备发送消息:", content);

  const tempId = Date.now();
  const localMessage: any = {
    tempId: tempId,
    senderId: currentUserId.value,
    senderNickname: "",
    receiverId: currentTargetUserId.value,
    content: content,
    createTime: new Date(),
  };
  messages.value.push(localMessage);
  console.log("本地消息已添加，当前消息列表:", messages.value);
  await nextTick();
  scrollToBottom();

  const success = webSocketManager.sendMessage(
    currentTargetUserId.value,
    content
  );
  console.log("WebSocket发送结果:", success);
  if (success) {
    newMessage.value = "";
  } else {
    ElMessage.error("发送消息失败");
    const idx = messages.value.findIndex((m: any) => m.tempId === tempId);
    if (idx > -1) {
      messages.value.splice(idx, 1);
    }
  }
};

const handleWebSocketMessage = (msg: WebSocketMessage) => {
  console.log("收到WebSocket消息:", msg);

  if (
    msg.senderId === currentUserId.value ||
    msg.receiverId === currentUserId.value
  ) {
    const isRelatedToCurrent =
      (msg.senderId === currentUserId.value &&
        msg.receiverId === currentTargetUserId.value) ||
      (msg.receiverId === currentUserId.value &&
        msg.senderId === currentTargetUserId.value);

    if (isRelatedToCurrent) {
      let isDuplicate = messages.value.some(
        (m: any) =>
          m.messageId && msg.messageId && m.messageId === msg.messageId
      );

      if (!isDuplicate && msg.senderId === currentUserId.value) {
        const tempIdx = messages.value.findIndex(
          (m: any) =>
            m.tempId && m.content === msg.content && m.senderId === msg.senderId
        );
        if (tempIdx > -1) {
          isDuplicate = true;
          messages.value.splice(tempIdx, 1, msg);
          console.log("替换本地临时消息为服务器返回消息");
        }
      }

      if (!isDuplicate) {
        messages.value.push(msg);
        nextTick(() => scrollToBottom());
      }

      if (
        msg.senderId !== currentUserId.value &&
        currentTargetUserId.value === msg.senderId
      ) {
        markAsRead(msg.senderId);
      }
    }

    loadConversations();
  }
};

const handleClose = () => {
  emit("update:visible", false);
  emit("close");
};

watch(
  () => props.visible,
  (val) => {
    if (val) {
      webSocketManager.connect();
      unsubscribe = webSocketManager.onMessage(handleWebSocketMessage);
      loadConversations();
      if (props.targetUserId) {
        currentTargetUserId.value = props.targetUserId;
        currentTargetNickname.value =
          props.targetNickname || `用户${props.targetUserId}`;
        loadMessages(props.targetUserId);
      }
    } else {
      if (unsubscribe) {
        unsubscribe();
        unsubscribe = null;
      }
    }
  }
);

onMounted(() => {
  console.log("ChatBox组件已挂载");
  const userStr = localStorage.getItem("userInfo");
  console.log("从localStorage获取到的userInfo:", userStr);
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      console.log("解析后的user对象:", user);
      currentUserId.value = user.id;
      console.log("设置currentUserId为:", currentUserId.value);
    } catch (e) {
      console.error("解析用户信息失败:", e);
    }
  } else {
    console.warn("localStorage中没有userInfo");
  }
});

onBeforeUnmount(() => {
  if (unsubscribe) {
    unsubscribe();
  }
  webSocketManager.disconnect();
});
</script>

<style lang="scss" scoped>
.chat-box-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.chat-box {
  width: 500px;
  height: 600px;
  background-color: white;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light, #e4e7ed);
  display: flex;
  justify-content: space-between;
  align-items: center;

  .chat-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary, #303133);
  }
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.conversation-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;

  .list-header {
    padding: 12px 20px;
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary, #303133);
    background-color: var(--bg-tertiary, #f5f7fa);
    border-bottom: 1px solid var(--border-light, #e4e7ed);
  }

  .conversation-item {
    padding: 12px 20px;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: background-color 0.2s ease;
    position: relative;

    &:hover {
      background-color: var(--bg-tertiary, #f5f7fa);
    }

    &.active {
      background-color: var(--el-color-primary-light-9, #ecf5ff);
    }

    .conv-info {
      flex: 1;
      min-width: 0;

      .conv-name {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary, #303133);
        margin-bottom: 4px;
      }

      .conv-last-msg {
        font-size: 12px;
        color: var(--text-tertiary, #909399);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .unread-badge {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
      background-color: var(--el-color-danger, #f56c6c);
      color: white;
      font-size: 12px;
      padding: 2px 6px;
      border-radius: 10px;
      min-width: 18px;
      text-align: center;
    }
  }
}

.messages-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;

  .messages-header {
    padding: 12px 20px;
    border-bottom: 1px solid var(--border-light, #e4e7ed);
    display: flex;
    align-items: center;
    gap: 8px;

    .target-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary, #303133);
    }
  }

  .messages-list {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .message-item {
      display: flex;
      gap: 12px;
      align-items: flex-start;

      &.message-self {
        flex-direction: row-reverse;

        .message-content-wrapper {
          align-items: flex-end;
        }

        .message-bubble {
          background-color: var(--el-color-primary, #409eff);
          color: white;
        }
      }

      .message-content-wrapper {
        max-width: 70%;
        display: flex;
        flex-direction: column;
        gap: 4px;

        .message-bubble {
          padding: 10px 14px;
          background-color: var(--bg-tertiary, #f5f7fa);
          border-radius: 12px;
          font-size: 14px;
          line-height: 1.5;
          word-break: break-word;
        }

        .message-time {
          font-size: 12px;
          color: var(--text-tertiary, #909399);
        }
      }
    }
  }

  .message-input-area {
    padding: 16px;
    border-top: 1px solid var(--border-light, #e4e7ed);
    display: flex;
    gap: 12px;
    align-items: flex-end;

    .el-textarea {
      flex: 1;
    }
  }
}
</style>
