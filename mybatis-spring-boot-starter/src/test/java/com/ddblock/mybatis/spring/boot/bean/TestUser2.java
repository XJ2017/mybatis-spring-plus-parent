package com.ddblock.mybatis.spring.boot.bean;

import com.ddblock.mybatis.spring.boot.model.BaseModel;
import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.model.annotation.Field;
import com.ddblock.mybatis.spring.plus.model.annotation.PrimaryIndex;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author XiaoJia
 * Date 2019-03-08 22:36
 */
@Configuration
@Table("user2")
public class TestUser2 extends BaseModel<TestUser2> {

    @Bean(name = "user2")
    public CommonDao<TestUser2> addBean(SqlSessionFactory sqlSessionFactory) {
        return super.addBean(sqlSessionFactory);
    }

    @PrimaryIndex
    @Field
    private Integer id;

    @Field
    private String name;

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

    @Override
    public String toString() {
        return "User(id=" + id + ",name=" + name + ")";
    }

}
