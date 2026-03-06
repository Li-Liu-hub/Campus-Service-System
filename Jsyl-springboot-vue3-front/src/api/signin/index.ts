import { post, get } from '@/utils/request';
import type { ApiResponse } from '@/types';

export interface SignInVO {
  signedToday: boolean;
  continuousDays: number;
  totalDays: number;
  lastSignDate: string;
}

export function signIn(): Promise<ApiResponse<SignInVO>> {
  return post<SignInVO>('/jsyl/home/signin');
}

export function getSignInStatus(): Promise<ApiResponse<SignInVO>> {
  return get<SignInVO>('/jsyl/home/signin/status');
}

export function getMonthSignInDates(year?: number, month?: number): Promise<ApiResponse<string[]>> {
  return get<string[]>('/jsyl/home/signin/calendar', { year, month });
}
