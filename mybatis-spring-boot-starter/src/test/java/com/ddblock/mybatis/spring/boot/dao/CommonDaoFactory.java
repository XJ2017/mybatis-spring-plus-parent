package com.ddblock.mybatis.spring.boot.dao;

import com.ddblock.mybatis.spring.boot.AbstractCommonDaoFactory;
import com.ddblock.mybatis.spring.boot.bean.TestUser;
import com.ddblock.mybatis.spring.boot.bean.TestUser2;
import com.ddblock.mybatis.spring.plus.CommonDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 所有Model DAO的工厂类。通过Spring提供的@Bean注解将DAO对象注入到Spring容器中
 *
 * Author XiaoJia
 * Date 2019-03-12 8:58
 */
@Configuration
public class CommonDaoFactory extends AbstractCommonDaoFactory {

    @Bean
    protected CommonDao<TestUser> addDaoBeanTestUser() {
        return addDaoBean(TestUser.class);
    }

    @Bean
    protected CommonDao<TestUser2> addDaoBeanTestUser2() {
        return addDaoBean(TestUser2.class);
    }

}
