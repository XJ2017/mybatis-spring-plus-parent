package com.ddblock.mybatis.spring.plus.mapper;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 将数据库中查询出来的Map数据，转换为Model数据
 *
 * Author XiaoJia
 * Date 2019-03-06 16:27
 */
public class CommonMapperResultHandler<T> implements ResultHandler {

    private Class<T> table;

    private List<T> dataList = new ArrayList<>();

    public CommonMapperResultHandler() {
        // 验证结果处理器时会用到
    }

    public CommonMapperResultHandler(Class<T> table) {
        this.table = table;
    }

    @Override
    public void handleResult(ResultContext resultContext) {
        T model;
        try {
            model = table.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("反射获取表对应的模型实例失败！", e);
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) resultContext.getResultObject();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Field field = table.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(model, entry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("将DB中的数据映射到实体类失败！", e);
        }

        dataList.add(model);
    }

    public List<T> getDataList() {
        return dataList;
    }

}
