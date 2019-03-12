package com.ddblock.mybatis.spring.boot;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.MapperFactoryBeanEx;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

/**
 * 抽象DAO对象生成工厂
 *
 * Author XiaoJia
 * Date 2019-03-12 8:58
 */
@AutoConfigureAfter(SqlSessionFactory.class)
public abstract class AbstractCommonDaoFactory {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    protected  <T> CommonDao<T> addDaoBean(Class<T> clazz) {
        MapperFactoryBeanEx<T> mapperFactoryBean = new MapperFactoryBeanEx<>(clazz);
        mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
        mapperFactoryBean.afterPropertiesSet();
        return mapperFactoryBean.getObject();
    }

}
