package com.ddblock.mybatis.spring.boot;

import com.ddblock.mybatis.spring.boot.bean.TestUser;
import com.ddblock.mybatis.spring.boot.bean.TestUser2;
import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@SpringBootTest(classes = StartApplication.class)
public class CommonDaoTest extends AbstractJUnit4SpringContextTests {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoTest.class);

    @Autowired
    private CommonDao<TestUser> userDao;

    @Autowired
    private CommonDao<TestUser2> user2Dao;

    @Test
    public void testAdd() {
        TestUser addUser = new TestUser();
        addUser.setId(1);
        addUser.setName("name1");
        addUser.setSex(true);

        userDao.add(addUser);
    }

    @Test
    public void testAdd2() {
        TestUser2 addUser = new TestUser2();
        addUser.setId(1);
        addUser.setName("name121211");

        user2Dao.add(addUser);
    }

    @Test
    public void testUpdate() {
        TestUser addUser = new TestUser();
        addUser.setId(111);
        addUser.setName("name11");
        addUser.setSex(true);

        LOGGER.info("不处理空值字段");
        userDao.update(addUser, false);
        testSearchAll();

        LOGGER.info("处理空值字段");
        userDao.update(addUser, true);
        testSearchAll();
    }

    @Test
    public void testUpdateBatch() {
        TestUser setUser = new TestUser();
        setUser.setName("name2");
        setUser.setSex(true);

        TestUser whereUser = new TestUser();
        whereUser.setId(1);

        LOGGER.info("不处理空值字段");
        int changeRow = userDao.updateBatch(setUser, whereUser, false);
        LOGGER.info("变更数据记录数：" + changeRow);
        testSearchAll();
    }

    @Test
    public void testDelete() {
        testAdd();

        int changeRow = userDao.delete(1);
        LOGGER.info("变更数据记录数：" + changeRow);
        testSearchAll();

        testAdd();
    }

    @Test
    public void testDeleteBatch() {
        testAdd();

        int changeRow = userDao.deleteBatch(new TestUser(), false);
        LOGGER.info("变更数据记录数：" + changeRow);
        testSearchAll();

        testAdd();
    }

    @Test
    public void testSearchOne() {
        userDao.searchOne(1);
    }

    @Test
    public void testSearchList() {
        TestUser queryUser = new TestUser();
        queryUser.setSex(true);

        LOGGER.info("不带Order by");
        userDao.searchList(queryUser);

        LOGGER.info("带Order by");
        userDao.searchList(queryUser, new Order("id", false));
    }

    @Test
    public void testSearchListBySQL() {
        userDao.searchListBySQL(new SQL() {
            {
                SELECT("*");
                FROM("user");
                WHERE("id <> 1");
                ORDER_BY("id desc limit 0, 10");
            }
        });
    }

    @Test
    public void testSearchAll() {
        LOGGER.info("不带Order by");
        userDao.searchAll();

        LOGGER.info("带Order by");
        userDao.searchAll(new Order("id", false));
    }

    @Test
    public void testSearchAllByPage() {
        Page<TestUser> page = new Page<>();

        LOGGER.info("不带Order by");
        userDao.searchAllByPage(page);

        LOGGER.info("带Order by");
        userDao.searchAllByPage(page, new Order("id", false));
    }

    @Test
    public void testSearchCount() {
        TestUser TestUser = new TestUser();
        TestUser.setName("name1");

        long count = userDao.searchCount(TestUser);
        LOGGER.info("表中数据记录数：" + count);
    }

    @Test
    public void testSearchAllCount() {
        long count = userDao.searchAllCount();
        LOGGER.info("表中数据总记录数：" + count);
    }

}
