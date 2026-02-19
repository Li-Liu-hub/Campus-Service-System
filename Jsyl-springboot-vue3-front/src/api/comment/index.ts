/**
 * 评论模块 API
 */

import { get, post, del } from '@/utils/request';
import {
  API_COMMENT_PUBLISH,
  API_COMMENT_LIST,
  API_COMMENT_DELETE
} from '@/constants/api';
import type { CommentInfo } from '@/types/entities';

export interface CommentPublishParams {
  postId: number;
  parentId?: number;
  content: string;
}

export interface CommentListParams {
  postId: number;
  page?: number;
  pageSize?: number;
}

export interface CommentListResponse {
  records: CommentInfo[];
  total: number;
}

export function publishComment(data: CommentPublishParams) {
  return post<string>(API_COMMENT_PUBLISH, data);
}

export function getCommentList(params: CommentListParams) {
  return get<CommentListResponse>(`${API_COMMENT_LIST}/${params.postId}`);
}

export function deleteComment(id: number) {
  return del<string>(`${API_COMMENT_DELETE}/${id}`);
}
