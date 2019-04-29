package com.ddblock.mybatis.spring.boot.service.impl;

import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.service.UserService;
import com.ddblock.mybatis.spring.boot.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author XiaoJia
 * Date 2019-03-14 11:49
 */
@Service("userService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
public class UserServiceImpl extends BaseServiceImpl<User, UserExample> implements UserService {
    @Override
    public void insertAndDelete() {
        User user = new User();
        user.setId(1);
        user.setName("name");
        commonDao.add(user);

        commonDao.delete(user.getId());
    }
}
