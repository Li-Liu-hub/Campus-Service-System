package com.jsyl.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123456";
        String encoded = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("加密后: " + encoded);
        
        System.out.println("\n生成超级管理员SQL:");
        System.out.println("INSERT INTO `user_info` (`account`, `nickname`, `password`, `phone`, `address`, `role`) VALUES ('superadmin', '超级管理员', '" + encoded + "', '13900000000', '系统管理', 4);");
    }
}
