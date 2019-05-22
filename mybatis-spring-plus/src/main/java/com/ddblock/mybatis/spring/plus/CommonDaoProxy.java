package com.ddblock.mybatis.spring.plus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import com.ddblock.mybatis.spring.plus.mapper.CommonMapperResultHandler;
import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;

/**
 * 通用DAO的代理实现类
 *
 * @author XiaoJia
 * @since 2019-03-07 8:13
 */
public class CommonDaoProxy<M, E extends BaseExample> implements CommonDao<M, E> {

    private Class<M> table;
    @SuppressWarnings("FieldCanBeLocal")
    private Class<E> example;
    private CommonMapper mapper;

    public CommonDaoProxy(Class<M> table, Class<E> example) {
        this.table = table;
        this.example = example;
    }

    public void setMapper(CommonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int add(M model) {
        return mapper.add(model);
    }

    @Override
    public int update(M model, boolean doNull) {
        return mapper.update(model, doNull);
    }

    @Override
    public int updateBatch(M setData, E example, boolean doNull) {
        return mapper.updateBatch(setData, example, doNull);
    }

    @Override
    public int delete(Serializable id) {
        return mapper.delete(table, id);
    }

    @Override
    public int deleteBatch(E example) {
        return mapper.deleteBatch(table, example);
    }

    @Override
    public M searchOne(Serializable id) {
        CommonMapperResultHandler<M> resultHandler = new CommonMapperResultHandler<>(table);
        mapper.searchOne(table, id, resultHandler);
        List<M> dataList = resultHandler.getDataList();
        if (dataList.size() > 1) {
            throw new RuntimeException("根据主键：" + id + "，查询到多条数据：" + Arrays.toString(dataList.toArray()));
        }
        return dataList.isEmpty() ? null : dataList.get(0);
    }

    @Override
    public List<M> searchList(E example) {
        CommonMapperResultHandler<M> resultHandler = new CommonMapperResultHandler<>(table);
        mapper.searchList(table, example, resultHandler);
        return resultHandler.getDataList();
    }

    @Override
    public List<M> searchListBySQL(Map<String, Object> paramMap, SQL sql) {
        CommonMapperResultHandler<M> resultHandler = new CommonMapperResultHandler<>(table);
        mapper.searchListBySQL(paramMap, sql, resultHandler);
        return resultHandler.getDataList();
    }

    @Override
    public <C> List<C> searchComplexListBySQL(Class<C> complexTable, Map<String, Object> paramMap, SQL sql) {
        CommonMapperResultHandler<C> resultHandler = new CommonMapperResultHandler<>(complexTable);
        mapper.searchListBySQL(paramMap, sql, resultHandler);
        return resultHandler.getDataList();
    }

    @Override
    public void searchPage(Page<M> page, E example) {
        CommonMapperResultHandler<M> resultHandler = new CommonMapperResultHandler<>(table, page);
        mapper.searchPage(table, example, resultHandler);
    }

    @Override
    public void searchPageBySQL(Page<M> page, Map<String, Object> paramMap, SQL sql) {
        CommonMapperResultHandler<M> resultHandler = new CommonMapperResultHandler<>(table, page);
        mapper.searchListBySQL(paramMap, sql, resultHandler);
    }

    @Override
    public <C> void searchComplexPageBySQL(Class<C> complexTable, Page<C> page, Map<String, Object> paramMap, SQL sql) {
        CommonMapperResultHandler<C> resultHandler = new CommonMapperResultHandler<>(complexTable, page);
        mapper.searchListBySQL(paramMap, sql, resultHandler);
    }

    @Override
    public long searchCount(E example) {
        return mapper.searchCount(table, example);
    }

}
