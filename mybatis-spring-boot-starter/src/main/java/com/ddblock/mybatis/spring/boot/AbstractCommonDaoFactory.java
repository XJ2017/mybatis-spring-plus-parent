package com.ddblock.mybatis.spring.boot;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.CommonDaoProxy;
import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象DAO对象生成工厂
 *
 * Author XiaoJia
 * Date 2019-03-12 8:58
 */
@AutoConfigureAfter(SqlSessionTemplate.class)
public abstract class AbstractCommonDaoFactory {

    private static final Map<Class<?>, CommonDaoProxy> cacheMap = new ConcurrentHashMap<>();

    @Autowired
    private SqlSession sqlSession;

    protected <T> CommonDao<T> addDaoBean(Class<T> table) {
        CommonDaoProxy<T> dao;
        if (!cacheMap.containsKey(table)) {
            dao = new CommonDaoProxy<>(table);
            cacheMap.put(table, dao);
        } else {
            //noinspection unchecked
            dao = cacheMap.get(table);
        }

        CommonMapper mapper = sqlSession.getMapper(CommonMapper.class);
        dao.setMapper(mapper);
        return dao;
    }

}
