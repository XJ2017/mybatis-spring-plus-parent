package com.ddblock.mybatis.spring.boot.generator;

import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;

import java.util.List;

/**
 * 定制Mybatis Generator的入口类。在generatorConfig.xml的context节点下的targetRuntime属性中配置本类全路径名称
 *
 * Author XiaoJia
 * Date 2019-03-11 17:45
 */
public class IntrospectedTableMyBatis3ImplEx extends IntrospectedTableMyBatis3Impl {

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        AbstractJavaGenerator javaGenerator = new SimpleModelGeneratorEx();

        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        javaModelGenerators.add(javaGenerator);

        // 替换注释生成模板
        Context context = javaGenerator.getContext();
        CommentGeneratorConfiguration configuration = context.getCommentGeneratorConfiguration();
        if (configuration == null) {
            configuration = new CommentGeneratorConfiguration();
            configuration.setConfigurationType(DefaultCommentGeneratorEx.class.getName());
            context.setCommentGeneratorConfiguration(configuration);
        }
    }
}
