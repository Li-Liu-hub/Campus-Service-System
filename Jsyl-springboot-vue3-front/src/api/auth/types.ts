

export interface LoginParams {
  /** 用户账号 */
  account: string;
  /** 用户密码 */
  password: string;
  /** 是否记住账号（前端使用，不传递给后端） */
  remember?: boolean;
}

export type LoginApiParams = Omit<LoginParams, 'remember'>;

export interface LoginResponse {
  /** 用户ID */
  id: number;
  /** 用户账号 */
  account: string;
  /** JWT认证令牌 */
  token: string;
}


export interface RegisterParams {
  /** 用户名 */
  username: string;
  /** 密码 */
  password: string;
  /** 确认密码 */
  confirmPassword: string;
  /** 手机号码（可选） */
  phone?: string;
}

export interface RegisterApiParams {
  /** 用户名 */
  username: string;
  /** 用户账号（与username相同，用于兼容后端） */
  account?: string;
  /** 密码 */
  password: string;
  /** 手机号码（可选） */
  phone?: string;
}

export interface RegisterResponse {
  /** 用户ID */
  id: number;
  /** 用户名 */
  username: string;
  /** 手机号码（可选） */
  phone?: string;
  /** JWT认证令牌 */
  token?: string;
  /** 创建时间 */
  createdAt?: string;
}


export interface LogoutResponse {
  /** 是否登出成功 */
  success: boolean;
  /** 消息 */
  message?: string;
}
