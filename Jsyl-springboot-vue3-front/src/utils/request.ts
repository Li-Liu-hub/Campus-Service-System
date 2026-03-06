import axios, { AxiosError } from 'axios';
import type { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';

export interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data: T;
}

export type RequestConfig = AxiosRequestConfig & {
  responseType?: 'json' | 'blob' | 'arraybuffer' | 'text' | 'stream';
  ignoreErrorTip?: boolean;
};

const REQUEST_TIMEOUT = 10000;
const DEFAULT_HEADERS = {
  'Content-Type': 'application/json;charset=utf-8'
};
const TOKEN_KEY = 'token';
const LOGIN_PATH = '/login';

const service = axios.create({
  baseURL: '',
  timeout: REQUEST_TIMEOUT,
  headers: DEFAULT_HEADERS
});

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const isLoginRequest = config.url?.includes(LOGIN_PATH);

    if (!isLoginRequest) {
      const token = localStorage.getItem(TOKEN_KEY);
      if (token) {
        config.headers['authentication'] = token;
      }
    }

    return config;
  },
  (error: AxiosError): Promise<never> => {
    ElMessage.error('请求发送失败，请检查网络连接');
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>): AxiosResponse<ApiResponse> => {
    const res = response.data;

    if (res.code !== 200) {
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
    handleHttpError(error);
    return Promise.reject(error);
  }
);

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
    } else if (message) {
      ElMessage.error(message);
    }
  } else if (error.request) {
    ElMessage.error('网络异常，请检查网络连接');
  } else {
    ElMessage.error(`请求配置错误: ${error.message || '未知错误'}`);
  }
}

export function request<T = any>(config: RequestConfig): Promise<ApiResponse<T>> {
  return service(config).then((response) => response.data);
}

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

export default service;
