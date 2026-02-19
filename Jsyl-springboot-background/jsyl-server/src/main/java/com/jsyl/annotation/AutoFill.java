package com.jsyl.annotation;

/*
* 自定义注解,用于表示某个方法需要进行功能字段自填充*/


import com.jsyl.enumeration.OperationType;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型: UPDATE INSERT
     OperationType value();
}
