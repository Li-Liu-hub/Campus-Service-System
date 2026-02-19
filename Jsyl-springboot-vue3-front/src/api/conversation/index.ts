import { get, post, put } from '@/utils/request';
import type { Conversation, PrivateMessage } from '@/types/entities';
import {
  API_CONVERSATION_LIST,
  API_CONVERSATION_MESSAGES,
  API_CONVERSATION_SEND,
  API_CONVERSATION_READ,
  API_CONVERSATION_UNREAD_COUNT
} from '@/constants/api';

export interface SendMessageParams {
  targetUserId: number;
  content: string;
}

export const getConversationList = () => {
  return get<Conversation[]>(API_CONVERSATION_LIST);
};

export const getMessages = (targetUserId: number) => {
  return get<PrivateMessage[]>(`${API_CONVERSATION_MESSAGES}/${targetUserId}`);
};

export const sendMessage = (params: SendMessageParams) => {
  return post<PrivateMessage>(API_CONVERSATION_SEND, params);
};

export const markAsRead = (targetUserId: number) => {
  return put(`${API_CONVERSATION_READ}/${targetUserId}`);
};

export const getUnreadCount = () => {
  return get<number>(API_CONVERSATION_UNREAD_COUNT);
};
