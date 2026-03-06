package com.jsyl.constant;

public enum RoleEnum {

    RESTRICTED(0, "被限制权限的用户"),
    USER(1, "普通用户"),
    VIP(2, "VIP用户"),
    ADMIN(3, "管理员"),
    SUPER_ADMIN(4, "超级管理员");

    private final int code;
    private final String description;

    RoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RoleEnum fromCode(int code) {
        for (RoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return USER;
    }

    public boolean hasPermission(RoleEnum requiredRole) {
        return this.code >= requiredRole.code;
    }

    public boolean isAdmin() {
        return this.code >= ADMIN.code;
    }

    public boolean isSuperAdmin() {
        return this.code == SUPER_ADMIN.code;
    }

    public boolean canEditUser() {
        return this.code >= ADMIN.code;
    }

    public boolean canDeletePost() {
        return this.code >= ADMIN.code;
    }

    public boolean canManageOrder() {
        return this.code >= ADMIN.code;
    }

    public boolean canViewAllData() {
        return this.code >= ADMIN.code;
    }
}
