package com.ddblock.mybatis.spring.boot.generator;

import java.util.List;

import com.ddblock.mybatis.spring.plus.mapper.support.BaseCriteria;

/**
 * 测试应用自定义通用Example
 *
 * @author XiaoJia
 * @since 2019-04-22 16:58
 */
public abstract class MyBaseCriteria extends BaseCriteria {

    public MyBaseCriteria andIdIsNull() {
        addCriterion("id is null");
        return this;
    }

    public MyBaseCriteria andIdIsNotNull() {
        addCriterion("id is not null");
        return this;
    }

    public MyBaseCriteria andIdEqualTo(Integer value) {
        addCriterion("id =", value, "id");
        return this;
    }

    public MyBaseCriteria andIdNotEqualTo(Integer value) {
        addCriterion("id <>", value, "id");
        return this;
    }

    public MyBaseCriteria andIdGreaterThan(Integer value) {
        addCriterion("id >", value, "id");
        return this;
    }

    public MyBaseCriteria andIdGreaterThanOrEqualTo(Integer value) {
        addCriterion("id >=", value, "id");
        return this;
    }

    public MyBaseCriteria andIdLessThan(Integer value) {
        addCriterion("id <", value, "id");
        return this;
    }

    public MyBaseCriteria andIdLessThanOrEqualTo(Integer value) {
        addCriterion("id <=", value, "id");
        return this;
    }

    public MyBaseCriteria andIdIn(List<Integer> values) {
        addCriterion("id in", values, "id");
        return this;
    }

    public MyBaseCriteria andIdNotIn(List<Integer> values) {
        addCriterion("id not in", values, "id");
        return this;
    }

    public MyBaseCriteria andIdBetween(Integer value1, Integer value2) {
        addCriterion("id between", value1, value2, "id");
        return this;
    }

    public MyBaseCriteria andIdNotBetween(Integer value1, Integer value2) {
        addCriterion("id not between", value1, value2, "id");
        return this;
    }

}
