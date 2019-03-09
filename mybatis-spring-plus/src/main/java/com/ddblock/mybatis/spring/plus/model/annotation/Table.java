package com.ddblock.mybatis.spring.plus.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识DB中的表信息
 *
 * Author XiaoJia
 * Date 2019-03-04 17:25
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Table {

    /**
     * 设置DB中的表名，默认使用类名（将首字母小写，并把之后大写字母转换为”_+字母“）
     *
     * @return DB中的表名
     */
    String value() default "";

}
