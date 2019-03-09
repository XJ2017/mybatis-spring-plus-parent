package com.ddblock.mybatis.spring.plus.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识表中的字段名
 *
 * Author XiaoJia
 * Date 2019-03-04 17:40
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Field {

    /**
     * 设置DB中的字段名，默认使用类名（将大写字母转换为”_+字母“）
     *
     * @return DB中的表名
     */
    String value() default "";

}
