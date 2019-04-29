package com.ddblock.mybatis.spring.boot.model;

import com.ddblock.mybatis.spring.plus.model.annotation.Field;
import com.ddblock.mybatis.spring.plus.model.annotation.Id;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;

/**
 * Author XiaoJia
 * Date 2019-03-08 22:36
 */
@Table("user")
public class User {

    @Id
    @Field
    private Integer id;

    @Field
    private String name;

    @Field
    private Boolean sex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User(id=" + id + ",name=" + name + ",sex=" + sex + ")";
    }

}
