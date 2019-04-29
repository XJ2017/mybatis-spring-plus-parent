package com.ddblock.mybatis.spring.plus.mapper.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有Mybatis生成的Example的基类，定义Example的通用属性与方法
 *
 * @author XiaoJia
 * @since 2019-04-22 14:15
 */
public abstract class BaseExample<T extends BaseCriteria> {

    private String orderByClause;

    private boolean distinct;

    private List<T> oredCriteria;

    public BaseExample() {
        oredCriteria = new ArrayList<>();
    }

    public T or() {
        T criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public T createCriteria() {
        T criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * 交由子类实例化条件
     *
     * @return Example类中声明Criteria类的对象
     */
    protected abstract T createCriteriaInternal();

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<T> getOredCriteria() {
        return oredCriteria;
    }

    public void or(T criteria) {
        oredCriteria.add(criteria);
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

}
