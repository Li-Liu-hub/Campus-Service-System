

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

export interface PageQueryParams {
  /** 当前页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
}
