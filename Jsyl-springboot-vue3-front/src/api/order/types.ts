/**
 * 订单模块类型定义
 *
 * @module api/order/types
 */

// ==================== 常量定义 ====================

/**
 * 订单状态常量
 */
export const OrderStatus = {
  /** 未接单 */
  PENDING_ACCEPT: 0,
  /** 已接单 */
  ACCEPTED: 1,
  /** 完成订单 */
  COMPLETED: 2
} as const;

/**
 * 订单类型常量
 */
export const OrderType = {
  /** 其他 */
  OTHER: 1,
  /** 替课 */
  SUBSTITUTE: 2,
  /** 帮助打印 */
  PRINT: 3,
  /** 帮忙购物 */
  SHOPPING: 4,
  /** 帮取外卖 */
  TAKEOUT: 5,
  /** 帮拿快递 */
  EXPRESS: 6
} as const;

/**
 * 订单状态类型
 */
export type OrderStatus = typeof OrderStatus[keyof typeof OrderStatus];

/**
 * 订单类型类型
 */
export type OrderType = typeof OrderType[keyof typeof OrderType];

// ==================== 接口定义 ====================

/**
 * 订单实体接口
 */
export interface Order {
  /** ID */
  id?: number;
  /** 订单号 */
  orderNo: string;
  /** 用户ID */
  userId: number;
  /** 订单金额 */
  orderAmount: number;
  /** 订单状态 */
  orderStatus: OrderStatus;
  /** 订单类型 */
  typeId: OrderType;
  /** 创建时间 */
  createTime: number[] | string | undefined;
  /** 服务地址 */
  serviceAddress?: string;
  /** 联系电话 */
  contactPhone?: string;
  /** 需求描述 */
  requirement?: string;
  /** 发布者昵称 */
  publisherNickname?: string;
}

/**
 * 订单分页查询参数接口
 */
export interface OrderPageQueryParams {
  /** 当前页码（从1开始） */
  page: number;
  /** 每页记录数 */
  pageSize: number;
  /** 订单号（模糊搜索） */
  orderNo?: string;
  /** 订单类型ID（0=全部） */
  typeId?: number;
  /** 用户姓名（昵称） */
  userName?: string;
}

/**
 * 订单分页响应接口
 */
export interface OrderPageResponse {
  /** 总记录数 */
  total: number;
  /** 订单记录列表 */
  records: Order[];
}

// ==================== 常量定义 ====================

/**
 * 订单状态文本映射
 */
export const ORDER_STATUS_TEXT: Record<number, string> = {
  [OrderStatus.PENDING_ACCEPT]: '未接单',
  [OrderStatus.ACCEPTED]: '已接单',
  [OrderStatus.COMPLETED]: '完成订单'
};

/**
 * 订单类型文本映射
 */
export const ORDER_TYPE_TEXT: Record<number, string> = {
  [OrderType.OTHER]: '其他',
  [OrderType.SUBSTITUTE]: '替课',
  [OrderType.PRINT]: '帮助打印',
  [OrderType.SHOPPING]: '帮忙购物',
  [OrderType.TAKEOUT]: '帮取外卖',
  [OrderType.EXPRESS]: '帮拿快递'
};
