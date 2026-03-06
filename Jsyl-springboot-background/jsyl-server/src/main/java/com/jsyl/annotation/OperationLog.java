package com.jsyl.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    String type() default "";

    String value() default "";

    boolean recordParams() default true;

    boolean recordResult() default true;
}
