package com.ddblock.mybatis.spring.plus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;

/**
 * 通用DAO
 *
 * @author XiaoJia
 * @since 2019-03-07 8:12
 *
 * @param <M>
 *            表结构类
 */
public interface CommonDao<M, E extends BaseExample> {

    /**
     * 添加一条记录
     *
     * @param model
     *            记录
     *
     * @return int 返回的变更条数
     */
    int add(M model);

    /**
     * 根据 id 更新对应的model
     *
     * @param model
     *            变更记录
     * @param doNull
     *            字段值为空是否处理
     *
     * @return int 返回的变更条数
     */
    int update(M model, boolean doNull);

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
     */
    int updateBatch(M setData, E example, boolean doNull);

    /**
     * 根据 id 删除一条数据
     *
     * @param id
     *            记录ID
     *
     * @return 返回的变更条数
     */
    int delete(Serializable id);

    /**
     * 根据对象值条件，批量删除符合条件的记录集合
     *
     * @param example
     *            变更条件
     *
     * @return int 返回的变更条数
     */
    int deleteBatch(E example);

    /**
     * 根据id查询一条记录
     *
     * @param id
     *            记录ID
     *
     * @return 返回的记录
     */
    M searchOne(Serializable id);

    /**
     * 根据对象值条件，查询符合条件的记录集合
     *
     * @param example
     *            查询条件
     * @param orders
     *            排序规则
     *
     * @return 返回的变更集合
     */
    List<M> searchList(E example, Order... orders);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     *
     * @return 返回的变更集合
     */
    List<M> searchListBySQL(Map<String, Object> paramMap, SQL sql);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param complexTable
     *            复合表模型
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     *
     * @return 返回的变更集合
     * @param <C>
     *            复合表模型
     */
    <C> List<C> searchComplexListBySQL(Class<C> complexTable, Map<String, Object> paramMap, SQL sql);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param page
     *            分页信息
     * @param example
     *            查询条件
     * @param orders
     *            排序规则
     */
    void searchPage(Page<M> page, E example, Order... orders);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param page
     *            分页信息
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     */
    void searchPageBySQL(Page<M> page, Map<String, Object> paramMap, SQL sql);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param complexTable
     *            复合表模型类（TODO 因为从Page中获取不到自身泛型类，故需要以参数传入）
     * @param page
     *            分页信息
     * @param paramMap
     *            SQL中的参数键值对
     * @param sql
     *            查询SQL
     * @param <C>
     *            复合表模型
     */
    <C> void searchComplexPageBySQL(Class<C> complexTable, Page<C> page, Map<String, Object> paramMap, SQL sql);

    /**
     * 获取符合条件的总记录数
     *
     * @param example
     *            查询条件
     *
     * @return 记录数
     */
    long searchCount(E example);

}
