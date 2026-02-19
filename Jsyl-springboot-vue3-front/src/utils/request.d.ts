// src/api/orderCenter.ts
import request from '@/utils/request'


export default request

export interface OrderPageQueryParams {
  page: number;
  pageSize: number;
  orderNo: string;
  orderStatus: number;
}

export interface Order {
  orderNo: string;
  userId: number;
  orderAmount: number;
  orderStatus: number;
  typeId: number;
  createTime: string;
}

export interface OrderPageResponse {
  total: number;
  list: Order[];
}

export interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data: T;
}

// 明确返回类型为 Promise<ApiResponse<OrderPageResponse>>
export function getOrderPage(params: OrderPageQueryParams): Promise<ApiResponse<OrderPageResponse>> {
  return request({
    url: '/api/jsyl/home/ordercenter/page',
    method: 'get',
    params
  })
}