package com.ddblock.mybatis.spring.plus.mapper;

import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.model.annotation.ComplexTable;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;
import com.ddblock.mybatis.spring.plus.util.ClassUtil;
import com.ddblock.mybatis.spring.plus.util.ExceptionUtil;
import com.ddblock.mybatis.spring.plus.util.StringUtil;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将数据库中查询出来的Map数据，转换为Model数据
 *
 * @param <T> 表模型、复合表模型（由多个表模型组成）
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
        this.table = table;
    }

    public CommonMapperResultHandler(Class<T> table, Page<T> page) {
        this.table = table;
        this.page = page;
        // 填充Page中存放数据的容器
        this.page.setResults(dataList);
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    @Override
    public void handleResult(ResultContext resultContext) {
        T model;
        try {
            model = table.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("反射获取表对应的模型实例失败！", e);
        }

        // 存放字段为单表的表名称与其对象
        Map<String, Object> modelFieldMap = new HashMap<>();

        // 处理单表模型
        if (table.getDeclaredAnnotation(Table.class) != null) {

            // 处理复合表模型中字段为表模型的字段
        } else if (table.getDeclaredAnnotation(ComplexTable.class) != null) {

            // 只支持复合表嵌套单表，不支持复合表嵌套复合表
            Map<String, Field> tempMap = ClassUtil.getAllField(table);
            tempMap.forEach((fieldName, field) -> {
                Class<?> subTable = field.getType();
                // 处理单表字段
                if (subTable.getDeclaredAnnotation(Table.class) != null) {
                    String subDBTableName = fieldName.equalsIgnoreCase(
                            subTable.getSimpleName()) ? StringUtil.formatToDBName(fieldName) : fieldName;
                    try {
                        // 实例化表对象
                        Object subModel = subTable.newInstance();
                        // 将子模型赋值给上层对象
                        field.setAccessible(true);
                        field.set(model, subModel);

                        modelFieldMap.put(subDBTableName, subModel);
                    } catch (Exception e) {
                        throw new RuntimeException("反射获取复合表中的单表对象并给复合表对象赋值失败！", e);
                    }
                }
            });

            // 非法情况
        } else {
            throw new IllegalArgumentException("只支持表与复合表赋值！");
        }

        @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) resultContext.getResultObject();
        map.forEach((dbFieldName, dbFieldVale) -> {
            // 处理复合表
            if (dbFieldVale instanceof Map) {
                Object subModel = modelFieldMap.get(dbFieldName);
                if (subModel == null) {
                    throw ExceptionUtil.wrapException("表[%s]中找不到复合属性[%s]，不能映射多值", table.getName(), dbFieldName);
                }
                //noinspection unchecked
                ((Map<String, Object>) dbFieldVale).forEach(
                        (subDbFieldName, subDbFieldValue) -> setFieldValue(subDbFieldName, subDbFieldValue, subModel));
            } else {
                // 处理单表
                setFieldValue(dbFieldName, dbFieldVale, model);
            }
        });

        dataList.add(model);
    }

    private void setFieldValue(String dbFieldName, Object dbFieldVale, Object model) {
        String fieldName = StringUtil.formatToJavaName(dbFieldName);
        Field field = ClassUtil.getAllField(model.getClass()).get(fieldName);

        // 校验字段是否存在
        if (field == null) {
            throw ExceptionUtil.wrapException("表[%s]中找不到属性[%s]，不能将DB中字段[%s]映射到表中", table.getName(), fieldName,
                                              dbFieldName);
        }

        field.setAccessible(true);
        try {
            field.set(model, dbFieldVale);
        } catch (IllegalAccessException e) {
            throw ExceptionUtil.wrapException("将表[%s]属性[%s]中set值[%s]失败！", e, table.getName(), fieldName, dbFieldVale);
        }
    }

    public Page<T> getPage() {
        return page;
    }

    public List<T> getDataList() {
        return dataList;
    }

}
