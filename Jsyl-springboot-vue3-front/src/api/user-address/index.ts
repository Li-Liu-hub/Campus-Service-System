/**
 * 用户地址模块 API
 */

import { get, post, put, del } from '@/utils/request';
import {
  API_USER_ADDRESS_LIST,
  API_USER_ADDRESS_ADD,
  API_USER_ADDRESS_UPDATE,
  API_USER_ADDRESS_DELETE,
  API_USER_ADDRESS_DEFAULT
} from '@/constants/api';
import type { UserAddress } from '@/types/entities';

export interface UserAddressParams {
  contactName: string;
  contactPhone: string;
  addressDetail: string;
  isDefault?: boolean;
}

export function getUserAddressList() {
  return get<UserAddress[]>(API_USER_ADDRESS_LIST);
}

export function addUserAddress(data: UserAddressParams) {
  return post<string>(API_USER_ADDRESS_ADD, data);
}

export function updateUserAddress(data: any) {
  return put<string>(API_USER_ADDRESS_UPDATE, data);
}

export function deleteUserAddress(id: number) {
  return del<string>(`${API_USER_ADDRESS_DELETE}/${id}`);
}

export function setDefaultUserAddress(id: number) {
  return post<string>(`${API_USER_ADDRESS_DEFAULT}/${id}`);
}
