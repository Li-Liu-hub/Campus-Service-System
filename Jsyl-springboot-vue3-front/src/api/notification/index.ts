
import { get, post, put, del } from '@/utils/request';
import {
  API_NOTIFICATION_LIST,
  API_NOTIFICATION_READ,
  API_NOTIFICATION_READ_ALL,
  API_NOTIFICATION_PATH
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
  unreadCount?: number;
}

export function getNotificationList(params: NotificationQueryParams) {
  return get<NotificationPageResponse>(API_NOTIFICATION_LIST, params);
}

export function getUnreadCount() {
  return get<number>(`${API_NOTIFICATION_PATH}/unreadCount`);
}

export function markNotificationRead(id: number) {
  return put<string>(`${API_NOTIFICATION_READ}/${id}`);
}

export function markAllNotificationsRead() {
  return post<string>(API_NOTIFICATION_READ_ALL);
}

export function deleteNotification(id: number) {
  return del<string>(`${API_NOTIFICATION_PATH}/delete/${id}`);
}
