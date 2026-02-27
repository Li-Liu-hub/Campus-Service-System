export const RoleEnum = {
  RESTRICTED: 0,
  USER: 1,
  VIP: 2,
  ADMIN: 3,
  SUPER_ADMIN: 4,
} as const;

export type RoleType = typeof RoleEnum[keyof typeof RoleEnum];

export const RoleText: Record<number, string> = {
  0: "被限制用户",
  1: "普通用户",
  2: "VIP用户",
  3: "管理员",
  4: "超级管理员",
};

export const RoleOptions = [
  { value: 0, label: "被限制用户" },
  { value: 1, label: "普通用户" },
  { value: 2, label: "VIP用户" },
  { value: 3, label: "管理员" },
  { value: 4, label: "超级管理员" },
];

export function hasPermission(currentRole: number, requiredRole: number): boolean {
  return currentRole >= requiredRole;
}

export function isAdmin(currentRole: number): boolean {
  return currentRole >= RoleEnum.ADMIN;
}

export function isSuperAdmin(currentRole: number): boolean {
  return currentRole === RoleEnum.SUPER_ADMIN;
}

export function canEditUser(currentRole: number): boolean {
  return currentRole >= RoleEnum.ADMIN;
}

export function canDeletePost(currentRole: number): boolean {
  return currentRole >= RoleEnum.ADMIN;
}

export function canManageOrder(currentRole: number): boolean {
  return currentRole >= RoleEnum.ADMIN;
}

export function canViewAllData(currentRole: number): boolean {
  return currentRole >= RoleEnum.ADMIN;
}
