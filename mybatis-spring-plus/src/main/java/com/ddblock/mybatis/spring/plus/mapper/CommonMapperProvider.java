package com.ddblock.mybatis.spring.plus.mapper;

import com.ddblock.mybatis.spring.plus.mapper.support.Order;
import com.ddblock.mybatis.spring.plus.mapper.support.Page;
import com.ddblock.mybatis.spring.plus.model.annotation.Id;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通用处理Mapper的SQL实现
 *
 * Author XiaoJia
 * Date 2019-03-06 11:08
 */
public class CommonMapperProvider {

    /**
     * 添加一条记录
     *
     * @param model 记录
     * @param <T>   表结构
     *
     * @return 生成的SQL
     */
    public <T> String add(T model) {
        return new SQL() {
            {
                INSERT_INTO(getTableName(model.getClass()));

                List<String> fieldNames = getFieldNames(model.getClass());
                for (String fieldName : fieldNames) {
                    VALUES(fieldName, "#{" + fieldName + "}");
                }
            }

        }.toString();
    }

    /**
     * 根据 id 更新对应的model
     *
     * @param model  变更记录
     * @param doNull 字段值为空是否处理
     *
     * @return 生成的SQL
     */
    public <T> String update(T model, boolean doNull) {
        String primary = getPrimaryIndex(model.getClass());
        List<String> fieldNames = getFieldNames(model.getClass());

        return new SQL() {
            {
                UPDATE(getTableName(model.getClass()));

                for (String fieldName : fieldNames) {
                    if (primary.equals(fieldName))
                        continue;

                    Object fieldValue = getFieldValue(model, fieldName);
                    if (!doNull && null == fieldValue)
                        continue;

                    SET(fieldName + "=#{param1." + fieldName + "}");
                }

                WHERE(primary + "=#{param1." + primary + "}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，批量更新符合条件的记录集合
     *
     * @param setData   变更之后的数据
     * @param whereData 变更条件（注意：字段为空不作为Where条件）
     * @param doNull    字段值为空是否处理
     * @param <T>       表结构
     *
     * @return 生成的SQL
     */
    public <T> String updateBatch(T setData, T whereData, boolean doNull) {
        Class<?> table = setData.getClass();
        List<String> fieldNames = getFieldNames(table);

        return new SQL() {
            {
                UPDATE(getTableName(table));

                for (String fieldName : fieldNames) {
                    Object fieldValue = getFieldValue(setData, fieldName);
                    if (!doNull && null == fieldValue)
                        continue;

                    SET(fieldName + "=#{param1." + fieldName + "}");
                }

                for (String fieldName : fieldNames) {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (null == fieldValue)
                        continue;

                    WHERE(fieldName + "=#{param2." + fieldName + "}");
                }
            }

        }.toString();
    }

    /**
     * 根据 id 删除一条数据
     *
     * @param table 表结构类
     * @param id    记录ID
     * @param <T>   表结构
     *
     * @return 生成的SQL
     */
    public <T> String delete(Class<T> table, Serializable id) {
        return new SQL() {
            {
                DELETE_FROM(getTableName(table));
                WHERE(getPrimaryIndex(table) + "=#{param2}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，批量删除符合条件的记录集合
     *
     * @param whereData 变更条件
     * @param doNull    字段值为空是否处理
     * @param <T>       表结构
     *
     * @return 生成的SQL
     */
    public <T> String deleteBatch(T whereData, boolean doNull) {
        return new SQL() {
            {
                DELETE_FROM(getTableName(whereData.getClass()));

                List<String> fieldNames = getFieldNames(whereData.getClass());
                for (String fieldName : fieldNames) {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (!doNull && null == fieldValue)
                        continue;

                    WHERE(fieldName + "=#{param1" + fieldName + "}");
                }

            }

        }.toString();
    }

    /**
     * 根据id查询一条记录
     *
     * @param table 表结构类
     * @param id    记录ID
     * @param <T>   表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchOne(Class<T> table, Serializable id) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));
                WHERE(getPrimaryIndex(table) + "=#{param2}");
            }

        }.toString();
    }

    /**
     * 根据对象值条件，查询符合条件的记录集合
     *
     * @param whereData 查询条件（注意：字段为空不作为Where条件）
     * @param orders    排序规则
     * @param <T>       表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchList(T whereData, Order... orders) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(whereData.getClass()));

                List<String> fieldNames = getFieldNames(whereData.getClass());
                for (String fieldName : fieldNames) {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (fieldName != null && fieldValue != null)
                        WHERE(fieldName + "=#{param1." + fieldName + "}");
                }

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        }.toString();
    }

    /**
     * 根据自定义SQL，查询符合条件的记录集合
     *
     * @param sql 查询SQL
     * @param <T> 表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchListBySQL(SQL sql) {
        return sql.toString();
    }

    /**
     * 按照排序规则将表中数据全部查询出来
     *
     * @param table  表结构类
     * @param orders 排序规则
     * @param <T>    表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAll(Class<T> table, Order... orders) {
        return new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        }.toString();
    }

    /**
     * 按照排序规则将表中数据分页查询出来
     *
     * @param table  表结构类
     * @param page   分页信息
     * @param orders 培训规则
     * @param <T>    表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAllByPage(Class<T> table, Page<T> page, Order... orders) {
        SQL sql = new SQL() {
            {
                SELECT("*");
                FROM(getTableName(table));

                for (Order order : orders) {
                    ORDER_BY(order.toSqlString());
                }
            }

        };

        StringBuilder sb = new StringBuilder();
        sql.usingAppender(sb);
        sb.append(" limit ").append(page.getStartRow()).append(" , ").append(page.getEndRow());

        return sb.toString();
    }

    /**
     * 获取符合条件的总记录数
     *
     * @param whereData 查询条件（注意：字段为空不作为Where条件）
     * @param <T>       表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchCount(T whereData) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(getTableName(whereData.getClass()));

                List<String> fieldNames = getFieldNames(whereData.getClass());
                for (String fieldName : fieldNames) {
                    Object fieldValue = getFieldValue(whereData, fieldName);
                    if (fieldName != null && fieldValue != null)
                        WHERE(fieldName + "=#{" + fieldName + "}");
                }
            }
        }.toString();
    }

    /**
     * 获取总记录数
     *
     * @param table 表结构类
     * @param <T>   表结构
     *
     * @return 生成的SQL
     */
    public <T> String searchAllCount(Class<T> table) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(getTableName(table));
            }
        }.toString();
    }

    // ----------------------------------- private method ------------------------------------------------

    /**
     * 获取表结构对应数据库中的表名
     *
     * @param table 表结构类
     * @param <T>   表结构
     *
     * @return 表名
     */
    private static <T> String getTableName(Class<T> table) {
        Table t = table.getDeclaredAnnotation(Table.class);
        return isEmpty(t.value()) ? formatToDBName(table.getSimpleName()) : t.value();
    }

    /**
     * 获取表结构中主键字段名
     *
     * @param table 表结构类
     * @param <T>   表结构
     *
     * @return 表主键的字段名
     */
    private static <T> String getPrimaryIndex(Class<T> table) {
        Field[] fields = table.getDeclaredFields();

        List<String> list = new ArrayList<>();
        for (Field f : fields) {
            Id annotation = f.getDeclaredAnnotation(Id.class);
            if (annotation != null) {
                String dbFieldName = isEmpty(annotation.value()) ? formatToDBName(f.getName()) : annotation.value();
                list.add(dbFieldName);
            }
        }

        if (list.isEmpty()) {
            throw new RuntimeException("未找到表实体：" + table.getName() + "，中的主键！");
        } else if (list.size() > 1) {
            throw new RuntimeException("表实体：" + table.getName() + "，中存在多个主键！" + Arrays.toString(list.toArray()));
        }

        return list.get(0);
    }

    /**
     * 获取表结构类中所有字段的字段名
     *
     * @param table 表结构类
     * @param <T> 表结构
     *
     * @return 所有字段的字段名
     */
    private static <T> List<String> getFieldNames(Class<T> table) {
        List<String> fieldList = new ArrayList<>();

        Field[] fields = table.getDeclaredFields();
        for (Field field : fields) {
            com.ddblock.mybatis.spring.plus.model.annotation.Field annotation;
            annotation = field.getDeclaredAnnotation(com.ddblock.mybatis.spring.plus.model.annotation.Field.class);
            if (annotation != null) {
                String dbFieldName = isEmpty(annotation.value()) ? formatToDBName(field.getName()) : annotation.value();
                fieldList.add(dbFieldName);
            }
        }

        return fieldList;
    }


    /**
     * 获取表结构类中指定字段的值
     *
     * @param t 表结构类的实例对象
     * @param fieldName 表字段名称
     * @param <T> 表结构
     *
     * @return 字段值
     */
    private static <T> Object getFieldValue(T t, String fieldName) {
        Method m = null;

        Method[] methods = t.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("get" + fieldName)) {
                m = method;
                break;
            }
        }

        if (m == null) {
            throw new RuntimeException("找不到字段" + fieldName + "的get方法！");
        }

        Object result;
        try {
            result = m.invoke(t);
        } catch (Exception e) {
            throw new RuntimeException("反射执行方法失败！", e);
        }

        return result;
    }

    /**
     * 判断字符串对象是否为空或则长度为零（没有执行trim()方法）
     *
     * @param s 字符串对象
     *
     * @return 是否为空或空串
     */
    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 将Java中属性名、类名的命名规范相应转为数据库中字段名、表名的命名规范
     *
     * @param originName 符合Java中属性名、类名的命名规范的字符串
     *
     * @return 符合数据库中字段名、表名的命名规范的字符串
     */
    private static String formatToDBName(String originName) {
        if (isEmpty(originName))
            return null;

        StringBuilder sb = new StringBuilder();
        char[] chars = originName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 65 && c <= 90) {
                if (i != 0)
                    sb.append('_');
                c = (char) (c + 32);
            }
            sb.append(c);
        }

        return sb.toString();
    }

}
