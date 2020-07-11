package com.ddblock.mybatis.spring.boot.service;

import com.ddblock.mybatis.spring.boot.StartApplication;
import com.ddblock.mybatis.spring.boot.example.UserExample;
import com.ddblock.mybatis.spring.boot.model.ComplexUser;
import com.ddblock.mybatis.spring.boot.model.User;
import com.ddblock.mybatis.spring.boot.model.User2;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
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
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 此处有使用到Spring的事务管理，因为注入UserService的实现对象UserServiceImpl添加了事务注解
 * <p>
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

    @Test
    public void testOneCache() {
        // 一次缓存，当前会话有效
        User addUser = addTestData();
        userService.duplicateSelect(addUser.getId());
    }

    @Test
    public void testTwoCache() {
        // 二次缓存，全局有效，必须事务提交才会刷新到缓存（全局缓存的数据存储在MappedStatement中）
        // 考虑到咱们是复用CommonMapper，只会对应一个MappedStatement，也就是大家操作同一个缓存对象
        // 由于Mybatis是使用synchronize实现同步，则提交事务的同时同步二级缓存将导致阻塞，而且查询时也会阻塞。
        // 所有本项目不开启二级缓存，如需验证二级缓存则在CommonMapper添加类注解@CacheNamespace
        LOGGER.info("第一次查询");
        userService.searchList(null);
        LOGGER.info("第二次查询");
        userService.searchList(null);
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
        example.setOrderByClause("id DESC");
        userService.searchList(example);
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
        UserExample example = new UserExample();
        example.setOrderByClause("id DESC");
        userService.searchPage(page, example);
        Assert.assertEquals(page.getTotalRecord(), 3);
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
