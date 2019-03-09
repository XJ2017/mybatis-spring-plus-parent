package com.ddblock.mybatis.spring.plus.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.*;

/**
 * 将不同类型的数据都包装成为Map，由通用处理器解析成为具体对象
 *
 * Author XiaoJia
 * Date 2019-03-06 17:59
 */
@MappedTypes(CommonMapperResultType.class)
public class CommonMapperTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map parameter, JdbcType jdbcType) throws SQLException {
        // 空实现
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        return getResults(rsmd, rs);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        return getResults(rsmd, rs);
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        ResultSetMetaData rsmd = cs.getMetaData();
        return getResults(rsmd, cs);
    }

    /**
     * 解析结果集中的数据到Map中
     *
     * @param rsmd 结果集对象的元数据
     * @param obj 结果集或CallableStatement
     *
     * @return 转换为Map存储的结果集
     *
     * @throws SQLException 异常
     */
    private Map<String, Object> getResults(ResultSetMetaData rsmd, Object obj) throws SQLException {
        Map<String, Object> row = new HashMap<String, Object>();

        for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
            TypeHandler<?> typeHandler;

            String name = rsmd.getColumnLabel(i + 1);
            try {
                Class<?> type = Resources.classForName(rsmd.getColumnClassName(i + 1));
                typeHandler = typeHandlerRegistry.getTypeHandler(type);
                if (typeHandler == null) {
                    typeHandler = typeHandlerRegistry.getTypeHandler(Object.class);
                }
            } catch (Exception e) {
                typeHandler = typeHandlerRegistry.getTypeHandler(Object.class);
            }

            row.put(name, getValue(typeHandler, obj, i + 1));
        }

        return row;
    }

    /**
     * 根据传入的操作对象类型进行数据的获取
     *
     * @param typeHandler 类型处理器
     * @param operate 操作对象
     * @param columnIndex 列下标
     *
     * @return 列下标对应的值
     *
     * @throws SQLException 异常
     */
    private Object getValue(TypeHandler typeHandler, Object operate, int columnIndex) throws SQLException {
        if (operate instanceof ResultSet) {
            return typeHandler.getResult((ResultSet) operate, columnIndex);
        } else if (operate instanceof CallableStatement) {
            return typeHandler.getResult((CallableStatement) operate, columnIndex);
        } else {
            throw new RuntimeException("不能处理的类型！");
        }
    }

}
