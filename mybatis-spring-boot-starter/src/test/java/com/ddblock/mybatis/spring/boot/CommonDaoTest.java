package com.ddblock.mybatis.spring.boot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;

import com.ddblock.mybatis.spring.boot.example.User2Example;
import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.ComplexUser;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.model.User2;
import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;

/**
 * 注意：此处没有使用到Spring的事务管理，因为注入的CommonDao接口的实现对象CommonDaoProxy没有添加事务注解
 */
@SpringBootTest(classes = StartApplication.class)
public class CommonDaoTest extends AbstractJUnit4SpringContextTests {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoTest.class);

    @Autowired
    private CommonDao<User, UserExample> userDao;

    @Autowired
    private CommonDao<User2, User2Example> user2Dao;

    @Autowired
    private PlatformTransactionManager txmanager;

    @After
    public void deleteTestData() {
        userDao.deleteBatch(null);
        user2Dao.deleteBatch(null);
    }

    @Test
    public void testAdd() {
        addTestData();
    }

    @Test
    public void testUpdate() {
        User addUser = addTestData();
        addUser.setName("updateName");

        LOGGER.info("不处理空值字段");
        boolean success = userDao.update(addUser, false) > 0;
        Assert.assertTrue(success);

        LOGGER.info("处理空值字段");
        success = userDao.update(addUser, true) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testUpdateBatch() {
        User addUser = addTestData();

        User setUser = new User();
        setUser.setName("nameUpdateBatch1");

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(addUser.getId());

        LOGGER.info("不处理空值字段");
        boolean success = userDao.updateBatch(setUser, example, false) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testDelete() {
        User addUser = addTestData();

        boolean success = userDao.delete(addUser.getId()) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testDeleteBatch() {
        addTestData();
        addTestData();

        boolean success = userDao.deleteBatch(null) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testSearchOne() {
        User addUser = addTestData();

        userDao.searchOne(addUser.getId());
    }

    @Test
    public void testSearchList() {
        addTestData("name");
        addTestData("name");
        addTestData();

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("name");

        LOGGER.info("不带Order by");
        userDao.searchList(example);

        LOGGER.info("带Order by");
        example.setOrderByClause("id DESC");
        userDao.searchList(example);
    }

    @Test
    public void testSearchListBySQL() {
        addTestData();
        addTestData();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        paramMap.put("begin", 0);
        paramMap.put("end", 10);
        userDao.searchListBySQL(paramMap, new SQL() {
            {
                SELECT("*");
                FROM("user");
                WHERE("id <> #{paramMap.id}");
                ORDER_BY("id desc limit #{paramMap.begin}, #{paramMap.end}");
            }
        });
    }

    @Test
    public void testSearchComplexListBySQL() {
        addTestData("name1");
        addTestData("name2");
        addTestData("name3");

        addTestUse2Data("name1");
        addTestUse2Data("name2");

        List<ComplexUser> userList = userDao.searchComplexListBySQL(ComplexUser.class, null, new SQL() {
            {
                SELECT(ClassUtil.getSelectSQL(ComplexUser.class));
                FROM("user");
                INNER_JOIN("user2 on user.name=user2.name");
            }
        });
        Assert.assertEquals(2, userList.size());
    }

    @Test
    public void testSearchPage() {
        addTestData();
        addTestData();
        addTestData();

        Page<User> page = new Page<>();

        LOGGER.info("不带Order by");
        userDao.searchPage(page, null);

        LOGGER.info("带Order by");
        UserExample example = new UserExample();
        example.setOrderByClause("id DESC");
        userDao.searchPage(page, example);
    }

    @Test
    public void testSearchPageBySQL() {
        addTestData();
        addTestData();
        addTestData();

        Page<User> page = new Page<>();
        page.setPageNo(1);
        page.setPageSize(10);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        userDao.searchPageBySQL(page, paramMap, new SQL() {
            {
                SELECT("*");
                FROM("user");
                WHERE("id <> #{paramMap.id}");
                ORDER_BY("id desc");
            }
        });
    }

    @Test
    public void testSearchComplexPageBySQL() {
        addTestData("name1");
        addTestData("name2");
        addTestData("name3");

        addTestUse2Data("name1");
        addTestUse2Data("name2");

        Page<ComplexUser> page = new Page<>();
        page.setPageNo(1);
        page.setPageSize(10);

        userDao.searchComplexPageBySQL(ComplexUser.class, page, null, new SQL() {
            {
                SELECT(ClassUtil.getSelectSQL(ComplexUser.class));
                FROM("user");
                INNER_JOIN("user2 on user.name=user2.name");
                ORDER_BY("user.id desc");
            }
        });
        Assert.assertEquals(2, page.getResults().size());
    }

    @Test
    public void testSearchCount() {
        addTestData("name");
        addTestData("name");
        addTestData();

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("name");

        long count = userDao.searchCount(example);
        LOGGER.info("表中数据记录数：" + count);
    }

    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());

        boolean success = userDao.add(addUser) > 0;
        Assert.assertTrue(success);

        return addUser;
    }

    private void addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);

        boolean success = userDao.add(addUser) > 0;
        Assert.assertTrue(success);
    }

    private void addTestUse2Data(String name) {
        User2 addUser2 = new User2();
        addUser2.setId(new Random().nextInt(1000));
        addUser2.setName(name);

        boolean success = user2Dao.add(addUser2) > 0;
        Assert.assertTrue(success);
    }

}
