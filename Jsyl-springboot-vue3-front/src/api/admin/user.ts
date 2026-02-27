import { get, put, del } from '@/utils/request';
import {
  API_ADMIN_USER_LIST,
  API_ADMIN_USER_DETAIL,
  API_ADMIN_USER_ROLE,
  API_ADMIN_USER_STATUS,
  API_ADMIN_ORDER_PAGE,
  API_ADMIN_POST_PAGE,
  API_ADMIN_ORDER_CANCEL,
  API_ADMIN_POST_DELETE
} from '@/constants/api';

export interface AdminUser {
  id: number;
  account: string;
  nickname: string;
  phone: string;
  permission: number;
  role: number;
  address: string;
}

export interface OrderQuery {
  page: number;
  pageSize: number;
  status?: number;
  typeId?: number;
}

export interface PostQuery {
  page: number;
  pageSize: number;
  status?: number;
  typeId?: number;
}

export const getUserList = () => {
  return get<AdminUser[]>(API_ADMIN_USER_LIST);
};

export const getUserDetail = (id: number) => {
  return get<AdminUser>(`${API_ADMIN_USER_DETAIL}/${id}`);
};

export const updateUserRole = (userId: number, role: number) => {
  return put<string>(`${API_ADMIN_USER_ROLE}?userId=${userId}&role=${role}`);
};

export const updateUserStatus = (userId: number, status: number) => {
  return put<string>(`${API_ADMIN_USER_STATUS}?userId=${userId}&status=${status}`);
};

export const getOrderList = (params: OrderQuery) => {
  return get<any>(API_ADMIN_ORDER_PAGE, { params });
};

export const cancelOrder = (orderId: number) => {
  return put<string>(`${API_ADMIN_ORDER_CANCEL}/${orderId}`);
};

export const getPostList = (params: PostQuery) => {
  return get<any>(API_ADMIN_POST_PAGE, { params });
};

export const deletePost = (postId: number) => {
  return del<string>(`${API_ADMIN_POST_DELETE}/${postId}`);
};

export const getStatistics = () => {
  return get<any>('/jsyl/admin/statistics');
};
