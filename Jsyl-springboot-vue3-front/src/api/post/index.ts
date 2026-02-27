/**
 * 帖子模块API
 *
 * @module api/post
 */

import { get, post, put, del } from '@/utils/request';
import {
  API_POST_PUBLISH,
  API_POST_DETAIL,
  API_POST_UPDATE,
  API_POST_DELETE,
  API_POST_PAGE,
  API_POST_MY_POSTS
} from '@/constants/api';
import type {
  PostPublishParams,
  PostPageQueryParams,
  PostPageResponse,
  PostDetailVO,
  Post
} from './types';

/**
 * 发布帖子
 * @param params 发布参数
 */
export function publishPost(params: PostPublishParams) {
  return post<string>(API_POST_PUBLISH, params);
}

/**
 * 获取帖子详情
 * @param id 帖子ID
 */
export function getPostDetail(id: number) {
  return get<PostDetailVO>(`${API_POST_DETAIL}/${id}`);
}

/**
 * 更新帖子
 * @param id 帖子ID
 * @param params 更新参数
 */
export function updatePost(id: number, params: PostPublishParams) {
  return put<string>(`${API_POST_UPDATE}/${id}`, params);
}

/**
 * 删除帖子
 * @param id 帖子ID
 */
export function deletePost(id: number) {
  return del<string>(`${API_POST_DELETE}/${id}`);
}

/**
 * 分页查询帖子
 * @param params 查询参数
 */
export function getPostPage(params: PostPageQueryParams) {
  return get<PostPageResponse>(API_POST_PAGE, params);
}

export function getMyPosts() {
  return get<Post[]>(API_POST_MY_POSTS);
}

export type { PostPublishParams, PostPageQueryParams, PostPageResponse, PostDetailVO, Post };
