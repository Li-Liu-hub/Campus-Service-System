/**
 * 通用类型定义模块
 * 
 * @module types/common
 */

// ==================== 分页相关类型 ====================

/**
 * 分页信息接口
 */
export interface Pagination {
  /** 当前页码 */
  currentPage: number;
  /** 每页条数 */
  pageSize: number;
  /** 总条数 */
  total: number;
  /** 总页数 */
  totalPages?: number;
}

// ==================== 选择项类型 ====================

/**
 * 选择项接口
 */
export interface SelectOption<T = string | number> {
  /** 选项值 */
  value: T;
  /** 选项标签 */
  label: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 子选项 */
  children?: SelectOption<T>[];
}

/**
 * 树形选择项接口
 */
export interface TreeSelectOption<T = string | number> extends SelectOption<T> {
  /** 子节点 */
  children?: TreeSelectOption<T>[];
  /** 是否展开 */
  expanded?: boolean;
  /** 是否选中 */
  checked?: boolean;
  /** 是否半选中 */
  halfChecked?: boolean;
  /** 是否叶子节点 */
  isLeaf?: boolean;
}

// ==================== 排序类型 ====================

/**
 * 排序方向常量
 */
export const SortDirection = {
  /** 升序 */
  ASC: 'asc',
  /** 降序 */
  DESC: 'desc'
} as const;

/**
 * 排序方向类型
 */
export type SortDirection = typeof SortDirection[keyof typeof SortDirection];

/**
 * 排序条件接口
 */
export interface SortCondition {
  /** 排序字段 */
  field: string;
  /** 排序方向 */
  direction: SortDirection;
}

// ==================== 时间范围类型 ====================

/**
 * 时间范围接口
 */
export interface DateRange {
  /** 开始时间 */
  startDate: Date | string | null;
  /** 结束时间 */
  endDate: Date | string | null;
}

// ==================== 文件相关类型 ====================

/**
 * 文件信息接口
 */
export interface FileInfo {
  /** 文件ID */
  id?: string | number;
  /** 文件名 */
  name: string;
  /** 文件大小 (字节) */
  size: number;
  /** 文件类型 */
  type: string;
  /** 文件URL */
  url?: string;
  /** 文件路径 */
  path?: string;
  /** 上传时间 */
  uploadTime?: string;
  /** 文件状态 */
  status?: 'pending' | 'uploading' | 'success' | 'error';
  /** 上传进度 */
  progress?: number;
  /** 错误信息 */
  error?: string;
}

/**
 * 文件上传配置接口
 */
export interface UploadConfig {
  /** 最大文件大小 (MB) */
  maxSize?: number;
  /** 允许的文件类型 */
  accept?: string;
  /** 最大文件数量 */
  maxCount?: number;
  /** 是否支持多选 */
  multiple?: boolean;
  /** 是否支持拖拽 */
  drag?: boolean;
}

// ==================== 表格相关类型 ====================

/**
 * 表格列配置接口
 */
export interface TableColumn {
  /** 列属性 */
  prop: string;
  /** 列标题 */
  label: string;
  /** 列宽度 */
  width?: string | number;
  /** 最小宽度 */
  minWidth?: string | number;
  /** 是否固定 */
  fixed?: 'left' | 'right' | boolean;
  /** 是否排序 */
  sortable?: boolean;
  /** 是否可编辑 */
  editable?: boolean;
  /** 对齐方式 */
  align?: 'left' | 'center' | 'right';
  /** 是否显示 */
  visible?: boolean;
  /** 自定义渲染函数 */
  render?: (row: any, column: TableColumn, index: number) => any;
  /** 自定义插槽名称 */
  slot?: string;
  /** 格式化函数 */
  formatter?: (row: any, column: TableColumn, cellValue: any, index: number) => string;
}

// ==================== 消息类型 ====================

/**
 * 消息类型常量
 */
export const MessageType = {
  /** 成功消息 */
  SUCCESS: 'success',
  /** 警告消息 */
  WARNING: 'warning',
  /** 错误消息 */
  ERROR: 'error',
  /** 信息消息 */
  INFO: 'info'
} as const;

/**
 * 消息类型类型
 */
export type MessageType = typeof MessageType[keyof typeof MessageType];

/**
 * 消息配置接口
 */
export interface MessageConfig {
  /** 消息类型 */
  type: MessageType;
  /** 消息内容 */
  content: string;
  /** 持续时间 (毫秒) */
  duration?: number;
  /** 是否可关闭 */
  closable?: boolean;
  /** 关闭回调 */
  onClose?: VoidFunction;
}
