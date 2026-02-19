/**
 * 用户模块API
 * 
 * 功能说明：
 * - 用户信息获取与更新
 * - 密码管理
 * - 手机/邮箱绑定
 * 
 * @module api/user
 */

import { get, post } from '@/utils/request';
import type { ApiResponse } from '@/types';
import {
  API_USER_INFO,
  API_USER_UPDATE,
  API_USER_CHANGE_PASSWORD,
  API_USER_BIND_PHONE,
  API_USER_BIND_EMAIL
} from '@/constants/api';
import { logger } from '@/utils/logger';
import type {
  UserInfo,
  UpdateUserInfoParams,
  ChangePasswordParams,
  BindPhoneParams,
  BindEmailParams
} from './types';

// ==================== API函数 ====================

/**
 * 获取用户信息
 * @description 获取当前登录用户的详细信息
 * @returns Promise<ApiResponse<UserInfo>> 用户信息
 */
export function getUserInfo(): Promise<ApiResponse<UserInfo>> {
  logger.api.request(API_USER_INFO, 'GET');
  return get<UserInfo>(API_USER_INFO)
    .then(response => {
      logger.api.success(API_USER_INFO, 'GET', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_USER_INFO, 'GET', error);
      throw error;
    });
}

/**
 * 更新用户信息
 * @description 更新当前登录用户的个人信息
 * @param data 更新参数（username, email, phone, avatar）
 * @returns Promise<ApiResponse<void>> 更新结果
 */
export function updateUserInfo(data: UpdateUserInfoParams): Promise<ApiResponse<void>> {
  logger.api.request(API_USER_UPDATE, 'POST', data);
  return post<void>(API_USER_UPDATE, data)
    .then(response => {
      logger.api.success(API_USER_UPDATE, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_USER_UPDATE, 'POST', error);
      throw error;
    });
}

/**
 * 修改密码
 * @description 修改当前登录用户的密码
 * @param data 密码修改参数（oldPassword, newPassword）
 * @returns Promise<ApiResponse<void>> 修改结果
 */
export function changePassword(data: ChangePasswordParams): Promise<ApiResponse<void>> {
  logger.api.request(API_USER_CHANGE_PASSWORD, 'POST', data);
  return post<void>(API_USER_CHANGE_PASSWORD, data)
    .then(response => {
      logger.api.success(API_USER_CHANGE_PASSWORD, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_USER_CHANGE_PASSWORD, 'POST', error);
      throw error;
    });
}

/**
 * 绑定手机
 * @description 绑定或更换用户手机号码
 * @param data 绑定参数（phone, code）
 * @returns Promise<ApiResponse<void>> 绑定结果
 */
export function bindPhone(data: BindPhoneParams): Promise<ApiResponse<void>> {
  logger.api.request(API_USER_BIND_PHONE, 'POST', data);
  return post<void>(API_USER_BIND_PHONE, data)
    .then(response => {
      logger.api.success(API_USER_BIND_PHONE, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_USER_BIND_PHONE, 'POST', error);
      throw error;
    });
}

/**
 * 绑定邮箱
 * @description 绑定或更换用户邮箱地址
 * @param data 绑定参数（email, code）
 * @returns Promise<ApiResponse<void>> 绑定结果
 */
export function bindEmail(data: BindEmailParams): Promise<ApiResponse<void>> {
  logger.api.request(API_USER_BIND_EMAIL, 'POST', data);
  return post<void>(API_USER_BIND_EMAIL, data)
    .then(response => {
      logger.api.success(API_USER_BIND_EMAIL, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_USER_BIND_EMAIL, 'POST', error);
      throw error;
    });
}

// ==================== 导出类型 ====================

export type {
  UserInfo,
  UpdateUserInfoParams,
  ChangePasswordParams,
  BindPhoneParams,
  BindEmailParams
};
