

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

export function formatDate(date: Date | string | number | null | undefined): string {
  return formatDateTime(date, 'YYYY-MM-DD');
}

export function formatTime(date: Date | string | number | null | undefined): string {
  return formatDateTime(date, 'HH:mm:ss');
}

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

export function formatAmount(amount: number | string | null | undefined, decimals: number = 2): string {
  const formatted = formatNumber(amount, decimals);
  return formatted === '-' ? '-' : `¥${formatted}`;
}

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


export function capitalize(str: string | null | undefined): string {
  if (!str) {
    return '';
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export function camelToKebab(str: string): string {
  return str.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
}

export function kebabToCamel(str: string): string {
  return str.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
}

export function truncate(str: string | null | undefined, maxLength: number = 50, suffix: string = '...'): string {
  if (!str) {
    return '';
  }
  if (str.length <= maxLength) {
    return str;
  }
  return str.substring(0, maxLength) + suffix;
}


export function formatPhoneMask(phone: string | null | undefined): string {
  if (!phone) {
    return '-';
  }
  if (phone.length !== 11) {
    return phone;
  }
  return `${phone.substring(0, 3)}****${phone.substring(7)}`;
}

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
