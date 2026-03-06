

export interface AnyObject {
  [key: string]: any;
}

export interface StringMap {
  [key: string]: string;
}

export interface NumberMap {
  [key: string]: number;
}

export interface BooleanMap {
  [key: string]: boolean;
}


export type VoidFunction = () => void;

export type AnyFunction = (...args: any[]) => any;

export type AsyncFunction<T = any> = (...args: any[]) => Promise<T>;


export type Nullable<T> = T | null;

export type Optional<T> = T | undefined;

export type Maybe<T> = T | null | undefined;


export type NonEmptyArray<T> = [T, ...T[]];

export type ReadonlyArray<T> = readonly T[];


export type Tuple2<T1, T2> = [T1, T2];

export type Tuple3<T1, T2, T3> = [T1, T2, T3];


export const BaseStatus = {
  /** 禁用 */
  DISABLED: 0,
  /** 启用 */
  ENABLED: 1
} as const;

export type BaseStatus = typeof BaseStatus[keyof typeof BaseStatus];

export const DeleteStatus = {
  /** 未删除 */
  NOT_DELETED: 0,
  /** 已删除 */
  DELETED: 1
} as const;

export type DeleteStatus = typeof DeleteStatus[keyof typeof DeleteStatus];


export type MouseEventHandler = (event: MouseEvent) => void;

export type KeyboardEventHandler = (event: KeyboardEvent) => void;

export type FormEventHandler = (event: Event) => void;


export type SuccessCallback<T = any> = (data: T) => void;

export type ErrorCallback = (error: any) => void;

export type CompleteCallback = () => void;
