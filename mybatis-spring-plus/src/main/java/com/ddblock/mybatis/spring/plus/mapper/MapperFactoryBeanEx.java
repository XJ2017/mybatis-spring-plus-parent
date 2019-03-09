package com.ddblock.mybatis.spring.plus.mapper;

import com.ddblock.mybatis.spring.plus.CommonDao;
import com.ddblock.mybatis.spring.plus.CommonDaoProxy;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.Assert.notNull;

/**
 * 重新实例化Mapper的对象，使其查找通用Mapper然后返回特定的Dao对象
 *
 * Author XiaoJia
 * Date 2019-03-08 11:56
 */
public class MapperFactoryBeanEx<T> extends MapperFactoryBean<CommonDao> {

    private static final Map<Class<?>, CommonDaoProxy> cacheMap = new ConcurrentHashMap<>();

    private Class<T> table;

    private Class<CommonMapper> mapperInterface = CommonMapper.class;

    private boolean addToConfig = true;

    public MapperFactoryBeanEx() {
        //intentionally empty
    }

    public MapperFactoryBeanEx(Class<T> table) {
        this.table = table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkDaoConfig() {
//        super.checkDaoConfig();
        notNull(super.getSqlSession(), "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");

        notNull(this.mapperInterface, "Property 'mapperInterface' is required");

        Configuration configuration = getSqlSession().getConfiguration();
        if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
            try {
                configuration.addMapper(this.mapperInterface);
            } catch (Exception e) {
                logger.error("Error while adding the bean '" + this.mapperInterface + "' to config.", e);
                throw new IllegalArgumentException(e);
            } finally {
                ErrorContext.instance().reset();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonDao<T> getObject() {
        CommonDaoProxy<T> dao;
        if (!cacheMap.containsKey(table)) {
            dao = new CommonDaoProxy<>(table);
            cacheMap.put(table, dao);
        } else {
            //noinspection unchecked
            dao = cacheMap.get(table);
        }

        CommonMapper commonMapper = getSqlSession().getMapper(this.mapperInterface);
        dao.setMapper(commonMapper);
        return dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CommonDao> getObjectType() {
        return CommonDao.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return false;
    }

    //------------- mutators --------------

    /**
     * Sets the bean interface of the MyBatis bean
     *
     * @param mapperInterface class of the interface
     */
    @Override
    public void setMapperInterface(Class<CommonDao> mapperInterface) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the bean interface of the MyBatis bean
     *
     * @return class of the interface
     */
    public Class<CommonDao> getMapperInterface() {
        throw new UnsupportedOperationException();
    }

    /**
     * If addToConfig is false the bean will not be added to MyBatis. This means
     * it must have been included in mybatis-config.xml.
     * <p>
     * If it is true, the bean will be added to MyBatis in the case it is not already
     * registered.
     * <p>
     * By default addToConfig is true.
     *
     * @param addToConfig a flag that whether add bean to MyBatis or not
     */
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    /**
     * Return the flag for addition into MyBatis config.
     *
     * @return true if the bean will be added to MyBatis in the case it is not already
     * registered.
     */
    public boolean isAddToConfig() {
        return addToConfig;
    }

}
