package com.ddblock.mybatis.spring.boot.generator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 将DB中字段类型转换为Java中的字段类型
 *
 * @author XiaoJia2
 * @since 2019-03-16 16:49
 */
public class JavaTypeResolverDefaultImplEx extends JavaTypeResolverDefaultImpl {

    public JavaTypeResolverDefaultImplEx() {
        super();

        // 默认是将 TINYINT 转换为 Byte，此处将其转换为 Integer
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }

}
