
import { get } from '@/utils/request';
import type { ApiResponse } from '@/types';
import { API_CAMPUS_LIST } from '@/constants/api';

export interface CampusInfo {
  id: number;
  campusName: string;
}

export function getCampusList(): Promise<ApiResponse<CampusInfo[]>> {
  return get<CampusInfo[]>(API_CAMPUS_LIST)
}
