package com.ddblock.mybatis.spring.plus.bean;

import com.ddblock.mybatis.spring.plus.model.annotation.ComplexTable;

/**
 * @author XiaoJia
 * @since 2019-05-16 0:01
 */
@ComplexTable
public class ComplexUser2 {

    private User u;

    private User2 u2;

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public User2 getU2() {
        return u2;
    }

    public void setU2(User2 u2) {
        this.u2 = u2;
    }
}
