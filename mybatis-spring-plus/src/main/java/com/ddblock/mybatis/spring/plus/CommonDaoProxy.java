package com.ddblock.mybatis.spring.plus;

import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import com.ddblock.mybatis.spring.plus.mapper.CommonMapperResultHandler;
import com.ddblock.mybatis.spring.plus.mapper.RowBoundsEx;
import com.ddblock.mybatis.spring.plus.mapper.support.BaseExample;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> mapList = mapper.searchOne(table, id);
        List<M> dataList = parserMapList(table, mapList);
        if (dataList.size() > 1) {
            throw new RuntimeException("根据主键：" + id + "，查询到多条数据：" + Arrays.toString(dataList.toArray()));
        }
        return dataList.isEmpty() ? null : dataList.get(0);
    }

    @Override
    public List<M> searchList(E example) {
        List<Map<String, Object>> mapList = mapper.searchList(table, example);
        return parserMapList(table, mapList);
    }

    @Override
    public List<M> searchListBySQL(Map<String, Object> paramMap, SQL sql) {
        List<Map<String, Object>> mapList = mapper.searchListBySQL(paramMap, sql);
        return parserMapList(table, mapList);
    }

    @Override
    public List<M> searchListBySQL(E example, SQL sql) {
        List<Map<String, Object>> mapList = mapper.searchListBySQL2(example, sql);
        return parserMapList(table, mapList);
    }

    @Override
    public <C> List<C> searchComplexListBySQL(Class<C> complexTable, Map<String, Object> paramMap, SQL sql) {
        List<Map<String, Object>> mapList = mapper.searchListBySQL(paramMap, sql);
        return parserMapList(complexTable, mapList);
    }

    @Override
    public void searchPage(Page<M> page, E example) {
        RowBoundsEx rowBounds = new RowBoundsEx(page.getStartRow(), page.getPageSize());
        List<Map<String, Object>> mapList = mapper.searchPage(table, example, rowBounds);
        List<M> resultList = parserMapList(table, mapList);
        page.setResults(resultList);
        page.setTotalRecord(rowBounds.getCount());
    }

    @Override
    public void searchPageBySQL(Page<M> page, Map<String, Object> paramMap, SQL sql) {
        RowBoundsEx rowBounds = new RowBoundsEx(page.getStartRow(), page.getPageSize());
        List<Map<String, Object>> mapList = mapper.searchPageBySQL(paramMap, sql, rowBounds);
        List<M> resultList = parserMapList(table, mapList);
        page.setResults(resultList);
        page.setTotalRecord(rowBounds.getCount());
    }

    @Override
    public <C> void searchComplexPageBySQL(Class<C> complexTable, Page<C> page, Map<String, Object> paramMap, SQL sql) {
        RowBoundsEx rowBounds = new RowBoundsEx(page.getStartRow(), page.getPageSize());
        List<Map<String, Object>> mapList = mapper.searchPageBySQL(paramMap, sql, rowBounds);
        List<C> resultList = parserMapList(complexTable, mapList);
        page.setResults(resultList);
        page.setTotalRecord(rowBounds.getCount());
    }

    @Override
    public long searchCount(E example) {
        return mapper.searchCount(table, example);
    }

    private <T> List<T> parserMapList(Class<T> table, List<Map<String, Object>> mapList) {
        CommonMapperResultHandler<T> resultHandler = new CommonMapperResultHandler<>(table);
        final DefaultResultContext<Map<String, Object>> context = new DefaultResultContext<>();
        for (Map<String, Object> map : mapList) {
            context.nextResultObject(map);
            resultHandler.handleResult(context);
        }
        return resultHandler.getDataList();
    }

}
