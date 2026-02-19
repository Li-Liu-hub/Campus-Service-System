/**
 * 数据库表结构对应的 TypeScript 类型定义
 */

// ==================== 用户相关 ====================

export interface UserInfo {
  id?: number;
  account?: string;
  nickname?: string;
  username?: string;
  password?: string;
  phone?: string;
  permission?: number;
  address?: string;
  deleted_at?: string;
}

export interface UserStats {
  user_id?: number;
  credit_score?: number;
  order_count?: number;
  avg_rating?: number;
  update_time?: string;
}

export interface UserAddress {
  id?: number;
  userId?: number;
  user_id?: number;
  contactName?: string;
  contact_name?: string;
  contactPhone?: string;
  contact_phone?: string;
  addressDetail?: string;
  address_detail?: string;
  isDefault?: number;
  is_default?: number;
  createTime?: string;
  create_time?: string;
}

// ==================== 帖子相关 ====================

export interface PostType {
  id?: number;
  type_name?: string;
}

export interface Post {
  id?: number;
  userId?: number;
  user_id?: number;
  title?: string;
  content?: string;
  viewCount?: number;
  view_count?: number;
  replyCount?: number;
  reply_count?: number;
  createTime?: string;
  create_time?: string;
  updateTime?: string;
  update_time?: string;
  typeId?: number;
  type_id?: number;
  deletedAt?: string;
  deleted_at?: string;
  campusId?: number;
  campus_id?: number;
  author?: string;
  likeCount?: number;
  commentCount?: number;
}

export interface PostInfo extends Post {}

export interface PostDetailVO extends Post {
  author?: any;
}

export interface PostImage {
  id?: number;
  postId?: number;
  post_id?: number;
  imageUrl?: string;
  image_url?: string;
  sortOrder?: number;
  sort_order?: number;
  createTime?: string;
  create_time?: string;
}

// ==================== 订单相关 ====================

export interface OrderType {
  id?: number;
  typeName?: string;
  type_name?: string;
}

export interface CampusInfo {
  id?: number;
  campusName?: string;
  campus_name?: string;
}

export interface OrderInfo {
  id?: number;
  orderNo?: string;
  order_no?: string;
  userId?: number;
  user_id?: number;
  acceptorId?: number;
  acceptor_id?: number;
  serviceAddress?: string;
  service_address?: string;
  requirement?: string;
  contactPhone?: string;
  contact_phone?: string;
  orderAmount?: number;
  order_amount?: number;
  orderStatus?: number;
  order_status?: number;
  typeId?: number;
  type_id?: number;
  createTime?: string;
  create_time?: string;
  requireTime?: string;
  require_time?: string;
  completeTime?: string;
  complete_time?: string;
  deletedAt?: string;
  deleted_at?: string;
  campusId?: number;
  campus_id?: number;
  orderStatusName?: string;
  typeName?: string;
  publisher?: any;
  acceptor?: any;
}

export interface Order extends OrderInfo {}

export interface OrderEvaluation {
  id?: number;
  orderId?: number;
  order_id?: number;
  userId?: number;
  user_id?: number;
  targetUserId?: number;
  target_user_id?: number;
  rating?: number;
  content?: string;
  createTime?: string;
  create_time?: string;
}

// ==================== 评论和通知 ====================

export interface Comment {
  id?: number;
  postId?: number;
  post_id?: number;
  parentId?: number;
  parent_id?: number;
  userId?: number;
  user_id?: number;
  content?: string;
  createTime?: string;
  create_time?: string;
  author?: any;
  userNickname?: string;
}

export interface CommentInfo extends Comment {}

export interface CommentVO extends Comment {}

export interface Notification {
  id?: number;
  userId?: number;
  user_id?: number;
  type?: number;
  title?: string;
  content?: string;
  relatedId?: number;
  related_id?: number;
  isRead?: boolean;
  is_read?: boolean;
  createTime?: string;
  create_time?: string;
}

// ==================== 枚举常量 ====================

export const ORDER_STATUS: Record<number, string> = {
  0: '未接单',
  1: '已接单',
  2: '完成订单',
};

export const NOTIFICATION_TYPE: Record<number, string> = {
  1: '系统通知',
  2: '评论通知',
  3: '订单通知',
};

export const POST_TYPE: Record<number, string> = {
  1: '求助',
  2: '闲聊',
  3: '交易',
};

// ==================== 聊天相关 ====================

export interface Conversation {
  id?: number;
  userId?: number;
  user_id?: number;
  targetUserId?: number;
  target_user_id?: number;
  lastMessage?: string;
  last_message?: string;
  unreadCount?: number;
  unread_count?: number;
  createTime?: string;
  create_time?: string;
  updateTime?: string;
  update_time?: string;
  targetUserNickname?: string;
}

export interface PrivateMessage {
  id?: number;
  conversationId?: number;
  conversation_id?: number;
  senderId?: number;
  sender_id?: number;
  receiverId?: number;
  receiver_id?: number;
  content?: string;
  msgType?: number;
  msg_type?: number;
  isRead?: number;
  is_read?: number;
  createTime?: string;
  create_time?: string;
}
