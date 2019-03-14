package com.ddblock.mybatis.spring.plus;

import com.ddblock.mybatis.spring.plus.bean.User;
import com.ddblock.mybatis.spring.plus.bean.User2;
import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;

/**
 * 测试通用DAO
 *
 * Author XiaoJia
 * Date 2019-03-04 17:46
 */
public class CommonDaoTest {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoTest.class);

    @After
    public void deleteTestData() {
        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.deleteBatch(new User(), false);
        });
    }

    @Test
    public void testAdd() {
        User addUser = new User();
        addUser.setId(1);
        addUser.setName("name1");
        addUser.setSex(true);

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.add(addUser);
        });
    }

    @Test
    public void testAdd2() {
        User2 addUser = new User2();
        addUser.setId(1);
        addUser.setName("name121211");

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User2> userDao = CommonDaoFactory.getCommonDao(User2.class);
            userDao.add(addUser);
        });

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User2> userDao = CommonDaoFactory.getCommonDao(User2.class);
            userDao.delete(addUser.getId());
        });
    }

    @Test
    public void testUpdate() {
        User newUser = addTestData();

        User updateUser = new User();
        updateUser.setId(newUser.getId());
        updateUser.setSex(true);

        LOGGER.info("不处理空值字段");
        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.update(updateUser, false);
        });

        LOGGER.info("处理空值字段");
        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.update(updateUser, true);
        });
    }

    @Test
    public void testUpdateBatch() {
        User newUser = addTestData("name");
        addTestData("name");

        User setUser = new User();
        setUser.setName("name2");
        setUser.setSex(true);

        User whereUser = new User();
        whereUser.setId(newUser.getId());

        LOGGER.info("不处理空值字段");
        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            int changeRow = userDao.updateBatch(setUser, whereUser, false);
            LOGGER.info("变更数据记录数：" + changeRow);
        });
    }

    @Test
    public void testDelete() {
        User newUser = addTestData();
        addTestData();

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            int changeRow = userDao.delete(newUser.getId());
            LOGGER.info("变更数据记录数：" + changeRow);
        });
    }

    @Test
    public void testDeleteBatch() {
        addTestData();
        addTestData();

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            int changeRow = userDao.deleteBatch(new User(), false);
            LOGGER.info("变更数据记录数：" + changeRow);
        });
    }

    @Test
    public void testSearchOne() {
        User newUser = addTestData();

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
        User user = userDao.searchOne(newUser.getId());
    }

    @Test
    public void testSearchList() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);

        User queryUser = new User();
        queryUser.setSex(true);

        LOGGER.info("不带Order by");
        userDao.searchList(queryUser);

        LOGGER.info("带Order by");
        userDao.searchList(queryUser, new Order("id", false));
    }

    @Test
    public void testSearchListBySQL() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);

        List<User> userList = userDao.searchListBySQL(new SQL() {
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
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);

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

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);

        Page<User> page = new Page<>();

        LOGGER.info("不带Order by");
        userDao.searchAllByPage(page);

        LOGGER.info("带Order by");
        userDao.searchAllByPage(page, new Order("id", false));
    }

    @Test
    public void testSearchCount() {
        addTestData("name1");
        addTestData("name1");
        addTestData("name2");

        User user = new User();
        user.setName("name1");

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
        long count = userDao.searchCount(user);
        LOGGER.info("表中数据记录数：" + count);
    }

    @Test
    public void testSearchAllCount() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
        long count = userDao.searchAllCount();
        LOGGER.info("表中数据总记录数：" + count);
    }

    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());
        addUser.setSex(addUser.getId() > 500);

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.add(addUser);
        });

        return addUser;
    }

    private User addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);
        addUser.setSex(addUser.getId() > 500);

        CommonDaoFactory.withTransaction(() -> {
            CommonDao<User> userDao = CommonDaoFactory.getCommonDao(User.class);
            userDao.add(addUser);
        });

        return addUser;
    }

}
