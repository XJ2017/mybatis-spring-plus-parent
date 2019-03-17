package com.ddblock.mybatis.spring.plus.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
import com.ddblock.mybatis.spring.plus.util.ExceptionUtil;
import com.ddblock.mybatis.spring.plus.util.StringUtil;

/**
 * 将数据库中查询出来的Map数据，转换为Model数据
 *
 * @param <T>
 *            表模型
 *
 * @author XiaoJia
 * @since 2019-03-06 16:27
 */
public class CommonMapperResultHandler<T> implements ResultHandler {

    private Class<T> table;
    private Page<T> page;
    private List<T> dataList = new ArrayList<>();

    public CommonMapperResultHandler() {
        // 验证结果处理器时会用到
    }

    public CommonMapperResultHandler(Class<T> table) {
        this(table, null);
    }

    public CommonMapperResultHandler(Class<T> table, Page<T> page) {
        this.table = table;
        this.page = page;
    }

    @Override
    public void handleResult(ResultContext resultContext) {
        T model;
        try {
            model = table.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("反射获取表对应的模型实例失败！", e);
        }

        Map<String, Field> fieldMap = ClassUtil.getAllField(table);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)resultContext.getResultObject();
        map.forEach((dbFieldName, dbFieldVale) -> {
            String fieldName = StringUtil.formatToJavaName(dbFieldName);
            if (!fieldMap.containsKey(fieldName)) {
                throw ExceptionUtil.wrapException("表[%s]中找不到属性[%s]，不能将DB中字段[%s]映射到表中", table.getName(), fieldName,
                    dbFieldName);
            }

            Field field = fieldMap.get(fieldName);
            field.setAccessible(true);
            try {
                field.set(model, dbFieldVale);
            } catch (IllegalAccessException e) {
                throw ExceptionUtil.wrapException("将表[%s]属性[%s]中set值[%s]失败！", e, table.getName(), fieldName,
                    dbFieldVale);
            }
        });

        dataList.add(model);
    }

    public Page<T> getPage() {
        if (page != null) {
            page.setResults(dataList);
        }
        return page;
    }

    public List<T> getDataList() {
        return dataList;
    }

}
