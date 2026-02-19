/**
 * 帖子模块类型定义
 *
 * @module api/post/types
 */

import type { Post, PostDetailVO } from '@/types/entities';

// ==================== 接口定义 ====================

/**
 * 帖子发布参数接口
 */
export interface PostPublishParams {
  /** 帖子标题 */
  title: string;
  /** 帖子内容 */
  content: string;
}

/**
 * 帖子分页查询参数接口
 */
export interface PostPageQueryParams {
  page: number;
  pageSize: number;
  /** 帖子标题关键词搜索 */
  keyword?: string;
  /** 发布者用户ID */
  userId?: number;
}

export interface PostPageResponse {
  records: Post[];
  total: number;
}

// ==================== 导出 ====================

export type { Post, PostDetailVO };
