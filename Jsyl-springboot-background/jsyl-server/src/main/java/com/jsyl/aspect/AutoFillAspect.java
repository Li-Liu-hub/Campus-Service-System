package com.jsyl.aspect;

import com.jsyl.annotation.AutoFill;
import com.jsyl.constant.AutoFillConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.enumeration.OperationType;
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

    /*
     * 切入点：匹配mapper中带有AutoFill注解的方法
     * */
    @Pointcut("execution(* com.jsyl.mapper.*.*(..)) && @annotation(com.jsyl.annotation.AutoFill)")
    public void autoFillPointCut(){}


    /*
     * 前置通知：在通知中进行公共字段的赋值
     * */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        // 1. 获取方法签名（强转为MethodSignature，因为切入点匹配的是方法）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 2. 获取方法上的AutoFill注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 3. 后续可通过注解获取操作类型（如INSERT/UPDATE），再对实体字段赋值...
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity = args[0];

//        准备赋值
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


        // （补充：还需获取方法参数，找到要赋值的实体对象）
    }
}