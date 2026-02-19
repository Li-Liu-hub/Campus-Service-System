/**
 * HTTP请求工具模块
 * 
 * 功能说明：
 * - 封装axios实例，提供统一的HTTP请求处理
 * - 请求拦截：自动添加Token认证头
 * - 响应拦截：统一处理业务错误和HTTP错误
 * - 提供便捷的请求方法（get, post, put, del）
 * 
 * @module utils/request
 */

import axios, { AxiosError } from 'axios';
import type { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';

// ==================== 类型定义 ====================

/**
 * API统一响应类型
 * @description 匹配后端返回的标准响应格式
 */
export interface ApiResponse<T = any> {
  /** 业务状态码（200=成功） */
  code: number;
  /** 响应消息 */
  msg: string;
  /** 响应数据 */
  data: T;
}

/**
 * 请求配置类型
 * @description 扩展axios配置，添加自定义选项
 */
export type RequestConfig = AxiosRequestConfig & {
  /** 响应类型 */
  responseType?: 'json' | 'blob' | 'arraybuffer' | 'text' | 'stream';
  /** 是否忽略统一错误提示（由组件自行处理） */
  ignoreErrorTip?: boolean;
};

// ==================== 常量定义 ====================

/** 请求超时时间（毫秒） */
const REQUEST_TIMEOUT = 10000;

/** 默认请求头 */
const DEFAULT_HEADERS = {
  'Content-Type': 'application/json;charset=utf-8'
};

/** Token存储键名 */
const TOKEN_KEY = 'token';

/** 登录接口路径标识 */
const LOGIN_PATH = '/login';

// ==================== Axios实例创建 ====================

/**
 * Axios实例
 * @description 配置了基础URL、超时时间和默认请求头
 */
const service = axios.create({
  baseURL: '',
  timeout: REQUEST_TIMEOUT,
  headers: DEFAULT_HEADERS
});

// ==================== 请求拦截器 ====================

/**
 * 请求拦截器
 * @description 自动添加Token到请求头，跳过登录接口
 */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const isLoginRequest = config.url?.includes(LOGIN_PATH);
    
    if (!isLoginRequest) {
      const token = localStorage.getItem(TOKEN_KEY);
      if (token) {
        config.headers['token'] = token;
        config.headers['authentication'] = token;
      }
    }

    if (import.meta.env.DEV) {
      console.log('📤 请求配置:', {
        url: config.url,
        method: config.method,
        params: config.params,
        data: config.data,
        headers: config.headers
      });
    }

    return config;
  },
  (error: AxiosError): Promise<never> => {
    console.error('❌ 请求拦截器错误:', error);
    ElMessage.error('请求发送失败，请检查网络连接');
    return Promise.reject(error);
  }
);

// ==================== 响应拦截器 ====================

/**
 * 响应拦截器
 * @description 统一处理业务错误和HTTP错误
 */
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>): AxiosResponse<ApiResponse> => {
    if (import.meta.env.DEV) {
      console.log('📥 响应数据:', response.data);
    }

    const res = response.data;

    if (res.code !== 200) {
      console.warn('⚠️ 业务错误:', res);
      
      const ignoreTip = (response.config as RequestConfig)?.ignoreErrorTip;
      const isLoginRequest = response.config.url?.includes(LOGIN_PATH);
      
      if (!ignoreTip && !isLoginRequest) {
        ElMessage.error(res.msg || '操作失败');
      }
      
      return Promise.reject(res) as never;
    }

    return response;
  },
  (error: AxiosError): Promise<never> => {
    console.error('❌ 响应拦截器错误:', error);
    
    handleHttpError(error);
    return Promise.reject(error);
  }
);

// ==================== 错误处理函数 ====================

/**
 * 处理HTTP错误
 * @param error Axios错误对象
 */
function handleHttpError(error: AxiosError): void {
  const isLoginRequest = error.config?.url?.includes(LOGIN_PATH);

  if (error.response) {
    const status = error.response.status;
    const errorMessages: Record<number, string> = {
      401: isLoginRequest ? '' : '登录已过期，请重新登录',
      403: '权限不足，无法访问',
      404: '请求的资源不存在',
      500: '服务器内部错误，请稍后重试'
    };

    const message = errorMessages[status] || `请求失败 (HTTP ${status})`;
    
    if (status === 401 && isLoginRequest) {
      console.log('⚠️ 登录请求401：账号/密码错误（由组件处理）');
    } else if (message) {
      ElMessage.error(message);
    }
  } else if (error.request) {
    ElMessage.error('网络异常，请检查网络连接');
  } else {
    ElMessage.error(`请求配置错误: ${error.message || '未知错误'}`);
  }
}

// ==================== 请求方法封装 ====================

/**
 * 通用请求方法
 * @description 封装axios请求，返回业务数据
 * @param config 请求配置
 * @returns Promise<ApiResponse<T>> 业务响应数据
 */
export function request<T = any>(config: RequestConfig): Promise<ApiResponse<T>> {
  return service(config).then((response) => response.data);
}

/**
 * GET请求
 * @param url 请求地址
 * @param params URL查询参数
 * @param config 额外配置
 * @returns Promise<ApiResponse<T>> 业务响应数据
 */
export function get<T = any>(
  url: string,
  params?: Record<string, any>,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return service({
    url,
    method: 'get',
    params,
    ...config
  }).then((response) => response.data);
}

/**
 * POST请求
 * @param url 请求地址
 * @param data 请求体数据
 * @param config 额外配置
 * @returns Promise<ApiResponse<T>> 业务响应数据
 */
export function post<T = any>(
  url: string,
  data?: Record<string, any>,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return service({
    url,
    method: 'post',
    data,
    ...config
  }).then((response) => response.data);
}

/**
 * PUT请求
 * @param url 请求地址
 * @param data 请求体数据
 * @param config 额外配置
 * @returns Promise<ApiResponse<T>> 业务响应数据
 */
export function put<T = any>(
  url: string,
  data?: Record<string, any>,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return service({
    url,
    method: 'put',
    data,
    ...config
  }).then((response) => response.data);
}

/**
 * DELETE请求
 * @param url 请求地址
 * @param params URL查询参数
 * @param config 额外配置
 * @returns Promise<ApiResponse<T>> 业务响应数据
 */
export function del<T = any>(
  url: string,
  params?: Record<string, any>,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return service({
    url,
    method: 'delete',
    params,
    ...config
  }).then((response) => response.data);
}

// ==================== 导出 ====================

export default service;
