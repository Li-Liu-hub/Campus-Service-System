import { get, post } from '@/utils/request';
import { API_ORDER_PAGE } from '@/constants/api';

export interface OrderPageQueryParams {
  page: number;
  pageSize: number;
  orderNo?: string;
  typeId?: number;
  orderStatus?: number;
  userId?: number;
  acceptorId?: number;
}

export interface Order {
  id?: number;
  orderNo?: string;
  typeId?: number;
  serviceAddress?: string;
  contactPhone?: string;
  requirement?: string;
  orderAmount?: number;
  orderStatus?: number;
  createTime?: number[] | string;
  userId?: number;
  acceptorId?: number;
}

export interface OrderPageResponse {
  records: Order[];
  total: number;
}

export interface PublishOrderParams {
  typeId: number;
  serviceAddress: string;
  contactPhone: string;
  requirement: string;
  orderAmount: number;
}

export interface OrderDetailVO {
  id?: number;
  orderNo?: string;
  serviceAddress?: string;
  requirement?: string;
  contactPhone?: string;
  orderAmount?: number;
  orderStatus?: number;
  orderStatusName?: string;
  typeId?: number;
  typeName?: string;
  createTime?: number[] | string;
  publisher?: any;
  acceptor?: any;
}

export const ORDER_STATUS_TEXT: Record<number, string> = {
  0: '待接单',
  1: '已接单',
  2: '已完成',
  3: '已取消',
};

export const ORDER_TYPE_TEXT: Record<number, string> = {
  1: '其他',
  2: '替课',
  3: '帮助打印',
  4: '帮忙购物',
  5: '帮取外卖',
  6: '帮拿快递',
};

export const OrderStatus = {
  PENDING_ACCEPT: 0,
  ACCEPTED: 1,
  COMPLETED: 2,
  CANCELLED: 3,
};

export function getOrderPage(params: OrderPageQueryParams) {
  return get<OrderPageResponse>(API_ORDER_PAGE, params);
}

export function publishOrder(data: PublishOrderParams) {
  return post<string>('/jsyl/home/orderCenter/publish', data);
}

export function acceptOrder(orderId: number) {
  return post<string>(`/jsyl/home/orderCenter/accept/${orderId}`);
}

export function getOrderDetail(id: number) {
  return get<OrderDetailVO>(`/jsyl/home/orderCenter/detail/${id}`);
}
