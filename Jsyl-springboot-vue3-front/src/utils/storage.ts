/**
 * 本地存储工具模块
 * 
 * @module utils/storage
 */

import {
  STORAGE_TOKEN_KEY,
  STORAGE_ACCOUNT_KEY,
  STORAGE_USER_INFO_KEY
} from '@/constants/config';

// ==================== 基础存储操作 ====================

/**
 * 获取存储项
 * @param key 存储键
 * @returns 存储值
 */
export function getStorage<T = any>(key: string): T | null {
  try {
    const value = localStorage.getItem(key);
    if (value === null) {
      return null;
    }
    return JSON.parse(value) as T;
  } catch (error) {
    console.error(`Failed to get storage item: ${key}`, error);
    return null;
  }
}

/**
 * 设置存储项
 * @param key 存储键
 * @param value 存储值
 */
export function setStorage<T>(key: string, value: T): void {
  try {
    const serializedValue = JSON.stringify(value);
    localStorage.setItem(key, serializedValue);
  } catch (error) {
    console.error(`Failed to set storage item: ${key}`, error);
  }
}

/**
 * 移除存储项
 * @param key 存储键
 */
export function removeStorage(key: string): void {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Failed to remove storage item: ${key}`, error);
  }
}

/**
 * 清空所有存储项
 */
export function clearStorage(): void {
  try {
    localStorage.clear();
  } catch (error) {
    console.error('Failed to clear storage', error);
  }
}

// ==================== Token相关操作 ====================

/**
 * 获取Token
 * @returns Token字符串
 */
export function getToken(): string | null {
  try {
    const value = localStorage.getItem(STORAGE_TOKEN_KEY);
    if (value === null) {
      return null;
    }
    return value;
  } catch (error) {
    console.error(`Failed to get token`, error);
    return null;
  }
}

/**
 * 设置Token
 * @param token Token字符串
 */
export function setToken(token: string): void {
  try {
    localStorage.setItem(STORAGE_TOKEN_KEY, token);
  } catch (error) {
    console.error(`Failed to set token`, error);
  }
}

/**
 * 移除Token
 */
export function removeToken(): void {
  removeStorage(STORAGE_TOKEN_KEY);
}

// ==================== 账号相关操作 ====================

/**
 * 获取保存的账号
 * @returns 账号字符串
 */
export function getSavedAccount(): string | null {
  return getStorage<string>(STORAGE_ACCOUNT_KEY);
}

/**
 * 保存账号
 * @param account 账号字符串
 */
export function setSavedAccount(account: string): void {
  setStorage(STORAGE_ACCOUNT_KEY, account);
}

/**
 * 移除保存的账号
 */
export function removeSavedAccount(): void {
  removeStorage(STORAGE_ACCOUNT_KEY);
}

// ==================== 用户信息相关操作 ====================

/**
 * 获取用户信息
 * @returns 用户信息对象
 */
export function getUserInfo<T = any>(): T | null {
  return getStorage<T>(STORAGE_USER_INFO_KEY);
}

/**
 * 设置用户信息
 * @param userInfo 用户信息对象
 */
export function setUserInfo<T>(userInfo: T): void {
  setStorage(STORAGE_USER_INFO_KEY, userInfo);
}

/**
 * 移除用户信息
 */
export function removeUserInfo(): void {
  removeStorage(STORAGE_USER_INFO_KEY);
}

// ==================== 登出操作 ====================

/**
 * 清除登录信息
 */
export function clearAuthInfo(): void {
  removeToken();
  removeUserInfo();
}
