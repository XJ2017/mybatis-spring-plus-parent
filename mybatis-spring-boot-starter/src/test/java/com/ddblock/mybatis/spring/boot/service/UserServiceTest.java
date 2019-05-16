package com.ddblock.mybatis.spring.boot.service;

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

import com.ddblock.mybatis.spring.boot.StartApplication;
import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.ComplexUser;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.model.User2;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;

/**
 * 此处有使用到Spring的事务管理，因为注入UserService的实现对象UserServiceImpl添加了事务注解
 *
 * 注意：在测试类或测试方法上添加@Transactional是没有用的，TODO 具体原因不清楚！！！
 *
 * @author XiaoJia
 * @date 2019-03-14 11:59
 */
@SpringBootTest(classes = StartApplication.class)
public class UserServiceTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private User2Service user2Service;

    @After
    public void deleteTestData() {
        userService.deleteBatch(null);
        user2Service.deleteBatch(null);
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
        boolean success = userService.update(addUser, false) > 0;
        Assert.assertTrue(success);

        LOGGER.info("处理空值字段");
        success = userService.update(addUser, true) > 0;
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
        boolean success = userService.updateBatch(setUser, example, false) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testDelete() {
        User addUser = addTestData();

        boolean success = userService.delete(addUser.getId()) > 0;
        Assert.assertTrue(success);
    }

    @Test
    public void testDeleteBatch() {
        addTestData();
        addTestData();

        boolean success = userService.deleteBatch(null) > 0;
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

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("name");

        LOGGER.info("不带Order by");
        userService.searchList(example);

        LOGGER.info("带Order by");
        userService.searchList(example, new Order("id", false));
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
    public void testSearchComplexListBySQL() {
        addTestData("name1");
        addTestData("name2");
        addTestData("name3");

        addTestUse2Data("name1");
        addTestUse2Data("name2");

        List<ComplexUser> userList = userService.searchComplexListBySQL(ComplexUser.class, null, new SQL() {
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
        userService.searchPage(page, null);

        LOGGER.info("带Order by");
        userService.searchPage(page, null, new Order("id", false));
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
    public void testSearchComplexPageBySQL() {
        addTestData("name1");
        addTestData("name2");
        addTestData("name3");

        addTestUse2Data("name1");
        addTestUse2Data("name2");

        Page<ComplexUser> page = new Page<>();
        page.setPageNo(1);
        page.setPageSize(10);

        userService.searchComplexPageBySQL(ComplexUser.class, page, null, new SQL() {
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

        long count = userService.searchCount(example);
        LOGGER.info("表中数据记录数：" + count);
    }

    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());
        userService.add(addUser);
        return addUser;
    }

    private void addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);
        userService.add(addUser);
    }

    private void addTestUse2Data(String name) {
        User2 addUser2 = new User2();
        addUser2.setId(new Random().nextInt(1000));
        addUser2.setName(name);

        boolean success = user2Service.add(addUser2) > 0;
        Assert.assertTrue(success);
    }

}
