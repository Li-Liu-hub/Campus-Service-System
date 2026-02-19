/**
 * 评论模块类型定义
 *
 * @module api/comment/types
 */

import type { Comment, CommentVO } from '@/types/entities';

// ==================== 接口定义 ====================

/**
 * 评论发布参数接口
 */
export interface CommentPublishParams {
  /** 所属帖子ID */
  postId: number;
  /** 父评论ID（顶级评论填0） */
  parentId?: number;
  /** 评论内容 */
  content: string;
}

// ==================== 导出 ====================

export type { Comment, CommentVO };
