
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

export function publishPost(params: PostPublishParams) {
  return post<string>(API_POST_PUBLISH, params);
}

export function getPostDetail(id: number) {
  return get<PostDetailVO>(`${API_POST_DETAIL}/${id}`);
}

export function updatePost(id: number, params: PostPublishParams) {
  return put<string>(`${API_POST_UPDATE}/${id}`, params);
}

export function deletePost(id: number) {
  return del<string>(`${API_POST_DELETE}/${id}`);
}

export function getPostPage(params: PostPageQueryParams) {
  return get<PostPageResponse>(API_POST_PAGE, params);
}

export function getMyPosts() {
  return get<Post[]>(API_POST_MY_POSTS);
}

export type { PostPublishParams, PostPageQueryParams, PostPageResponse, PostDetailVO, Post };
