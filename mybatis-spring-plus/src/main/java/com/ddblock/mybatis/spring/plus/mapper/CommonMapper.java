package com.ddblock.mybatis.spring.plus.mapper;

import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 操作DB的通用处理Mapper
 *
 * @author XiaoJia
 * @since 2019-03-04 17:47
 */
public interface CommonMapper {

    /**
     * 添加一条记录
     *
     * @param model 记录
     * @return int 返回的变更条数
     */
    @InsertProvider(type = CommonMapperProvider.class, method = "add")
    <T> int add(T model);

    /**
     * 根据 id 更新对应的model
     *
     * @param model  变更记录
     * @param doNull 字段值为空是否处理
     * @return int 返回的变更条数
     */
    @UpdateProvider(type = CommonMapperProvider.class, method = "update")
    <T> int update(@Param("model") T model, @Param("doNull") boolean doNull);

    /**
     * 根据对象值条件，批量更新符合条件的记录集合
     *
     * @param setData 变更之后的数据
     * @param example 变更条件
     * @param doNull  字段值为空是否处理
     * @param <T>     表模型类
     * @param <E>     表模型查询类
     * @return int 返回的变更条数
     */
    @UpdateProvider(type = CommonMapperProvider.class, method = "updateBatch")
    <T, E extends BaseExample> int updateBatch(@Param("setData") T setData, @Param("example") E example,
                                               @Param("doNull") boolean doNull);

    /**
     * 根据 id 删除一条数据
     *
     * @param table 表结构类
     * @param id    记录ID
     * @param <T>   表结构
     * @return 返回的变更条数
     */
    @DeleteProvider(type = CommonMapperProvider.class, method = "delete")
    <T> int delete(@Param("table") Class<T> table, @Param("id") Serializable id);

    /**
     * 根据对象值条件，批量删除符合条件的记录集合
     *
     * @param table   表结构类
     * @param example 变更条件
     * @param <T>     表模型类
     * @param <E>     表模型查询类
     * @return int 返回的变更条数
     */
    @DeleteProvider(type = CommonMapperProvider.class, method = "deleteBatch")
    <T, E extends BaseExample> int deleteBatch(@Param("table") Class<T> table, @Param("example") E example);

    /**
     * 根据id查询一条记录
     *
     * @param table 表结构类
     * @param id    记录ID
     * @param <T>   表结构
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchOne")
    <T> List<Map<String, Object>> searchOne(@Param("table") Class<T> table, @Param("id") Serializable id);

    /**
     * 根据对象值条件，查询符合条件的记录集合
     *
     * @param table   表模型
     * @param example 查询条件
     * @param <T>     表模型类
     * @param <E>     表模型查询类
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchList")
    <T, E extends BaseExample> List<Map<String, Object>> searchList(@Param("table") Class<T> table,
                                                                    @Param("example") E example);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap SQL中的参数键值对
     * @param sql      查询SQL
     * @param <T>      表结构
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchListBySQL")
    <T> List<Map<String, Object>> searchListBySQL(@Param("paramMap") Map<String, Object> paramMap,
                                                  @Param("sql") SQL sql);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param example 查询条件
     * @param sql     查询SQL
     * @param <T>     表结构
     * @param <E>     表模型查询类
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchListBySQL2")
    <T, E extends BaseExample> List<Map<String, Object>> searchListBySQL2(@Param("example") E example,
                                                                          @Param("sql") SQL sql);

    /**
     * 根据查询条件，查询符合条件的记录集合
     *
     * @param table     表模型类
     * @param example   查询对象
     * @param rowBounds 行边界
     * @param <T>       表模型类
     * @param <E>       表模型条件类
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchPage")
    <T, E extends BaseExample> List<Map<String, Object>> searchPage(@Param("table") Class<T> table,
                                                                    @Param("example") E example, RowBoundsEx rowBounds);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap  SQL中的参数键值对
     * @param sql       查询SQL
     * @param rowBounds 行边界
     * @param <T>       表结构
     */
    @ResultType(value = CommonMapperResultType.class)
    @SelectProvider(type = CommonMapperProvider.class, method = "searchPageBySQL")
    <T> List<Map<String, Object>> searchPageBySQL(@Param("paramMap") Map<String, Object> paramMap,
                                                  @Param("sql") SQL sql, RowBoundsEx rowBounds);

    /**
     * 获取符合条件的总记录数
     *
     * @param table   表模型类
     * @param example 查询条件
     * @param <T>     表模型
     * @param <E>     表模型条件类
     * @return 记录数
     */
    @SelectProvider(type = CommonMapperProvider.class, method = "searchCount")
    <T, E extends BaseExample> long searchCount(@Param("table") Class<T> table, @Param("example") E example);

}
