package com.ddblock.mybatis.spring.boot.service.impl;

import com.ddblock.mybatis.spring.boot.example.User2Example;
import com.ddblock.mybatis.spring.boot.model.User2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.service.User2Service;
import com.ddblock.mybatis.spring.boot.service.base.BaseServiceImpl;

/**
 * Author XiaoJia Date 2019-03-14 11:49
 */
@Service("user2Service")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
public class User2ServiceImpl extends BaseServiceImpl<User2, User2Example> implements User2Service {

}
