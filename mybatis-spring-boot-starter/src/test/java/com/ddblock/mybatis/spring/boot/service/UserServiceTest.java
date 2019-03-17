package com.ddblock.mybatis.spring.boot.service;

import com.ddblock.mybatis.spring.boot.StartApplication;
import com.ddblock.mybatis.spring.boot.bean.User;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 此处有使用到Spring的事务管理，因为注入UserService的实现对象UserServiceImpl添加了事务注解
 *
 * 注意：在测试类或测试方法上添加@Transactional是没有用的，TODO  具体原因不清楚！！！
 *
 * Author XiaoJia
 * Date 2019-03-14 11:59
 */
@SpringBootTest(classes = StartApplication.class)
public class UserServiceTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @After
    public void deleteTestData() {
        userService.deleteBatch(new User(), false);
    }

    /**
     * 用来测试子类覆盖父类的事务
     */
    @Test
    public void testAddAndDelete() {
        userService.insertAndDelete();
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
        boolean success = userService.update(addUser, false);
        Assert.assertTrue(success);

        LOGGER.info("处理空值字段");
        success = userService.update(addUser, true);
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
        boolean success = userService.updateBatch(setUser, whereUser, false);
        Assert.assertTrue(success);
    }

    @Test
    public void testDelete() {
        User addUser = addTestData();

        boolean success = userService.delete(addUser.getId());
        Assert.assertTrue(success);
    }

    @Test
    public void testDeleteBatch() {
        addTestData();
        addTestData();

        boolean success = userService.deleteBatch(new User(), false);
        Assert.assertTrue(success);
    }

    @Test
    public void testSearchOne() {
        User addUser = addTestData();

        userService.searchOne(addUser.getId());
    }

    @Test
    public void testSearchList() {
        addTestData("name");
        addTestData("name");
        addTestData();

        User queryUser = new User();
        queryUser.setName("name");

        LOGGER.info("不带Order by");
        userService.searchList(queryUser);

        LOGGER.info("带Order by");
        userService.searchList(queryUser, new Order("id", false));
    }

    @Test
    public void testSearchListBySQL() {
        addTestData();
        addTestData();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        paramMap.put("begin", 0);
        paramMap.put("end", 10);
        userService.searchListBySQL(paramMap, new SQL() {
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
        userService.searchPageBySQL(page, paramMap, new SQL() {
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
        userService.searchAll();

        LOGGER.info("带Order by");
        userService.searchAll(new Order("id", false));
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
        userService.searchAllByPage(page);

        LOGGER.info("带Order by");
        userService.searchAllByPage(page, new Order("id", false));
    }

    @Test
    public void testSearchCount() {
        addTestData("name");
        addTestData("name");
        addTestData();

        User User = new User();
        User.setName("name");

        long count = userService.searchCount(User);
        LOGGER.info("表中数据记录数：" + count);
    }

    @Test
    public void testSearchAllCount() {
        addTestData();
        addTestData();

        long count = userService.searchAllCount();
        LOGGER.info("表中数据总记录数：" + count);
    }


    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());

        boolean success = userService.add(addUser);
        Assert.assertTrue(success);

        return addUser;
    }

    private void addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);

        boolean success = userService.add(addUser);
        Assert.assertTrue(success);
    }

}
