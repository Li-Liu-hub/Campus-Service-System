/**
 * API统一导出模块
 * 
 * @module api
 */

export { login, register, logout } from './auth';
export type {
  LoginParams,
  LoginApiParams,
  LoginResponse,
  RegisterParams,
  RegisterApiParams,
  RegisterResponse,
  LogoutResponse
} from './auth';

export {
  getUserInfo,
  updateUserInfo,
  changePassword,
  bindPhone,
  bindEmail
} from './user';
export type {
  UserInfo,
  UpdateUserInfoParams,
  ChangePasswordParams,
  BindPhoneParams,
  BindEmailParams
} from './user';

export { getOrderPage } from './order';
export type {
  Order,
  OrderPageQueryParams,
  OrderPageResponse,
  OrderStatus,
  ORDER_STATUS_TEXT,
  ORDER_TYPE_TEXT
} from './order';
