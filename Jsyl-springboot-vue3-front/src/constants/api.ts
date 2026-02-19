/**
 * API路径常量模块
 * 
 * @module constants/api
 */

// ==================== API基础路径 ====================

/** API基础路径 */
export const API_BASE_URL = '/jsyl';

/** 公共API路径 */
export const API_COMMON_PATH = `${API_BASE_URL}/common`;

/** 首页功能API路径 */
export const API_HOME_PATH = `${API_BASE_URL}/home`;

// ==================== 认证相关API ====================

/** 登录API路径 */
export const API_AUTH_LOGIN = `${API_COMMON_PATH}/login`;

/** 注册API路径 */
export const API_AUTH_REGISTER = `${API_COMMON_PATH}/register`;

/** 登出API路径 */
export const API_AUTH_LOGOUT = `${API_COMMON_PATH}/logout`;

// ==================== 用户相关API ====================

/** 获取用户信息API路径 */
export const API_USER_INFO = `${API_COMMON_PATH}/userInfo`;

/** 更新用户信息API路径 */
export const API_USER_UPDATE = `${API_COMMON_PATH}/updateUserInfo`;

/** 修改密码API路径 */
export const API_USER_CHANGE_PASSWORD = `${API_COMMON_PATH}/changePassword`;

/** 绑定手机API路径 */
export const API_USER_BIND_PHONE = `${API_COMMON_PATH}/bindPhone`;

/** 绑定邮箱API路径 */
export const API_USER_BIND_EMAIL = `${API_COMMON_PATH}/bindEmail`;

// ==================== 用户地址相关API ====================

/** 用户地址列表API路径 */
export const API_USER_ADDRESS_LIST = `${API_HOME_PATH}/userAddress/list`;

/** 添加用户地址API路径 */
export const API_USER_ADDRESS_ADD = `${API_HOME_PATH}/userAddress/add`;

/** 更新用户地址API路径 */
export const API_USER_ADDRESS_UPDATE = `${API_HOME_PATH}/userAddress/update`;

/** 删除用户地址API路径 */
export const API_USER_ADDRESS_DELETE = `${API_HOME_PATH}/userAddress/delete`;

/** 设置默认地址API路径 */
export const API_USER_ADDRESS_DEFAULT = `${API_HOME_PATH}/userAddress/setDefault`;

// ==================== 订单相关API ====================

/** 订单中心API路径 */
export const API_ORDER_CENTER_PATH = `${API_HOME_PATH}/orderCenter`;

/** 获取订单分页API路径 */
export const API_ORDER_PAGE = `${API_ORDER_CENTER_PATH}/page`;

/** 发布订单API路径 */
export const API_ORDER_PUBLISH = `${API_ORDER_CENTER_PATH}/publish`;

/** 接单API路径 */
export const API_ORDER_ACCEPT = `${API_ORDER_CENTER_PATH}/accept`;

/** 获取订单详情API路径 */
export const API_ORDER_DETAIL = `${API_ORDER_CENTER_PATH}/detail`;

// ==================== 帖子相关API ====================

/** 帖子API路径 */
export const API_POST_PATH = `${API_HOME_PATH}/post`;

/** 发布帖子API路径 */
export const API_POST_PUBLISH = `${API_POST_PATH}/publish`;

/** 获取帖子详情API路径 */
export const API_POST_DETAIL = `${API_POST_PATH}/detail`;

/** 更新帖子API路径 */
export const API_POST_UPDATE = `${API_POST_PATH}/update`;

/** 删除帖子API路径 */
export const API_POST_DELETE = `${API_POST_PATH}/delete`;

/** 分页查询帖子API路径 */
export const API_POST_PAGE = `${API_POST_PATH}/page`;

// ==================== 评论相关API ====================

/** 评论API路径 */
export const API_COMMENT_PATH = `${API_HOME_PATH}/comment`;

/** 发布评论API路径 */
export const API_COMMENT_PUBLISH = `${API_COMMENT_PATH}/publish`;

/** 获取帖子评论API路径 */
export const API_COMMENT_LIST = `${API_COMMENT_PATH}/post`;

/** 删除评论API路径 */
export const API_COMMENT_DELETE = `${API_COMMENT_PATH}/delete`;

// ==================== 通知相关API ====================

/** 通知API路径 */
export const API_NOTIFICATION_PATH = `${API_HOME_PATH}/notification`;

/** 获取通知列表API路径 */
export const API_NOTIFICATION_LIST = `${API_NOTIFICATION_PATH}/list`;

/** 标记通知已读API路径 */
export const API_NOTIFICATION_READ = `${API_NOTIFICATION_PATH}/read`;

/** 标记所有通知已读API路径 */
export const API_NOTIFICATION_READ_ALL = `${API_NOTIFICATION_PATH}/readAll`;

// ==================== 对话相关API ====================

/** 对话API路径 */
export const API_CONVERSATION_PATH = `${API_BASE_URL}/user/conversation`;

/** 获取会话列表API路径 */
export const API_CONVERSATION_LIST = `${API_CONVERSATION_PATH}/list`;

/** 获取消息列表API路径 */
export const API_CONVERSATION_MESSAGES = `${API_CONVERSATION_PATH}/messages`;

/** 发送消息API路径 */
export const API_CONVERSATION_SEND = `${API_CONVERSATION_PATH}/send`;

/** 标记消息已读API路径 */
export const API_CONVERSATION_READ = `${API_CONVERSATION_PATH}/read`;

/** 获取未读消息数量API路径 */
export const API_CONVERSATION_UNREAD_COUNT = `${API_CONVERSATION_PATH}/unread/count`;

// ==================== 统一导出对象 ====================

/**
 * API路径常量集合
 */
export const API_PATHS = {
  // 认证相关
  AUTH_LOGIN: API_AUTH_LOGIN,
  AUTH_REGISTER: API_AUTH_REGISTER,
  AUTH_LOGOUT: API_AUTH_LOGOUT,

  // 用户相关
  USER_INFO: API_USER_INFO,
  USER_UPDATE: API_USER_UPDATE,
  USER_CHANGE_PASSWORD: API_USER_CHANGE_PASSWORD,
  USER_BIND_PHONE: API_USER_BIND_PHONE,
  USER_BIND_EMAIL: API_USER_BIND_EMAIL,

  // 用户地址相关
  USER_ADDRESS_LIST: API_USER_ADDRESS_LIST,
  USER_ADDRESS_ADD: API_USER_ADDRESS_ADD,
  USER_ADDRESS_UPDATE: API_USER_ADDRESS_UPDATE,
  USER_ADDRESS_DELETE: API_USER_ADDRESS_DELETE,
  USER_ADDRESS_DEFAULT: API_USER_ADDRESS_DEFAULT,

  // 订单相关
  ORDER_PAGE: API_ORDER_PAGE,
  ORDER_PUBLISH: API_ORDER_PUBLISH,
  ORDER_ACCEPT: API_ORDER_ACCEPT,
  ORDER_DETAIL: API_ORDER_DETAIL,

  // 帖子相关
  POST_PUBLISH: API_POST_PUBLISH,
  POST_DETAIL: API_POST_DETAIL,
  POST_UPDATE: API_POST_UPDATE,
  POST_DELETE: API_POST_DELETE,
  POST_PAGE: API_POST_PAGE,

  // 评论相关
  COMMENT_PUBLISH: API_COMMENT_PUBLISH,
  COMMENT_LIST: API_COMMENT_LIST,
  COMMENT_DELETE: API_COMMENT_DELETE,

  // 通知相关
  NOTIFICATION_LIST: API_NOTIFICATION_LIST,
  NOTIFICATION_READ: API_NOTIFICATION_READ,
  NOTIFICATION_READ_ALL: API_NOTIFICATION_READ_ALL,
} as const;
