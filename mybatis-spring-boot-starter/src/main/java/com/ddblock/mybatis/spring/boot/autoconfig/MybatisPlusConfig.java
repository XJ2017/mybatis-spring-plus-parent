package com.ddblock.mybatis.spring.boot.autoconfig;

import com.ddblock.mybatis.spring.plus.mapper.CommonMapper;
import com.ddblock.mybatis.spring.plus.mapper.CommonMapperTypeHandler;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * {@link MybatisPlusConfig Auto-configuration} for {@link DataSourceAutoConfiguration}.
 */
@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisPlusConfig {

    private static final Logger LOGGER = LogManager.getLogger(MybatisPlusConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        LOGGER.info("Init Mybatis");

        SqlSessionFactoryBean ss = new SqlSessionFactoryBean();
        ss.setDataSource(dataSource);

        SqlSessionFactory sqlSessionFactory = ss.getObject();
        Configuration configuration = sqlSessionFactory.getConfiguration();

        // 添加类型处理器
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        registry.register(CommonMapperTypeHandler.class);

        // 添加Mapper
        configuration.addMapper(CommonMapper.class);

        // 设置日志实现
        configuration.setLogImpl(Slf4jImpl.class);

        return sqlSessionFactory;
    }

}
