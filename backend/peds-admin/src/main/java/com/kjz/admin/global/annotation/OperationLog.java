package com.kjz.admin.global.annotation;


import com.kjz.admin.global.consts.ActionTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统日志
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 描述
     *
     */
    String value();

    /**
     * 操作类型
     *
     */
    ActionTypeEnum type() default ActionTypeEnum.ADD;

}
