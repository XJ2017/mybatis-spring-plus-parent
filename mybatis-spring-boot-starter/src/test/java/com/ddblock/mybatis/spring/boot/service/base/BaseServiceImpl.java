package com.ddblock.mybatis.spring.boot.service.base;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 基础服务的实现类
 *
 * Author XiaoJia
 * Date 2019-03-12 15:01
 */
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    protected CommonDao<T> commonDao;

    @Override
    public boolean add(T model) {
        return commonDao.add(model) > 0;
    }

    @Override
    public boolean update(T model, boolean doNull) {
        return commonDao.update(model, doNull) > 0;
    }

    @Override
    public boolean updateBatch(T setData, T whereData, boolean doNull) {
        return commonDao.updateBatch(setData, whereData, doNull) > 0;
    }

    @Override
    public boolean delete(Serializable id) {
        return commonDao.delete(id) > 0;
    }

    @Override
    public boolean deleteBatch(T whereData, boolean doNull) {
        return commonDao.deleteBatch(whereData, doNull) > 0;
    }

    @Override
    public T searchOne(Serializable id) {
        return commonDao.searchOne(id);
    }

    @Override
    public List<T> searchList(T whereData, Order... orders) {
        return commonDao.searchList(whereData, orders);
    }

    @Override
    public List<T> searchListBySQL(SQL sql) {
        return commonDao.searchListBySQL(sql);
    }

    @Override
    public List<T> searchAll(Order... orders) {
        return commonDao.searchAll(orders);
    }

    @Override
    public Page<T> searchAllByPage(Page<T> page, Order... orders) {
        return commonDao.searchAllByPage(page, orders);
    }

    @Override
    public long searchCount(T whereData) {
        return commonDao.searchCount(whereData);
    }

    @Override
    public long searchAllCount() {
        return commonDao.searchAllCount();
    }
}
