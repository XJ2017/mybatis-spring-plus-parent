package com.ddblock.mybatis.spring.plus;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;

/**
 * 获取通用处理DAO的工厂类（spring boot的模式下不使用）
 *
 * @author XiaoJia
 * @since 2019-03-07 8:13
 */
public class CommonDaoFactory {
    private static final Logger LOGGER = LogManager.getLogger(CommonDaoFactory.class);

    private static SqlSessionFactory sqlSessionFactory;
    private static final ThreadLocal<SqlSession> SQL_SESSION_TL = new ThreadLocal<SqlSession>() {
        @Override
        protected SqlSession initialValue() {
            // TODO 不应该这么操作
            return sqlSessionFactory.openSession();
        }
    };

    // 实例化Mybatis
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("加载Mybatis配置文件失败！", e);
        }
    }

    /**
     * 通过操作的表结构类获取指定类型DAO对象
     *
     * @param table
     *            表结构类
     * @param <T>
     *            表结构
     *
     * @return 指定类型DAO对象
     */
    public static <T, E extends BaseExample> CommonDao<T, E> getCommonDao(Class<T> table, Class<E> example) {
        CommonMapper commonMapper = SQL_SESSION_TL.get().getMapper(CommonMapper.class);
        CommonDaoProxy<T, E> commonDaoImpl = new CommonDaoProxy<>(table, example);
        commonDaoImpl.setMapper(commonMapper);
        return commonDaoImpl;
    }

    /**
     * 使用单独的SqlSession处理业务操作
     *
     * @param runnable
     *            业务操作
     */
    public static void withTransaction(Runnable runnable) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SqlSession oldSqlSession = SQL_SESSION_TL.get();
        try {
            SQL_SESSION_TL.set(sqlSession);

            // 处理业务
            runnable.run();

            LOGGER.warn("开始提交事务");
            sqlSession.commit();
            LOGGER.warn("提交事务成功");
        } catch (Throwable t) {
            LOGGER.warn("开始回滚事务");
            sqlSession.rollback();
            LOGGER.warn("回滚事务成功");

            throw t;
        } finally {
            try {
                sqlSession.close();
            } catch (Throwable t) {
                LOGGER.error("关闭sqlSession失败！", t);
            }

            SQL_SESSION_TL.set(oldSqlSession);
        }
    }

    /**
     * 提交当前线程的事务
     */
    public static void commit() {
        SqlSession sqlSession = SQL_SESSION_TL.get();
        sqlSession.commit();
    }

}
