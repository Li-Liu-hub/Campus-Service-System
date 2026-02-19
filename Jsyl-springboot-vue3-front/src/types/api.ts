/**
 * API相关类型定义模块
 * 
 * @module types/api
 */

// ==================== 通用API响应类型 ====================

/**
 * API基础响应接口
 */
export interface ApiResponse<T = any> {
  /** 响应状态码 */
  code: number;
  /** 响应消息 */
  msg: string;
  /** 响应数据 */
  data?: T;
  /** 时间戳 */
  timestamp?: number;
}

/**
 * 分页响应数据接口
 */
export interface PageResponse<T> {
  /** 数据列表 */
  records: T[];
  /** 总条数 */
  total: number;
  /** 当前页 */
  current: number;
  /** 每页条数 */
  size: number;
  /** 总页数 */
  pages?: number;
}

/**
 * 分页查询参数接口
 */
export interface PageQueryParams {
  /** 当前页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
}
