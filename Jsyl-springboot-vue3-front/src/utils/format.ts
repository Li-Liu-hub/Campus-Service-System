/**
 * 格式化工具模块
 * 
 * @module utils/format
 */

// ==================== 时间格式化 ====================

/**
 * 格式化日期时间
 * @param date 日期对象或时间戳
 * @param format 格式字符串
 * @returns 格式化后的日期字符串
 */
export function formatDateTime(
  date: Date | string | number | null | undefined,
  format: string = 'YYYY-MM-DD HH:mm:ss'
): string {
  if (!date) {
    return '-';
  }

  let dateObj: Date;
  if (typeof date === 'string') {
    dateObj = new Date(date);
  } else if (typeof date === 'number') {
    dateObj = new Date(date);
  } else {
    dateObj = date;
  }

  if (isNaN(dateObj.getTime())) {
    return '-';
  }

  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const seconds = String(dateObj.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 格式化日期
 * @param date 日期对象或时间戳
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: Date | string | number | null | undefined): string {
  return formatDateTime(date, 'YYYY-MM-DD');
}

/**
 * 格式化时间
 * @param date 日期对象或时间戳
 * @returns 格式化后的时间字符串
 */
export function formatTime(date: Date | string | number | null | undefined): string {
  return formatDateTime(date, 'HH:mm:ss');
}

/**
 * 格式化相对时间
 * @param date 日期对象或时间戳
 * @returns 相对时间字符串
 */
export function formatRelativeTime(date: Date | string | number | null | undefined): string {
  if (!date) {
    return '-';
  }

  let dateObj: Date;
  if (typeof date === 'string') {
    dateObj = new Date(date);
  } else if (typeof date === 'number') {
    dateObj = new Date(date);
  } else {
    dateObj = date;
  }

  if (isNaN(dateObj.getTime())) {
    return '-';
  }

  const now = new Date();
  const diff = now.getTime() - dateObj.getTime();
  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (seconds < 60) {
    return '刚刚';
  } else if (minutes < 60) {
    return `${minutes}分钟前`;
  } else if (hours < 24) {
    return `${hours}小时前`;
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return formatDate(dateObj);
  }
}

// ==================== 数字格式化 ====================

/**
 * 格式化数字
 * @param num 数字
 * @param decimals 小数位数
 * @returns 格式化后的数字字符串
 */
export function formatNumber(num: number | string | null | undefined, decimals: number = 0): string {
  if (num === null || num === undefined || num === '') {
    return '-';
  }

  const n = typeof num === 'string' ? parseFloat(num) : num;
  if (isNaN(n)) {
    return '-';
  }

  return n.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  });
}

/**
 * 格式化金额
 * @param amount 金额
 * @param decimals 小数位数
 * @returns 格式化后的金额字符串
 */
export function formatAmount(amount: number | string | null | undefined, decimals: number = 2): string {
  const formatted = formatNumber(amount, decimals);
  return formatted === '-' ? '-' : `¥${formatted}`;
}

/**
 * 格式化百分比
 * @param value 数值
 * @param decimals 小数位数
 * @returns 格式化后的百分比字符串
 */
export function formatPercent(value: number | string | null | undefined, decimals: number = 2): string {
  if (value === null || value === undefined || value === '') {
    return '-';
  }

  const n = typeof value === 'string' ? parseFloat(value) : value;
  if (isNaN(n)) {
    return '-';
  }

  return `${(n * 100).toFixed(decimals)}%`;
}

// ==================== 字符串格式化 ====================

/**
 * 首字母大写
 * @param str 字符串
 * @returns 首字母大写的字符串
 */
export function capitalize(str: string | null | undefined): string {
  if (!str) {
    return '';
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * 驼峰命名转短横线命名
 * @param str 驼峰命名字符串
 * @returns 短横线命名字符串
 */
export function camelToKebab(str: string): string {
  return str.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
}

/**
 * 短横线命名转驼峰命名
 * @param str 短横线命名字符串
 * @returns 驼峰命名字符串
 */
export function kebabToCamel(str: string): string {
  return str.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
}

/**
 * 截断字符串
 * @param str 字符串
 * @param maxLength 最大长度
 * @param suffix 后缀
 * @returns 截断后的字符串
 */
export function truncate(str: string | null | undefined, maxLength: number = 50, suffix: string = '...'): string {
  if (!str) {
    return '';
  }
  if (str.length <= maxLength) {
    return str;
  }
  return str.substring(0, maxLength) + suffix;
}

// ==================== 其他格式化 ====================

/**
 * 格式化手机号（中间4位用*代替）
 * @param phone 手机号
 * @returns 格式化后的手机号
 */
export function formatPhoneMask(phone: string | null | undefined): string {
  if (!phone) {
    return '-';
  }
  if (phone.length !== 11) {
    return phone;
  }
  return `${phone.substring(0, 3)}****${phone.substring(7)}`;
}

/**
 * 格式化邮箱（用户名部分隐藏）
 * @param email 邮箱
 * @returns 格式化后的邮箱
 */
export function formatEmailMask(email: string | null | undefined): string {
  if (!email) {
    return '-';
  }
  const atIndex = email.indexOf('@');
  if (atIndex <= 1) {
    return email;
  }
  const username = email.substring(0, atIndex);
  const domain = email.substring(atIndex);
  return `${username.substring(0, 2)}***${domain}`;
}

/**
 * 格式化文件大小
 * @param bytes 字节数
 * @returns 格式化后的文件大小
 */
export function formatFileSize(bytes: number | null | undefined): string {
  if (bytes === null || bytes === undefined) {
    return '-';
  }

  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = bytes;
  let unitIndex = 0;

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }

  return `${size.toFixed(unitIndex === 0 ? 0 : 2)} ${units[unitIndex]}`;
}
