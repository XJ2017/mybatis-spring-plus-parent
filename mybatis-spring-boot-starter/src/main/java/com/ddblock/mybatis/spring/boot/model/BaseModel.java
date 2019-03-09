package com.ddblock.mybatis.spring.boot.model;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.MapperFactoryBeanEx;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.util.ClassUtils;

/**
 * 所有Model的抽象层，用来定义注入Bean到Spring容器的方式
 *
 * Author XiaoJia
 * Date 2019-03-08 23:33
 */
@AutoConfigureAfter(SqlSessionFactory.class)
public abstract class BaseModel<T> {

    /**
     * 生成Spring Bean，并将Bean注入到Spring容器
     *
     * @param sqlSessionFactory Mybatis创建的session工厂
     *
     * @return 生成的指定参数类型的DAO对象
     */
    protected CommonDao<T> addBean(SqlSessionFactory sqlSessionFactory) {
        //noinspection unchecked
        MapperFactoryBeanEx<T> mapperFactoryBean = new MapperFactoryBeanEx(ClassUtils.getUserClass(this.getClass()));
        mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
        mapperFactoryBean.afterPropertiesSet();
        return mapperFactoryBean.getObject();
    }

}
