package com.ddblock.mybatis.spring.boot.service;

import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.service.base.BaseService;

/**
 * @author XiaoJia
 * @since 2019-03-14 11:45
 */
public interface UserService extends BaseService<User, UserExample> {

    /**
     * 用来测试子类覆盖父类的事务
     */
    void insertAndDelete();

    /**
     * 利用重复查询测试Mybatis的一级缓存
     *
     * @param id 记录ID
     */
    void duplicateSelect(Integer id);

}
