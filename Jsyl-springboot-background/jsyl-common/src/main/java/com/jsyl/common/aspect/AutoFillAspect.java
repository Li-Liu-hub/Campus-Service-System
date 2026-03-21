package com.jsyl.common.aspect;

import com.jsyl.common.annotation.AutoFill;
import com.jsyl.common.constant.AutoFillConstant;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.jsyl.module..mapper.*.*(..)) && @annotation(com.jsyl.common.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity = args[0];

        LocalDateTime now= LocalDateTime.now();
        Long currenId = BaseContext.getCurrentId();

        if(operationType == OperationType.INSERT){
            try {
            Method setCreateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            Method setCreateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
            Method setUpdateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

            setUpdateTime.invoke(entity,now);
            setCreateUser.invoke(entity,currenId);
            setUpdateUser.invoke(entity,currenId);
            setCreateTime.invoke(entity,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currenId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
