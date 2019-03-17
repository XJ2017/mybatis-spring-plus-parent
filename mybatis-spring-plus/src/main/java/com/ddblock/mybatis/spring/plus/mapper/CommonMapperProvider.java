package com.ddblock.mybatis.spring.plus.mapper;

import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.util.Map;

import static com.ddblock.mybatis.spring.plus.util.ClassUtil.*;
import static com.ddblock.mybatis.spring.plus.util.StringUtil.formatToDBName;

/**
 * 通用处理Mapper的SQL实现
 *
 * @author XiaoJia
 * @since 2019-03-06 11:08
 */
public class CommonMapperProvider {

    /**
     * 添加一条记录
     *
     * @param model
     *            记录
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String add(T model) {
        return new SQL() {
            {
                INSERT_INTO(getTableName(model.getClass()));

                Map<String, String> fieldMap = getFieldAndDBFieldNames(model.getClass());
                fieldMap.forEach((fieldName, dbFieldName) -> {
                    VALUES(dbFieldName, "#{" + fieldName + "}");
                });
            }

        }.toString();
    }

    /**
     * 根据 id 更新对应的model
     *
     * @param model
     *            变更记录
     * @param doNull
     *            字段值为空是否处理
     *
     * @return 生成的SQL
     */
    public <T> String update(@Param("model") T model, @Param("doNull") boolean doNull) {
        Class<?> table = model.getClass();
        String idFieldName = getIdFieldName(table);
        Map<String, String> fieldMap = getFieldAndDBFieldNames(table);

        return new SQL() {
            {
                UPDATE(getTableName(table));

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    if (idFieldName.equals(fieldName))
                        return;

                    Object fieldValue = ClassUtil.getFieldValue(model, fieldName);
                    if (!doNull && null == fieldValue)
                        return;

                    SET(dbFieldName + "=#{model." + fieldName + "}");
                });

                WHERE(formatToDBName(idFieldName) + "=#{model." + idFieldName + "}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，批量更新符合条件的记录集合
     *
     * @param setData
     *            变更之后的数据
     * @param whereData
     *            变更条件（注意：字段为空不作为Where条件）
     * @param doNull
     *            字段值为空是否处理
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String updateBatch(@Param("setData") T setData, @Param("whereData") T whereData,
        @Param("doNull") boolean doNull) {
        Class<?> table = setData.getClass();
        Map<String, String> fieldMap = getFieldAndDBFieldNames(table);

        return new SQL() {
            {
                UPDATE(getTableName(table));

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    Object fieldValue = getFieldValue(setData, fieldName);
                    if (!doNull && null == fieldValue)
                        return;

                    SET(dbFieldName + "=#{setData." + fieldName + "}");
                });

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (null == fieldValue)
                        return;

                    WHERE(dbFieldName + "=#{whereData." + fieldName + "}");
                });

            }

        }.toString();
    }

    /**
     * 根据 id 删除一条数据
     *
     * @param table
     *            表结构类
     * @param id
     *            记录ID
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String delete(@Param("table") Class<T> table, @Param("id") Serializable id) {
        return new SQL() {
            {
                DELETE_FROM(getTableName(table));
                WHERE(formatToDBName(getIdFieldName(table)) + "=#{id}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，批量删除符合条件的记录集合
     *
     * @param whereData
     *            变更条件
     * @param doNull
     *            字段值为空是否处理
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String deleteBatch(@Param("whereData") T whereData, @Param("doNull") boolean doNull) {
        Class<?> table = whereData.getClass();
        Map<String, String> fieldMap = getFieldAndDBFieldNames(table);

        return new SQL() {
            {
                DELETE_FROM(getTableName(table));

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    Object fieldValue = ClassUtil.getFieldValue(whereData, fieldName);
                    if (!doNull && null == fieldValue)
                        return;

                    WHERE(dbFieldName + "=#{whereData." + fieldName + "}");
                });
            }

        }.toString();
    }

    /**
     * 根据id查询一条记录
     *
     * @param table
     *            表结构类
     * @param id
     *            记录ID
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchOne(@Param("table") Class<T> table, @Param("id") Serializable id) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));
                WHERE(formatToDBName(getIdFieldName(table)) + "=#{id}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，查询符合条件的记录集合
     *
     * @param whereData
     *            查询条件（注意：字段为空不作为Where条件）
     * @param orders
     *            排序规则
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchList(@Param("whereData") T whereData, @Param("orders") Order... orders) {
        Class<?> table = whereData.getClass();
        Map<String, String> fieldMap = getFieldAndDBFieldNames(table);

        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (fieldName != null && fieldValue != null)
                        WHERE(dbFieldName + "=#{whereData." + fieldName + "}");
                });

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        }.toString();
    }

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchListBySQL(@Param("paramMap") Map<String, Object> paramMap, @Param("sql") SQL sql) {
        return sql.toString();
    }

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchPageBySQL(@Param("paramMap") Map<String, Object> paramMap, @Param("sql") SQL sql) {
        return sql.toString();
    }

    /**
     * 按照排序规则将表中数据全部查询出来
     *
     * @param table
     *            表结构类
     * @param orders
     *            排序规则
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAll(@Param("table") Class<T> table, @Param("orders") Order... orders) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        }.toString();
    }

    /**
     * 按照排序规则将表中数据分页查询出来
     *
     * @param table
     *            表结构类
     * @param orders
     *            培训规则
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAllByPage(@Param("table") Class<T> table, @Param("orders") Order... orders) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        }.toString();
    }

    /**
     * 获取符合条件的总记录数
     *
     * @param whereData
     *            查询条件（注意：字段为空不作为Where条件）
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchCount(T whereData) {
        Class<?> table = whereData.getClass();
        Map<String, String> fieldMap = getFieldAndDBFieldNames(table);

        return new SQL() {
            {
                SELECT("count(1)");
                FROM(getTableName(table));

                fieldMap.forEach((fieldName, dbFieldName) -> {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (fieldValue != null)
                        WHERE(dbFieldName + "=#{" + fieldName + "}");
                });
            }
        }.toString();
    }

    /**
     * 获取总记录数
     *
     * @param table
     *            表结构类
     * @param <T>
     *            表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAllCount(Class<T> table) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(getTableName(table));
            }
        }.toString();
    }

}
