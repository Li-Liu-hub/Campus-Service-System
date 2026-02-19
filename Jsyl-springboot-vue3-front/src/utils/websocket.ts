import { getToken } from '@/utils/storage';

export interface WebSocketMessage {
  type: number;
  senderId: number;
  senderNickname: string;
  receiverId: number;
  content: string;
  messageId: number;
  conversationId: number;
  createTime: string | Date;
}

class WebSocketManager {
  private ws: WebSocket | null = null;
  private connected: boolean = false;
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 5;
  private messageHandlers: ((message: WebSocketMessage) => void)[] = [];
  private heartbeatTimer: number | null = null;
  private serverUrl: string = '';

  connect() {
    if (this.connected && this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接');
      return;
    }

    const token = getToken();
    if (!token) {
      console.warn('未登录，无法连接WebSocket');
      return;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const backendHost = 'localhost:8080';
    const encodedToken = encodeURIComponent(token);
    this.serverUrl = protocol + '//' + backendHost + '/jsyl/ws?token=' + encodedToken;

    try {
      console.log('正在连接WebSocket:', this.serverUrl);
      console.log('Token长度:', token.length);
      console.log('编码后Token:', encodedToken);
      this.ws = new WebSocket(this.serverUrl);
      
      this.ws.onopen = () => {
        console.log('WebSocket连接成功');
        this.connected = true;
        this.reconnectAttempts = 0;
        this.startHeartbeat();
      };

      this.ws.onmessage = (event) => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data);
          console.log('收到消息:', message);
          this.messageHandlers.forEach((handler) => handler(message));
        } catch (error) {
          console.error('解析消息失败:', error);
        }
      };

      this.ws.onclose = (event) => {
        console.log('WebSocket连接关闭', event);
        this.connected = false;
        this.stopHeartbeat();
        this.handleReconnect();
      };

      this.ws.onerror = (error) => {
        console.error('WebSocket连接错误', error);
        this.connected = false;
      };
    } catch (error) {
      console.error('创建WebSocket连接失败', error);
    }
  }

  private startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatTimer = window.setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 3 }));
      }
    }, 30000);
  }

  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  sendMessage(receiverId: number, content: string) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.error('WebSocket未连接');
      return false;
    }

    const message = {
      type: 1,
      receiverId,
      content,
    };

    try {
      this.ws.send(JSON.stringify(message));
      return true;
    } catch (error) {
      console.error('发送消息失败:', error);
      return false;
    }
  }

  onMessage(handler: (message: WebSocketMessage) => void) {
    this.messageHandlers.push(handler);
    return () => {
      const index = this.messageHandlers.indexOf(handler);
      if (index > -1) {
        this.messageHandlers.splice(index, 1);
      }
    };
  }

  private handleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('WebSocket重连次数已达上限');
      return;
    }

    this.reconnectAttempts++;
    const delay = Math.pow(2, this.reconnectAttempts) * 1000;
    console.log('WebSocket将在' + (delay / 1000) + '秒后重连...');

    setTimeout(() => {
      this.connect();
    }, delay);
  }

  disconnect() {
    this.stopHeartbeat();
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    this.connected = false;
    this.messageHandlers = [];
    console.log('WebSocket已断开');
  }

  isConnected() {
    return this.connected && this.ws && this.ws.readyState === WebSocket.OPEN;
  }
}

export const webSocketManager = new WebSocketManager();
