package com.ddblock.mybatis.spring.plus.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识一个表结构中主键
 *
 * Author XiaoJia
 * Date 2019-03-07 12:31
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Id {
}
