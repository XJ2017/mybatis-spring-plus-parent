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
public @interface PrimaryIndex {
    /**
     * 设置DB表中的主键，默认使用类名（将大写字母转换为”_+字母“）
     *
     * @return DB表中的主键
     */
    String value() default "";
}
