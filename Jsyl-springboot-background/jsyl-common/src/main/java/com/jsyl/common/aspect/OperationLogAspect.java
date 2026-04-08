package com.jsyl.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsyl.common.annotation.OperationLog;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 敏感字段列表
    private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
            "password", "pwd", "token", "secret", "key"
    );

    // 定义切点：拦截admin包下的所有方法
    @Pointcut("execution(* com.jsyl.controller.admin..*.*(..))")
    public void adminOperation() {}

    // 定义切点：拦截带有@OperationLog注解的方法
    @Pointcut("@annotation(com.jsyl.common.annotation.OperationLog)")
    public void annotationOperation() {}

    @Around("adminOperation() || annotationOperation()")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String url = request.getRequestURI();

        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = signature.getMethod();
        OperationLog annotation = targetMethod.getAnnotation(OperationLog.class);

        // 只记录写操作或带有注解的方法
        if (!"GET".equals(method) || annotation != null) {
            Long userId = BaseContext.getCurrentId();
            String operatorName = "未知";
            Integer operatorId = 0;

            // 获取操作类型和描述
            String operationType = annotation != null && !annotation.type().isEmpty()
                    ? annotation.type() : getOperationType(url);
            String operation = annotation != null && !annotation.value().isEmpty()
                    ? annotation.value() : getOperationName(joinPoint.getSignature().getName(), method);

            Object result = null;
            String requestParams = "";
            String responseResult = "";
            String status = "success";
            String errorMsg = null;

            try {
                // 获取请求参数
                if (annotation == null || annotation.recordParams()) {
                    Object[] args = joinPoint.getArgs();
                    if (args != null && args.length > 0) {
                        try {
                            requestParams = filterSensitiveData(objectMapper.writeValueAsString(args));
                            // 限制参数长度
                            if (requestParams.length() > 2000) {
                                requestParams = requestParams.substring(0, 2000) + "...";
                            }
                        } catch (Exception e) {
                            requestParams = "参数序列化失败";
                        }
                    }
                }

                // 执行方法
                result = joinPoint.proceed();

                // 获取响应结果
                if (annotation == null || annotation.recordResult()) {
                    try {
                        if (result != null) {
                            responseResult = objectMapper.writeValueAsString(result);
                            // 限制响应长度
                            if (responseResult.length() > 2000) {
                                responseResult = responseResult.substring(0, 2000) + "...";
                            }
                        }
                    } catch (Exception e) {
                        responseResult = "结果序列化失败";
                    }
                }

            } catch (Exception e) {
                status = "fail";
                errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.length() > 500) {
                    errorMsg = errorMsg.substring(0, 500) + "...";
                }
                log.error("操作执行失败: {}", e.getMessage());
                throw e;
            } finally {
                long executionTime = System.currentTimeMillis() - startTime;

                // 记录日志 - 这里需要通过Result返回状态，实际业务中应该通过事件或服务调用来记录
                log.info("操作日志: 耗时{}ms, 状态{}, URL{}, 操作{}", executionTime, status, url, operation);
            }

            return result;
        }

        return joinPoint.proceed();
    }

    private String filterSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        String result = data;
        for (String field : SENSITIVE_FIELDS) {
            result = result.replaceAll("\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                    "\"" + field + "\":\"******\"");
        }
        return result;
    }

    private String getOperationType(String url) {
        if (url.contains("/user/")) {
            return "user";
        } else if (url.contains("/order") || url.contains("/Order")) {
            return "order";
        } else if (url.contains("/post") || url.contains("/Post")) {
            return "post";
        } else if (url.contains("/system") || url.contains("/logs")) {
            return "system";
        }
        return "other";
    }

    private String getOperationName(String methodName, String httpMethod) {
        switch (methodName.toLowerCase()) {
            case "update":
            case "updateuser":
            case "updatestatus":
                return "修改";
            case "delete":
            case "deleteuser":
            case "deletepost":
                return "删除";
            case "add":
            case "create":
            case "insert":
            case "adduser":
                return "新增";
            case "status":
                return "修改状态";
            case "role":
            case "updaterole":
                return "修改角色";
            case "clear":
            case "clearlogs":
                return "清空";
            case "batchdelete":
                return "批量删除";
            case "export":
                return "导出";
            case "import":
                return "导入";
            default:
                return methodName;
        }
    }
}
