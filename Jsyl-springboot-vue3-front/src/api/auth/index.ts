import { post } from '@/utils/request';
import type { ApiResponse } from '@/types';
import {
  API_AUTH_LOGIN,
  API_AUTH_REGISTER,
  API_AUTH_LOGOUT
} from '@/constants/api';
import type {
  LoginParams,
  LoginApiParams,
  LoginResponse,
  RegisterParams,
  RegisterApiParams,
  RegisterResponse,
  LogoutResponse
} from './types';

export function login(data: LoginApiParams): Promise<ApiResponse<LoginResponse>> {
  return post<LoginResponse>(API_AUTH_LOGIN, data);
}

export function register(data: RegisterApiParams): Promise<ApiResponse<RegisterResponse>> {
  return post<RegisterResponse>(API_AUTH_REGISTER, data);
}

export function logout(): Promise<ApiResponse<LogoutResponse>> {
  return post<LogoutResponse>(API_AUTH_LOGOUT);
}

export type {
  LoginParams,
  LoginApiParams,
  LoginResponse,
  RegisterParams,
  RegisterApiParams,
  RegisterResponse,
  LogoutResponse
};
