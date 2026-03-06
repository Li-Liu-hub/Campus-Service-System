
import { post } from '@/utils/request';

export interface UploadResponse {
  url: string;
  fileName: string;
}

export interface BatchUploadResponse {
  urls: string[];
  fileNames: string[];
  count: number;
}

export function uploadAvatar(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return post<UploadResponse>('/jsyl/common/upload/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function uploadPostImage(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return post<UploadResponse>('/jsyl/common/upload/post/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function uploadPostImages(files: File[]) {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append('files', file);
  });
  return post<BatchUploadResponse>('/jsyl/common/upload/post/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function uploadImage(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return post<UploadResponse>('/jsyl/common/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}
