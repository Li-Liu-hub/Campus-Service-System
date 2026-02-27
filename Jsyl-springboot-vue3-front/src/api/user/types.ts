/**
 * 用户模块类型定义
 * 
 * @module api/user/types
 */

// ==================== 用户信息相关类型 ====================

/**
 * 用户信息接口
 */
export interface UserInfo {
  /** 用户ID */
  id?: number;
  /** 用户名 */
  username?: string;
  /** 邮箱地址 */
  email?: string;
  /** 手机号码 */
  phone?: string;
  /** 头像URL */
  avatar?: string;
  /** 账号创建时间 */
  createdAt?: string;
  /** 账号更新时间 */
  updatedAt?: string;
  /** 账户余额 */
  accountBalance?: number;
}

/**
 * 更新用户信息参数接口
 */
export interface UpdateUserInfoParams {
  /** 用户名 */
  username?: string;
  /** 邮箱地址 */
  email?: string;
  /** 手机号码 */
  phone?: string;
  /** 头像URL */
  avatar?: string;
}

// ==================== 密码相关类型 ====================

/**
 * 修改密码参数接口
 */
export interface ChangePasswordParams {
  /** 原密码 */
  oldPassword: string;
  /** 新密码 */
  newPassword: string;
}

// ==================== 绑定相关类型 ====================

/**
 * 绑定手机参数接口
 */
export interface BindPhoneParams {
  /** 手机号码 */
  phone: string;
  /** 验证码 */
  code: string;
}

/**
 * 绑定邮箱参数接口
 */
export interface BindEmailParams {
  /** 邮箱地址 */
  email: string;
  /** 验证码 */
  code: string;
}
