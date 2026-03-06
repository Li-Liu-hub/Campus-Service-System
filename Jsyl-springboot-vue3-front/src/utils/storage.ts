
import {
  STORAGE_TOKEN_KEY,
  STORAGE_ACCOUNT_KEY,
  STORAGE_USER_INFO_KEY
} from '@/constants/config';


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

export function setStorage<T>(key: string, value: T): void {
  try {
    const serializedValue = JSON.stringify(value);
    localStorage.setItem(key, serializedValue);
  } catch (error) {
    console.error(`Failed to set storage item: ${key}`, error);
  }
}

export function removeStorage(key: string): void {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Failed to remove storage item: ${key}`, error);
  }
}

export function clearStorage(): void {
  try {
    localStorage.clear();
  } catch (error) {
    console.error('Failed to clear storage', error);
  }
}


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

export function setToken(token: string): void {
  try {
    localStorage.setItem(STORAGE_TOKEN_KEY, token);
  } catch (error) {
    console.error(`Failed to set token`, error);
  }
}

export function removeToken(): void {
  removeStorage(STORAGE_TOKEN_KEY);
}


export function getSavedAccount(): string | null {
  return getStorage<string>(STORAGE_ACCOUNT_KEY);
}

export function setSavedAccount(account: string): void {
  setStorage(STORAGE_ACCOUNT_KEY, account);
}

export function removeSavedAccount(): void {
  removeStorage(STORAGE_ACCOUNT_KEY);
}


export function getUserInfo<T = any>(): T | null {
  return getStorage<T>(STORAGE_USER_INFO_KEY);
}

export function setUserInfo<T>(userInfo: T): void {
  setStorage(STORAGE_USER_INFO_KEY, userInfo);
}

export function removeUserInfo(): void {
  removeStorage(STORAGE_USER_INFO_KEY);
}


export function clearAuthInfo(): void {
  removeToken();
  removeUserInfo();
}
