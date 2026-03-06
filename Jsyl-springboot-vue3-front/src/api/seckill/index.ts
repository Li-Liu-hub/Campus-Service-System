import { get, post } from '@/utils/request';
import type { ApiResponse } from '@/types';

export interface SeckillGoods {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  originalPrice: number;
  seckillPrice: number;
  stock: number;
  soldCount: number;
  startTime: string;
  endTime: string;
  status: number;
  limitPerUser: number;
  userPurchasedCount: number;
  remainingSeconds: number;
}

export interface SeckillOrder {
  id: number;
  orderNo: string;
  userId: number;
  goodsId: number;
  goodsTitle: string;
  goodsImage: string;
  quantity: number;
  price: number;
  totalAmount: number;
  status: number;
  createTime: string;
  payTime?: string;
}

export function getSeckillGoodsList(): Promise<ApiResponse<SeckillGoods[]>> {
  return get<SeckillGoods[]>('/jsyl/home/seckill/goods');
}

export function getSeckillGoodsDetail(id: number): Promise<ApiResponse<SeckillGoods>> {
  return get<SeckillGoods>(`/jsyl/home/seckill/goods/${id}`);
}

export function seckillBuy(goodsId: number, quantity: number = 1): Promise<ApiResponse<{ orderNo: string; message: string }>> {
  return post<{ orderNo: string; message: string }>('/jsyl/home/seckill/buy', { goodsId, quantity });
}

export function getMySeckillOrders(): Promise<ApiResponse<SeckillOrder[]>> {
  return get<SeckillOrder[]>('/jsyl/home/seckill/orders');
}

// ============ 管理端秒杀商品管理API ============

export interface SeckillGoodsQuery {
  page: number;
  pageSize: number;
  title?: string;
  status?: number;
}

export function getAdminSeckillGoodsList(params: SeckillGoodsQuery): Promise<ApiResponse<any>> {
  return get<any>('/jsyl/admin/seckill/goods', { params });
}

export function getAdminSeckillGoodsDetail(id: number): Promise<ApiResponse<SeckillGoods>> {
  return get<SeckillGoods>(`/jsyl/admin/seckill/goods/${id}`);
}

export function addSeckillGoods(data: Partial<SeckillGoods>): Promise<ApiResponse<string>> {
  return post<string>('/jsyl/admin/seckill/goods', data);
}

export function updateSeckillGoods(id: number, data: Partial<SeckillGoods>): Promise<ApiResponse<string>> {
  return fetch(`/api/jsyl/admin/seckill/goods/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  }).then(res => res.json());
}

export function deleteSeckillGoods(id: number): Promise<ApiResponse<string>> {
  return fetch(`/api/jsyl/admin/seckill/goods/${id}`, {
    method: 'DELETE'
  }).then(res => res.json());
}

export function updateSeckillGoodsStatus(id: number, status: number): Promise<ApiResponse<string>> {
  return fetch(`/api/jsyl/admin/seckill/goods/${id}/status?status=${status}`, {
    method: 'PUT'
  }).then(res => res.json());
}

export function getSeckillOverview(): Promise<ApiResponse<any>> {
  return get<any>('/jsyl/admin/seckill/overview');
}
