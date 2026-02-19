/**
 * 配置常量模块
 * 
 * @module constants/config
 */

// ==================== 应用配置 ====================

/** 应用标题 */
export const APP_TITLE = '校园服务平台';

/** 应用版本 */
export const APP_VERSION = '1.0.0';

/** 应用描述 */
export const APP_DESCRIPTION = '校园服务管理系统';

// ==================== 存储配置 ====================

/** Token存储键 */
export const STORAGE_TOKEN_KEY = 'token';

/** 账号存储键 */
export const STORAGE_ACCOUNT_KEY = 'login_account';

/** 用户信息存储键 */
export const STORAGE_USER_INFO_KEY = 'user_info';

// ==================== 请求配置 ====================

/** 请求超时时间 (毫秒) */
export const REQUEST_TIMEOUT = 30000;

/** 请求重试次数 */
export const REQUEST_RETRY_COUNT = 1;

/** 请求重试间隔 (毫秒) */
export const REQUEST_RETRY_INTERVAL = 1000;

// ==================== 表单验证配置 ====================

/** 用户名最小长度 */
export const USERNAME_MIN_LENGTH = 3;

/** 用户名最大长度 */
export const USERNAME_MAX_LENGTH = 20;

/** 密码最小长度 */
export const PASSWORD_MIN_LENGTH = 6;

/** 密码最大长度 */
export const PASSWORD_MAX_LENGTH = 20;

/** 手机号正则 */
export const PHONE_REGEX = /^1[3-9]\d{9}$/;

/** 邮箱正则 */
export const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// ==================== 分页配置 ====================

/** 默认分页大小 */
export const DEFAULT_PAGE_SIZE = 10;

/** 分页大小选项 */
export const PAGE_SIZE_OPTIONS = [10, 20, 50, 100];
