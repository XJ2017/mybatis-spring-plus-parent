package com.ddblock.mybatis.spring.boot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.CommonDaoProxy;
import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;

/**
 * 抽象DAO对象生成工厂
 *
 * @author XiaoJia
 * @date 2019-03-12 8:58
 */
@AutoConfigureAfter(SqlSessionTemplate.class)
public abstract class AbstractCommonDaoFactory {

    private static final Map<Class<?>, CommonDaoProxy> CACHE_MAP = new ConcurrentHashMap<>();

    @Autowired
    private SqlSession sqlSession;

    protected <T, E extends BaseExample> CommonDao<T, E> addDaoBean(Class<T> table, Class<E> example) {
        CommonDaoProxy<T, E> dao;
        if (!CACHE_MAP.containsKey(table)) {
            dao = new CommonDaoProxy<>(table, example);
            // 只使用Table当做查询条件，因为一个Table只会存在一个Example
            CACHE_MAP.put(table, dao);
        } else {
            // noinspection unchecked
            dao = CACHE_MAP.get(table);
        }

        CommonMapper mapper = sqlSession.getMapper(CommonMapper.class);
        dao.setMapper(mapper);
        return dao;
    }

}
