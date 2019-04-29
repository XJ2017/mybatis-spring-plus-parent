package com.ddblock.mybatis.spring.plus.mapper.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有Mybatis生成的Example中Criteria的基类，定义Criteria的通用属性与方法
 *
 * @author XiaoJia
 * @since 2019-04-22 14:38
 */
public abstract class BaseCriteria {

    private List<Criterion> criteria;

    public BaseCriteria() {
        criteria = new ArrayList<>();
    }

    public boolean isValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
        return criteria;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    protected void addCriterion(String condition) {
        if (condition == null) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criteria.add(new Criterion(condition));
    }

    protected void addCriterion(String condition, Object value, String property) {
        if (value == null) {
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value1, value2));
    }

}
