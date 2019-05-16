package com.ddblock.mybatis.spring.boot.model;

import com.ddblock.mybatis.spring.plus.model.annotation.ComplexTable;

/**
 * @author XiaoJia
 * @since 2019-05-16 0:01
 */
@ComplexTable
public class ComplexUser {

    private User user;

    private User2 user2;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User2 getUser2() {
        return user2;
    }

    public void setUser2(User2 user2) {
        this.user2 = user2;
    }

}
