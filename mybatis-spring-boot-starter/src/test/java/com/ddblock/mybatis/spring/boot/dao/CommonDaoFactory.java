package com.ddblock.mybatis.spring.boot.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ddblock.mybatis.spring.boot.AbstractCommonDaoFactory;
import com.ddblock.mybatis.spring.boot.example.User2Example;
import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.model.User2;
import com.ddblock.mybatis.spring.plus.CommonDao;

/**
 * 所有Model DAO的工厂类。通过Spring提供的@Bean注解将DAO对象注入到Spring容器中
 * 
 * Date 2019-04-29 08:28
 */
@Configuration
public class CommonDaoFactory extends AbstractCommonDaoFactory {

    @Bean
    protected CommonDao<User2, User2Example> addDaoBeanUser2() {
        return addDaoBean(User2.class, User2Example.class);
    }

    @Bean
    protected CommonDao<User, UserExample> addDaoBeanUser() {
        return addDaoBean(User.class, UserExample.class);
    }
}