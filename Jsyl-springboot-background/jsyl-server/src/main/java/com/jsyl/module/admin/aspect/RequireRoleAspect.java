package com.jsyl.module.admin.aspect;


import com.jsyl.common.annotation.RequireRole;
import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.enumeration.RoleEnum;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.exception.PermissionDeniedException;
import com.jsyl.common.exception.UserNotLoginException;
import com.jsyl.common.exception.UserNotFoundException;
import com.jsyl.model.user.entity.User;
import com.jsyl.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


    @Aspect
    @Component
    @Slf4j
    public class RequireRoleAspect {
        @Autowired
        private UserMapper userMapper;

        @Pointcut("@annotation(com.jsyl.common.annotation.RequireRole)")
        public void requireRolePointcut(){}

        @Before("requireRolePointcut()")
        public void checkRole(JoinPoint joinPoint) {
            // 1. 拿当前登录用户（不是方法参数）
            Long currentUserId = BaseContext.getCurrentId();
            if (currentUserId == null) {
                throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
            }

            // 2. 查用户
            User user = userMapper.getById(currentUserId.intValue());
            if (user == null) {
                throw new UserNotFoundException(MessageConstant.USER_NOT_FOUND);
            }

            // 3. 从注解上读取要求的角色
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            RequireRole annotation = signature.getMethod().getAnnotation(RequireRole.class);
            RoleEnum requiredRole = annotation.value();

            // 4. 比较权限
            RoleEnum currentRole = RoleEnum.fromCode(user.getRole());
            if (!currentRole.hasPermission(requiredRole)) {
                throw new PermissionDeniedException(MessageConstant.PERMISSION_DENIED);
            }
        }
    }
