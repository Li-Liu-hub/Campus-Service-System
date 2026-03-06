
import type { Comment, CommentVO } from '@/types/entities';


export interface CommentPublishParams {
  /** 所属帖子ID */
  postId: number;
  /** 父评论ID（顶级评论填0） */
  parentId?: number;
  /** 评论内容 */
  content: string;
}


export type { Comment, CommentVO };
