package com.ddblock.mybatis.spring.plus.interceptor;

import com.ddblock.mybatis.spring.plus.mapper.RowBoundsEx;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 分页拦截器
 *
 * @author XiaoJia
 * @since 2019-03-17 12:34
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}))
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 被代理对象
        Object target = invocation.getTarget();

        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        BoundSql boundSql = (BoundSql) args[5];

        if (rowBounds != RowBounds.DEFAULT && target instanceof Executor) {
            String originSql = boundSql.getSql();

            // 执行SQL获取符合条件的记录总数
            Connection connection = ((Executor) target).getTransaction().getConnection();

            String pageSql = "select count(1) from ( " + originSql + " ) temp ";
            PreparedStatement preparedStatement = connection.prepareStatement(pageSql);

            LanguageDriver lang = mappedStatement.getLang();
            ParameterHandler parameterHandler = lang.createParameterHandler(mappedStatement, parameterObject, boundSql);
            parameterHandler.setParameters(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            long count = 0;
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
            ((RowBoundsEx) rowBounds).setCount(count);

            // 在原SQL上添加limit语句
            MetaObject boundSqlMetaObject = SystemMetaObject.forObject(boundSql);
            boundSqlMetaObject.setValue("sql", appendLimitSql(rowBounds, originSql));
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof CachingExecutor) {
            MetaObject cachingExecutorMetaObject = SystemMetaObject.forObject(target);
            Object delegate = cachingExecutorMetaObject.getValue("delegate");
            Object wrap = Plugin.wrap(delegate, this);
            cachingExecutorMetaObject.setValue("delegate", wrap);
        }
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 空实现
    }

    /**
     * 追加limit
     *
     * @param rowBounds 行边界
     * @param sql       原SQL
     * @return 生成的新SQL
     */
    private String appendLimitSql(RowBounds rowBounds, String sql) {
        return sql + " LIMIT " + rowBounds.getOffset() + " , " + rowBounds.getLimit();
    }

}
