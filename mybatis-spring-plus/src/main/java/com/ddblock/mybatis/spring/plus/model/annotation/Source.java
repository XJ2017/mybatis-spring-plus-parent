package com.ddblock.mybatis.spring.plus.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识组合类中存在非表模型时，字段值的来源
 *
 * @author XiaoJia
 * @since 2020/4/8 21:08
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
public @interface Source {
    /**
     * 标识组合类中存在非表模型时，字段值的来源属性
     *
     * @return 字段值的来源属性值
     */
    String value();
}
