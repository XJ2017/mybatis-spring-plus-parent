package com.ddblock.mybatis.spring.boot;

import com.ddblock.mybatis.spring.boot.bean.User2;
import com.ddblock.mybatis.spring.boot.bean.User;
import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.CommonDaoFactory;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 注意：此处没有使用到Spring的事务管理，因为注入的CommonDao接口的实现对象CommonDaoProxy没有添加事务注解
 */
@SpringBootTest(classes = StartApplication.class)
public class CommonDaoTest extends AbstractJUnit4SpringContextTests {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoTest.class);

    @Autowired
    private CommonDao<User> userDao;

    @Autowired
    private CommonDao<User2> user2Dao;

    @Autowired
    private PlatformTransactionManager txmanager;

    @After
    public void deleteTestData() {
        userDao.deleteBatch(new User(), false);
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

        User whereUser = new User();
        whereUser.setId(addUser.getId());

        LOGGER.info("不处理空值字段");
        boolean success = userDao.updateBatch(setUser, whereUser, false) > 0;
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

        boolean success = userDao.deleteBatch(new User(), false) > 0;
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

        User queryUser = new User();
        queryUser.setName("name");

        LOGGER.info("不带Order by");
        userDao.searchList(queryUser);

        LOGGER.info("带Order by");
        userDao.searchList(queryUser, new Order("id", false));
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
    public void testSearchAll() {
        addTestData();
        addTestData();

        LOGGER.info("不带Order by");
        userDao.searchAll();

        LOGGER.info("带Order by");
        userDao.searchAll(new Order("id", false));
    }

    @Test
    public void testSearchAllByPage() {
        addTestData();
        addTestData();
        addTestData();
        addTestData();

        Page<User> page = new Page<>();
        page.setPageSize(2);

        LOGGER.info("不带Order by");
        userDao.searchAllByPage(page);

        LOGGER.info("带Order by");
        userDao.searchAllByPage(page, new Order("id", false));
    }

    @Test
    public void testSearchCount() {
        addTestData("name");
        addTestData("name");
        addTestData();

        User User = new User();
        User.setName("name");

        long count = userDao.searchCount(User);
        LOGGER.info("表中数据记录数：" + count);
    }

    @Test
    public void testSearchAllCount() {
        addTestData();
        addTestData();

        long count = userDao.searchAllCount();
        LOGGER.info("表中数据总记录数：" + count);
    }


    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());

        boolean success = userDao.add(addUser) > 0;
        Assert.assertTrue(success);

        return addUser;
    }

    private User addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);

        boolean success = userDao.add(addUser) > 0;
        Assert.assertTrue(success);

        return addUser;
    }

}
