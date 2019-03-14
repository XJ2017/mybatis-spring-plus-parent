package com.ddblock.mybatis.spring.boot.service;

import com.ddblock.mybatis.spring.boot.bean.User;
import com.ddblock.mybatis.spring.boot.service.base.BaseService;

/**
 * Author XiaoJia
 * Date 2019-03-14 11:45
 */
public interface UserService extends BaseService<User> {

    /**
     * 用来测试子类覆盖父类的事务
     */
    void insertAndDelete();

}
