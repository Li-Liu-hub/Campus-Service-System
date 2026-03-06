
import { get, post } from '@/utils/request';
import type { ApiResponse } from '@/types';
import {
  API_USER_INFO,
  API_USER_UPDATE,
  API_USER_CHANGE_PASSWORD,
  API_USER_BIND_PHONE,
  API_USER_BIND_EMAIL
} from '@/constants/api';
import type {
  UserInfo,
  UpdateUserInfoParams,
  ChangePasswordParams,
  BindPhoneParams,
  BindEmailParams
} from './types';


export function getUserInfo(): Promise<ApiResponse<UserInfo>> {
  return get<UserInfo>(API_USER_INFO)
}

export function updateUserInfo(data: UpdateUserInfoParams): Promise<ApiResponse<void>> {
  return post<void>(API_USER_UPDATE, data)
}

export function changePassword(data: ChangePasswordParams): Promise<ApiResponse<void>> {
  return post<void>(API_USER_CHANGE_PASSWORD, data)
}

export function bindPhone(data: BindPhoneParams): Promise<ApiResponse<void>> {
  return post<void>(API_USER_BIND_PHONE, data)
}

export function bindEmail(data: BindEmailParams): Promise<ApiResponse<void>> {
  return post<void>(API_USER_BIND_EMAIL, data)
}


export type {
  UserInfo,
  UpdateUserInfoParams,
  ChangePasswordParams,
  BindPhoneParams,
  BindEmailParams
};
