package com.ddblock.mybatis.spring.plus.mapper;

import static com.ddblock.mybatis.spring.plus.util.ClassUtil.*;
import static com.ddblock.mybatis.spring.plus.util.StringUtil.formatToDBName;

import java.io.Serializable;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
import com.ddblock.mybatis.spring.plus.util.SqlUtil;

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
     * @param example
     *            变更条件
     *
     * @param doNull
     *            字段值为空是否处理
     * @return int 返回的变更条数
     * @param <T>
     *            表模型类
     * @param <E>
     *            表模型查询类
     */
    public <T, E extends BaseExample> String updateBatch(@Param("setData") T setData, @Param("example") E example,
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

                SqlUtil.applyWhere(this, example);
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
     * @param table
     *            表结构类
     * @param example
     *            变更条件
     *
     * @return 生成的SQL
     * @param <T>
     *            表模型类
     * @param <E>
     *            表模型查询类
     */
    public <T, E extends BaseExample> String deleteBatch(@Param("table") Class<T> table, @Param("example") E example) {
        return new SQL() {
            {
                DELETE_FROM(getTableName(table));
                SqlUtil.applyWhere(this, example);
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
     * @param table
     *            表模型
     * @param example
     *            查询条件
     *
     * @param <T>
     *            表模型类
     * @param <E>
     *            表模型查询类
     * @return 生成的SQL
     */
    public <T, E extends BaseExample> String searchList(@Param("table") Class<T> table, @Param("example") E example) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                SqlUtil.applyWhere(this, example);
                if (example != null && example.getOrderByClause() != null) {
                    this.ORDER_BY(example.getOrderByClause());
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
     *
     * @return 生成的SQL
     */
    public String searchListBySQL(@Param("paramMap") Map<String, Object> paramMap, @Param("sql") SQL sql) {
        return sql.toString();
    }

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param example
     *            查询条件
     * @param sql
     *            查询SQL
     *
     * @return 生成的SQL
     * @param <E>
     *            表模型查询类
     */
    public <E extends BaseExample> String searchListBySQL2(@Param("example") E example, @Param("sql") SQL sql) {
        return sql.toString();
    }

    /**
     * 根据对象值条件，查询符合条件的记录集合并进行分页
     *
     * @param table
     *            表模型
     * @param example
     *            查询条件
     *
     * @param <T>
     *            表模型类
     * @param <E>
     *            表模型查询类
     * @return 生成的SQL
     */
    public <T, E extends BaseExample> String searchPage(@Param("table") Class<T> table, @Param("example") E example) {
        return searchList(table, example);
    }

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     *
     * @return 生成的SQL
     */
    public String searchPageBySQL(@Param("paramMap") Map<String, Object> paramMap, @Param("sql") SQL sql) {
        return sql.toString();
    }

    /**
     * 获取符合条件的总记录数
     *
     * @param table
     *            表模型类
     * @param example
     *            查询条件
     *
     * @param <T>
     *            表模型
     * @param <E>
     *            表模型条件类
     */
    public <T, E extends BaseExample> String searchCount(@Param("table") Class<T> table, @Param("example") E example) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(getTableName(table));

                SqlUtil.applyWhere(this, example);
            }

        }.toString();
    }

}
