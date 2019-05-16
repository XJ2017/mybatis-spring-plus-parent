package com.ddblock.mybatis.spring.plus.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 复合表标识
 *
 * @author XiaoJia
 * @since 2019-05-15 12:37
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface ComplexTable {}
