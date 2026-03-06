

export interface UserInfo {
  /** 用户ID */
  id?: number;
  /** 用户名 */
  username?: string;
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

export interface UpdateUserInfoParams {
  /** 用户名 */
  username?: string;
  /** 手机号码 */
  phone?: string;
  /** 头像URL */
  avatar?: string;
  /** 校区ID */
  campusId?: number;
}


export interface ChangePasswordParams {
  /** 原密码 */
  oldPassword: string;
  /** 新密码 */
  newPassword: string;
}


export interface BindPhoneParams {
  /** 手机号码 */
  phone: string;
  /** 验证码 */
  code: string;
}

export interface BindEmailParams {
  /** 邮箱地址 */
  email: string;
  /** 验证码 */
  code: string;
}
