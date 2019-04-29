package com.ddblock.mybatis.spring.boot.service.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;

/**
 * 基础服务的实现类
 *
 * Author XiaoJia Date 2019-03-12 15:01
 */
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
public abstract class BaseServiceImpl<M, E extends BaseExample> implements BaseService<M, E> {

    @Autowired
    protected CommonDao<M, E> commonDao;

    @Override
    public int add(M model) {
        return commonDao.add(model);
    }

    @Override
    public int update(M model, boolean doNull) {
        return commonDao.update(model, doNull);
    }

    @Override
    public int updateBatch(M setData, E example, boolean doNull) {
        return commonDao.updateBatch(setData, example, doNull);
    }

    @Override
    public int delete(Serializable id) {
        return commonDao.delete(id);
    }

    @Override
    public int deleteBatch(E example) {
        return commonDao.deleteBatch(example);
    }

    @Override
    public M searchOne(Serializable id) {
        return commonDao.searchOne(id);
    }

    @Override
    public List<M> searchList(E example, Order... orders) {
        return commonDao.searchList(example, orders);
    }

    @Override
    public List<M> searchListBySQL(Map<String, Object> paramMap, SQL sql) {
        return commonDao.searchListBySQL(paramMap, sql);
    }

    @Override
    public void searchPage(Page<M> page, E example, Order... orders) {
        commonDao.searchPage(page, example, orders);
    }

    @Override
    public void searchPageBySQL(Page<M> page, Map<String, Object> paramMap, SQL sql) {
        commonDao.searchPageBySQL(page, paramMap, sql);
    }

    @Override
    public long searchCount(E example) {
        return commonDao.searchCount(example);
    }

}
