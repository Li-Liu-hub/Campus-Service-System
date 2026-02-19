/**
 * 日志工具模块
 * 
 * @module utils/logger
 */

// ==================== 日志级别 ====================

/**
 * 日志级别常量
 */
export const LogLevel = {
  /** 调试级别 */
  DEBUG: 'debug',
  /** 信息级别 */
  INFO: 'info',
  /** 警告级别 */
  WARN: 'warn',
  /** 错误级别 */
  ERROR: 'error'
} as const;

/**
 * 日志级别类型
 */
export type LogLevel = typeof LogLevel[keyof typeof LogLevel];

/**
 * 当前日志级别（可通过环境变量配置）
 */
const CURRENT_LOG_LEVEL: LogLevel = (import.meta.env.VITE_LOG_LEVEL as LogLevel) || LogLevel.INFO;

/**
 * 日志级别优先级
 */
const LOG_LEVEL_PRIORITY: Record<LogLevel, number> = {
  [LogLevel.DEBUG]: 0,
  [LogLevel.INFO]: 1,
  [LogLevel.WARN]: 2,
  [LogLevel.ERROR]: 3
};

// ==================== 日志格式化 ====================

/**
 * 获取时间戳
 * @returns 格式化的时间戳
 */
function getTimestamp(): string {
  const now = new Date();
  return now.toISOString();
}

/**
 * 格式化日志消息
 * @param level 日志级别
 * @param message 日志消息
 * @returns 格式化的日志内容
 */
function formatMessage(level: LogLevel, message: string): string {
  const timestamp = getTimestamp();
  const levelStr = level.toUpperCase().padEnd(5);
  return `[${timestamp}] [${levelStr}] ${message}`;
}

// ==================== 日志输出 ====================

/**
 * 检查是否应该输出该级别的日志
 * @param level 日志级别
 * @returns 是否应该输出
 */
function shouldLog(level: LogLevel): boolean {
  return LOG_LEVEL_PRIORITY[level] >= LOG_LEVEL_PRIORITY[CURRENT_LOG_LEVEL];
}

/**
 * 输出调试日志
 * @param message 日志消息
 * @param args 附加参数
 */
export function logDebug(message: string, ...args: any[]): void {
  if (shouldLog(LogLevel.DEBUG)) {
    console.debug(formatMessage(LogLevel.DEBUG, message), ...args);
  }
}

/**
 * 输出信息日志
 * @param message 日志消息
 * @param args 附加参数
 */
export function logInfo(message: string, ...args: any[]): void {
  if (shouldLog(LogLevel.INFO)) {
    console.info(formatMessage(LogLevel.INFO, message), ...args);
  }
}

/**
 * 输出警告日志
 * @param message 日志消息
 * @param args 附加参数
 */
export function logWarn(message: string, ...args: any[]): void {
  if (shouldLog(LogLevel.WARN)) {
    console.warn(formatMessage(LogLevel.WARN, message), ...args);
  }
}

/**
 * 输出错误日志
 * @param message 日志消息
 * @param args 附加参数
 */
export function logError(message: string, ...args: any[]): void {
  if (shouldLog(LogLevel.ERROR)) {
    console.error(formatMessage(LogLevel.ERROR, message), ...args);
  }
}

// ==================== API请求日志 ====================

/**
 * 记录API请求开始
 * @param url 请求URL
 * @param method 请求方法
 * @param data 请求数据
 */
export function logApiRequest(url: string, method: string, data?: any): void {
  logDebug(`API Request: ${method} ${url}`, data);
}

/**
 * 记录API响应成功
 * @param url 请求URL
 * @param method 请求方法
 * @param response 响应数据
 */
export function logApiSuccess(url: string, method: string, response?: any): void {
  logDebug(`API Success: ${method} ${url}`, response);
}

/**
 * 记录API响应错误
 * @param url 请求URL
 * @param method 请求方法
 * @param error 错误信息
 */
export function logApiError(url: string, method: string, error?: any): void {
  logError(`API Error: ${method} ${url}`, error);
}

// ==================== 性能日志 ====================

/**
 * 性能标记映射
 */
const performanceMarks = new Map<string, number>();

/**
 * 开始性能计时
 * @param markName 标记名称
 */
export function startPerformance(markName: string): void {
  performanceMarks.set(markName, Date.now());
  logDebug(`Performance Start: ${markName}`);
}

/**
 * 结束性能计时
 * @param markName 标记名称
 * @returns 耗时（毫秒）
 */
export function endPerformance(markName: string): number {
  const startTime = performanceMarks.get(markName);
  if (startTime === undefined) {
    logWarn(`Performance mark not found: ${markName}`);
    return 0;
  }

  const duration = Date.now() - startTime;
  performanceMarks.delete(markName);
  logInfo(`Performance: ${markName} took ${duration}ms`);
  return duration;
}

// ==================== 导出日志对象 ====================

export const logger = {
  debug: logDebug,
  info: logInfo,
  warn: logWarn,
  error: logError,
  api: {
    request: logApiRequest,
    success: logApiSuccess,
    error: logApiError
  },
  performance: {
    start: startPerformance,
    end: endPerformance
  }
};
