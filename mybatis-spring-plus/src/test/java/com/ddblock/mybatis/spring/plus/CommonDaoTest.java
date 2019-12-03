package com.ddblock.mybatis.spring.plus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ddblock.mybatis.spring.plus.bean.ComplexUser2;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.ddblock.mybatis.spring.plus.bean.ComplexUser;
import com.ddblock.mybatis.spring.plus.bean.User;
import com.ddblock.mybatis.spring.plus.bean.User2;
import com.ddblock.mybatis.spring.plus.example.User2Example;
import com.ddblock.mybatis.spring.plus.example.UserExample;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
import com.ddblock.mybatis.spring.plus.util.SqlUtil;

/**
 * 测试通用DAO
 *
 * @author XiaoJia
 * @date 2019-03-04 17:46
 */
public class CommonDaoTest {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoTest.class);

    @After
    public void deleteTestData() {
        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        userDao.deleteBatch(null);

        CommonDao<User2, User2Example> user2Dao = CommonDaoFactory.getCommonDao(User2.class, User2Example.class);
        user2Dao.deleteBatch(null);

        // 提交线程级别事务
        CommonDaoFactory.commit();
    }

    @Test
    public void testAdd() {
        User addUser = new User();
        addUser.setId(1);
        addUser.setName("name1");
        addUser.setSex(true);

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        userDao.add(addUser);
    }

    @Test
    public void testAdd2() {
        User2 addUser = new User2();
        addUser.setId(1);
        addUser.setName("name121211");

        CommonDao<User2, User2Example> userDao = CommonDaoFactory.getCommonDao(User2.class, User2Example.class);
        userDao.add(addUser);
    }

    @Test
    public void testUpdate() {
        User newUser = addTestData();

        User updateUser = new User();
        updateUser.setId(newUser.getId());
        updateUser.setSex(true);

        LOGGER.info("不处理空值字段");
        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        userDao.update(updateUser, false);

        LOGGER.info("处理空值字段");
        userDao.update(updateUser, true);
    }

    @Test
    public void testUpdateBatch() {
        User newUser = addTestData("name");
        addTestData("name");

        User setUser = new User();
        setUser.setName("name2");
        setUser.setSex(true);

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(newUser.getId());

        LOGGER.info("不处理空值字段");
        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        int changeRow = userDao.updateBatch(setUser, example, false);
        LOGGER.info("变更数据记录数：" + changeRow);
    }

    @Test
    public void testDelete() {
        User newUser = addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        int changeRow = userDao.delete(newUser.getId());
        LOGGER.info("变更数据记录数：" + changeRow);
    }

    @Test
    public void testDeleteBatch() {
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        int changeRow = userDao.deleteBatch(null);
        LOGGER.info("变更数据记录数：" + changeRow);
    }

    @Test
    public void testSearchOne() {
        User newUser = addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        User user = userDao.searchOne(newUser.getId());
    }

    @Test
    public void testSearchList() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andSexEqualTo(true);

        LOGGER.info("不带Order by");
        userDao.searchList(example);

        example.setOrderByClause("id DESC");
        userDao.searchList(example);
    }

    @Test
    public void testSearchListBySQL() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        paramMap.put("begin", 0);
        paramMap.put("end", 10);
        List<User> userList = userDao.searchListBySQL(paramMap, new SQL() {
            {
                SELECT("*");
                FROM("user");
                WHERE("id <> #{paramMap.id}");
                ORDER_BY("id desc limit #{paramMap.begin}, #{paramMap.end}");
            }
        });
    }

    @Test
    public void testUpdate1() {
        User user = addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

        userDao.searchListBySQL(new HashMap<>(), new SQL() {
            {
                UPDATE("user");
                SET("name=CONCAT(name, '1')");
                WHERE("id=" + user.getId());
            }
        });
    }

    @Test
    public void testSearchListBySQL2() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(1);

        List<User> userList = userDao.searchListBySQL(example, new SQL() {
            {
                SELECT("*");
                FROM("user");
                SqlUtil.applyWhere(this, example);
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

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
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
    public void testSearchComplexListBySQL2() {
        addTestData("name1");
        addTestData("name2");
        addTestData("name3");

        addTestUse2Data("name1");
        addTestUse2Data("name2");

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        List<ComplexUser2> userList = userDao.searchComplexListBySQL(ComplexUser2.class, null, new SQL() {
            {
                SELECT(ClassUtil.getSelectSQL(ComplexUser2.class));
                FROM("user u");
                INNER_JOIN("user2 u2 on u.name=u2.name");
            }
        });
        Assert.assertEquals(2, userList.size());
    }

    @Test
    public void testSearchPage() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

        Page<User> page = new Page<>();

        LOGGER.info("不带Order by");
        userDao.searchPage(page, null);

        LOGGER.info("带Order by");
        UserExample example = new UserExample();
        example.setOrderByClause("id DESC");
        userDao.searchPage(page, null);
    }

    @Test
    public void testSearchPageBySQL() {
        addTestData();
        addTestData();
        addTestData();

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);

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

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
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
        addTestData("name1");
        addTestData("name1");
        addTestData("name2");

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("name1");

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        long count = userDao.searchCount(example);
        LOGGER.info("表中数据记录数：" + count);
    }

    private User addTestData() {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName("name" + addUser.getId());
        addUser.setSex(addUser.getId() > 500);

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        userDao.add(addUser);

        return addUser;
    }

    private User addTestData(String name) {
        User addUser = new User();
        addUser.setId(new Random().nextInt(1000));
        addUser.setName(name);
        addUser.setSex(addUser.getId() > 500);

        CommonDao<User, UserExample> userDao = CommonDaoFactory.getCommonDao(User.class, UserExample.class);
        userDao.add(addUser);

        return addUser;
    }

    private void addTestUse2Data(String name) {
        User2 addUser2 = new User2();
        addUser2.setId(new Random().nextInt(1000));
        addUser2.setName(name);

        CommonDao<User2, User2Example> user2Dao = CommonDaoFactory.getCommonDao(User2.class, User2Example.class);
        user2Dao.add(addUser2);
    }

}
