package com.ddblock.mybatis.spring.plus;

import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用DAO
 *
 * @author XiaoJia
 * @since 2019-03-07 8:12
 *
 * @param <T> 表结构类
 */
public interface CommonDao<T> {

    /**
     * 添加一条记录
     *
     * @param model 记录
     *
     * @return int 返回的变更条数
     */
    int add(T model);

    /**
     * 根据 id 更新对应的model
     *
     * @param model  变更记录
     * @param doNull 字段值为空是否处理
     *
     * @return int 返回的变更条数
     */
    int update(T model, boolean doNull);

    /**
     * 根据对象值条件，批量更新符合条件的记录集合
     *
     * @param setData  变更之后的数据
     * @param whereData 变更条件（注意：字段为空不作为Where条件）
     * @param doNull 字段值为空是否处理
     *
     * @return int 返回的变更条数
     */
    int updateBatch(T setData, T whereData, boolean doNull);

    /**
     * 根据 id 删除一条数据
     *
     * @param id 记录ID
     *
     * @return 返回的变更条数
     */
    int delete(Serializable id);

    /**
     * 根据对象值条件，批量删除符合条件的记录集合
     *
     * @param whereData  变更条件
     * @param doNull 字段值为空是否处理
     *
     * @return int 返回的变更条数
     */
    int deleteBatch(T whereData, boolean doNull);

    /**
     * 根据id查询一条记录
     *
     * @param id 记录ID
     *
     * @return 返回的记录
     */
    T searchOne(Serializable id);

    /**
     * 根据对象值条件，查询符合条件的记录集合
     *
     * @param whereData  查询条件（注意：字段为空不作为Where条件）
     * @param orders 排序规则
     *
     * @return 返回的变更集合
     */
    List<T> searchList(T whereData, Order... orders);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param paramMap SQL中的参数键值对
     * @param sql 查询SQL
     *
     * @return 返回的变更集合
     */
    List<T> searchListBySQL(Map<String, Object> paramMap, SQL sql);

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param page     分页信息
     * @param paramMap SQL中的参数键值对
     * @param sql 查询SQL
     */
    void searchPageBySQL(Page<T> page, Map<String, Object> paramMap, SQL sql);

    /**
     * 按照排序规则将表中数据全部查询出来
     *
     * @param orders 排序规则
     *
     * @return 返回的表全记录
     */
    List<T> searchAll(Order... orders);

    /**
     * 按照排序规则将表中数据分页查询出来
     *
     * @param page   分页信息
     * @param orders 培训规则
     */
    void searchAllByPage(Page<T> page, Order... orders);

    /**
     * 获取符合条件的总记录数
     *
     * @param whereData 查询条件（注意：字段为空不作为Where条件）
     *
     * @return 记录数
     */
    long searchCount(T whereData);

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    long searchAllCount();

}
