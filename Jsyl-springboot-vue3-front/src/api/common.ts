/**
 * 通用模块API
 * 
 * @module api/common
 */

import { get } from '@/utils/request';
import type { ApiResponse } from '@/types';
import { API_CAMPUS_LIST } from '@/constants/api';
import { logger } from '@/utils/logger';

export interface CampusInfo {
  id: number;
  campusName: string;
}

export function getCampusList(): Promise<ApiResponse<CampusInfo[]>> {
  logger.api.request(API_CAMPUS_LIST, 'GET');
  return get<CampusInfo[]>(API_CAMPUS_LIST)
    .then(response => {
      logger.api.success(API_CAMPUS_LIST, 'GET', response);
      return response;
    })
    .catch(error => {
      logger.api.error(API_CAMPUS_LIST, 'GET', error);
      throw error;
    });
}
