/**
 * 通知模块 API
 */

import { get, post, put } from '@/utils/request';
import {
  API_NOTIFICATION_LIST,
  API_NOTIFICATION_READ,
  API_NOTIFICATION_READ_ALL
} from '@/constants/api';
import type { Notification } from '@/types/entities';

export interface NotificationQueryParams {
  page: number;
  pageSize: number;
  type?: number;
  isRead?: boolean;
}

export interface NotificationPageResponse {
  records: Notification[];
  total: number;
}

export function getNotificationList(params: NotificationQueryParams) {
  return get<NotificationPageResponse>(API_NOTIFICATION_LIST, params);
}

export function markNotificationRead(id: number) {
  return put<string>(`${API_NOTIFICATION_READ}/${id}`);
}

export function markAllNotificationsRead() {
  return post<string>(API_NOTIFICATION_READ_ALL);
}
