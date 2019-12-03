package com.ddblock.mybatis.spring.plus.util;

import static com.ddblock.mybatis.spring.plus.util.StringUtil.formatToDBName;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;

import com.ddblock.mybatis.spring.plus.model.annotation.ComplexTable;
import com.ddblock.mybatis.spring.plus.model.annotation.Id;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;

/**
 * 操作Class对象的工具类
 *
 * @author XiaoJia
 * @date 2019-03-13 10:47
 */
public class ClassUtil {

    public static final String DB_SELECT_SPLIT = ".";

    /**
     * 缓存表的所有字段信息
     */
    private static final Map<Class<?>, Map<String, Field>> CACHE_FIELD = new ConcurrentHashMap<>();
    /**
     * 缓存复合表的所有拼接SQL
     */
    private static final Map<Class<?>, String> CACHE_COMPLEX_SQL = new ConcurrentHashMap<>();

    /**
     * 获取表结构对应数据库中的表名
     *
     * @param table
     *            表结构类
     * @param <T>
     *            表结构
     *
     * @return 表名
     */
    public static <T> String getTableName(@NonNull Class<T> table) {
        Table t = table.getDeclaredAnnotation(Table.class);
        return StringUtil.isEmpty(t.value()) ? StringUtil.formatToDBName(table.getSimpleName()) : t.value();
    }

    /**
     * 获取表模型集合的查询片段SQL
     *
     * @param complexTable
     *            复合表模型
     * @return
     */
    public static String getSelectSQL(@NonNull Class<?> complexTable) {
        ComplexTable complexTableAnno = complexTable.getDeclaredAnnotation(ComplexTable.class);
        if (complexTableAnno == null) {
            return null;
        }

        if (CACHE_COMPLEX_SQL.containsKey(complexTable)) {
            return CACHE_COMPLEX_SQL.get(complexTable);
        }

        StringBuilder sb = new StringBuilder();

        // 只支持复合表嵌套单表，不支持复合表嵌套复合表
        Map<String, Field> fieldMap = getAllField(complexTable);
        fieldMap.forEach((fieldName, field) -> {
            // 处理单表字段
            Class<?> subTable = field.getType();
            if (subTable.getDeclaredAnnotation(Table.class) != null) {
                String subDBTableName = field.getName();

                Map<String, Field> fields = getAllField(subTable);
                fields.forEach((subFieldName, subField) -> {

                    com.ddblock.mybatis.spring.plus.model.annotation.Field annotation;
                    annotation =
                        subField.getDeclaredAnnotation(com.ddblock.mybatis.spring.plus.model.annotation.Field.class);
                    if (annotation != null) {
                        sb.append(",").append(subDBTableName).append(DB_SELECT_SPLIT);
                        sb.append(formatToDBName(subFieldName));
                        sb.append(" as '");
                        sb.append(subDBTableName).append(DB_SELECT_SPLIT);
                        sb.append(formatToDBName(subFieldName)).append("'");
                    }

                });
            } else {
                sb.append(",").append(formatToDBName(fieldName));
            }
        });

        String sqlStr = sb.toString().substring(1);
        CACHE_COMPLEX_SQL.put(complexTable, sqlStr);

        return sqlStr;
    }

    /**
     * 获取表结构中主键字段名
     *
     * @param table
     *            表结构类
     * @param <T>
     *            表结构
     *
     * @return 表主键的字段名
     */
    public static <T> String getIdFieldName(Class<T> table) {
        List<String> list = new ArrayList<>();

        Map<String, Field> fieldMap = getAllField(table);
        fieldMap.forEach((fieldName, field) -> {
            Id annotation = field.getDeclaredAnnotation(Id.class);
            if (annotation != null) {
                list.add(fieldName);
            }
        });

        if (list.isEmpty()) {
            throw ExceptionUtil.wrapException("未找到表实体[%s]中的主键！", table.getName());
        } else if (list.size() > 1) {
            throw ExceptionUtil.wrapException("表实体[%s]中存在多个主键[%s]！", table.getName(), Arrays.toString(list.toArray()));
        }

        return list.get(0);
    }

    /**
     * 获取对象中指定字段的值
     *
     * @param obj
     *            实例对象
     * @param fieldName
     *            字段名称
     * @param <T>
     *            实例的类型
     *
     * @return 字段值
     */
    public static <T> Object getFieldValue(T obj, String fieldName) {
        Class<?> clazz = obj.getClass();

        Map<String, Field> fieldMap = getAllField(clazz);

        if (!fieldMap.containsKey(fieldName)) {
            throw new IllegalArgumentException("在类[" + clazz.getName() + "]及其所有父类中找不到[" + fieldName + "]对应的字段!");
        }

        Field field = fieldMap.get(fieldName);
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (Exception e) {
            throw ExceptionUtil.wrapException("反射获取对象[%s]的属性字段[%s]失败！", e, obj, fieldName);
        }
    }

    /**
     * 获取表结构类中所有字段的字段名
     *
     * @param table
     *            表结构类
     * @param <T>
     *            表结构
     *
     * @return 所有字段的字段名
     */
    public static <T> Map<String, String> getFieldAndDBFieldNames(Class<T> table) {
        Map<String, String> fieldAndDBFieldMap = new HashMap<>();

        Map<String, Field> fields = ClassUtil.getAllField(table);
        fields.forEach((fieldName, field) -> {

            com.ddblock.mybatis.spring.plus.model.annotation.Field annotation;
            annotation = field.getDeclaredAnnotation(com.ddblock.mybatis.spring.plus.model.annotation.Field.class);
            if (annotation != null) {
                fieldAndDBFieldMap.put(fieldName, formatToDBName(fieldName));
            }

        });

        return fieldAndDBFieldMap;
    }

    /**
     * 获取除Object外，所有属性信息（包括private修饰的属性）
     *
     * Key：属性名称，Value：属性对象
     *
     * @param clazz
     *            类对象
     *
     * @return 除Object外，所有属性信息
     */
    public static Map<String, Field> getAllField(Class<?> clazz) {
        if (CACHE_FIELD.containsKey(clazz)) {
            return CACHE_FIELD.get(clazz);
        }

        List<Field> list = new ArrayList<>();

        // 添加自身类属性（包含private修饰的属性）
        list.addAll(Arrays.asList(clazz.getDeclaredFields()));

        // 添加所有父类的属性（除Object）
        Class superclass = clazz.getSuperclass();
        while (superclass != Object.class) {
            list.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }

        // 去掉重复的字段
        Map<String, Field> map = new HashMap<>();

        Set<String> set = new HashSet<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            Field field = list.get(i);
            if (set.add(field.getName())) {
                map.put(field.getName(), field);
            }
        }

        CACHE_FIELD.put(clazz, map);
        return map;
    }

}
