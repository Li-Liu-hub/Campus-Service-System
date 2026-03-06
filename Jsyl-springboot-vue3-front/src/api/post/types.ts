
import type { Post, PostDetailVO } from '@/types/entities';


export interface PostPublishParams {
  /** 帖子标题 */
  title: string;
  /** 帖子内容 */
  content: string;
}

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


export type { Post, PostDetailVO };
