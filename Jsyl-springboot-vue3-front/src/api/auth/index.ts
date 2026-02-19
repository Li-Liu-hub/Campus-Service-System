/**
 * 认证模块API
 * 
 * 功能说明：
 * - 用户登录认证
 * - 用户注册
 * - 用户登出
 * 
 * @module api/auth
 */

import { post } from '@/utils/request';
import type { ApiResponse } from '@/types';
import {
  API_AUTH_LOGIN,
  API_AUTH_REGISTER,
  API_AUTH_LOGOUT
} from '@/constants/api';
import { logger } from '@/utils/logger';
import type {
  LoginParams,
  LoginApiParams,
  LoginResponse,
  RegisterParams,
  RegisterApiParams,
  RegisterResponse,
  LogoutResponse
} from './types';

// ==================== API函数 ====================

/**
 * 用户登录
 * @description 用户登录认证，成功后返回JWT令牌
 * @param data 登录参数（account, password）
 * @returns Promise<ApiResponse<LoginResponse>> 登录结果，包含token
 */
export function login(data: LoginApiParams): Promise<ApiResponse<LoginResponse>> {
  logger.api.request(API_AUTH_LOGIN, 'POST', data);
  return post<LoginResponse>(API_AUTH_LOGIN, data)
    .then(response => {
      logger.api.success(API_AUTH_LOGIN, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_AUTH_LOGIN, 'POST', error);
      throw error;
    });
}

/**
 * 用户注册
 * @description 用户注册，创建新账号
 * @param data 注册参数（username, email, password, phone）
 * @returns Promise<ApiResponse<RegisterResponse>> 注册结果
 */
export function register(data: RegisterApiParams): Promise<ApiResponse<RegisterResponse>> {
  logger.api.request(API_AUTH_REGISTER, 'POST', data);
  return post<RegisterResponse>(API_AUTH_REGISTER, data)
    .then(response => {
      logger.api.success(API_AUTH_REGISTER, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_AUTH_REGISTER, 'POST', error);
      throw error;
    });
}

/**
 * 用户登出
 * @description 用户退出登录，清除服务器端会话
 * @returns Promise<ApiResponse<LogoutResponse>> 登出结果
 */
export function logout(): Promise<ApiResponse<LogoutResponse>> {
  logger.api.request(API_AUTH_LOGOUT, 'POST');
  return post<LogoutResponse>(API_AUTH_LOGOUT)
    .then(response => {
      logger.api.success(API_AUTH_LOGOUT, 'POST', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_AUTH_LOGOUT, 'POST', error);
      throw error;
    });
}

// ==================== 导出类型 ====================

export type {
  LoginParams,
  LoginApiParams,
  LoginResponse,
  RegisterParams,
  RegisterApiParams,
  RegisterResponse,
  LogoutResponse
};
