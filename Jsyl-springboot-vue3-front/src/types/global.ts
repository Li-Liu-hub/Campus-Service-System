/**
 * 全局类型定义模块
 * 
 * @module types/global
 */

// ==================== 通用对象类型 ====================

/**
 * 任意对象类型
 */
export interface AnyObject {
  [key: string]: any;
}

/**
 * 字符串键值对类型
 */
export interface StringMap {
  [key: string]: string;
}

/**
 * 数字键值对类型
 */
export interface NumberMap {
  [key: string]: number;
}

/**
 * 布尔值键值对类型
 */
export interface BooleanMap {
  [key: string]: boolean;
}

// ==================== 函数类型 ====================

/**
 * 空函数类型
 */
export type VoidFunction = () => void;

/**
 * 任意函数类型
 */
export type AnyFunction = (...args: any[]) => any;

/**
 * 异步函数类型
 */
export type AsyncFunction<T = any> = (...args: any[]) => Promise<T>;

// ==================== 可空类型 ====================

/**
 * 可空类型
 */
export type Nullable<T> = T | null;

/**
 * 可选类型
 */
export type Optional<T> = T | undefined;

/**
 * 可空且可选类型
 */
export type Maybe<T> = T | null | undefined;

// ==================== 数组类型 ====================

/**
 * 非空数组类型
 */
export type NonEmptyArray<T> = [T, ...T[]];

/**
 * 只读数组类型
 */
export type ReadonlyArray<T> = readonly T[];

// ==================== 元组类型 ====================

/**
 * 二元组类型
 */
export type Tuple2<T1, T2> = [T1, T2];

/**
 * 三元组类型
 */
export type Tuple3<T1, T2, T3> = [T1, T2, T3];

// ==================== 枚举类型 ====================

/**
 * 基础状态常量
 */
export const BaseStatus = {
  /** 禁用 */
  DISABLED: 0,
  /** 启用 */
  ENABLED: 1
} as const;

/**
 * 基础状态类型
 */
export type BaseStatus = typeof BaseStatus[keyof typeof BaseStatus];

/**
 * 删除状态常量
 */
export const DeleteStatus = {
  /** 未删除 */
  NOT_DELETED: 0,
  /** 已删除 */
  DELETED: 1
} as const;

/**
 * 删除状态类型
 */
export type DeleteStatus = typeof DeleteStatus[keyof typeof DeleteStatus];

// ==================== 事件类型 ====================

/**
 * 鼠标事件处理函数类型
 */
export type MouseEventHandler = (event: MouseEvent) => void;

/**
 * 键盘事件处理函数类型
 */
export type KeyboardEventHandler = (event: KeyboardEvent) => void;

/**
 * 表单事件处理函数类型
 */
export type FormEventHandler = (event: Event) => void;

// ==================== 回调类型 ====================

/**
 * 成功回调类型
 */
export type SuccessCallback<T = any> = (data: T) => void;

/**
 * 失败回调类型
 */
export type ErrorCallback = (error: any) => void;

/**
 * 完成回调类型
 */
export type CompleteCallback = () => void;
