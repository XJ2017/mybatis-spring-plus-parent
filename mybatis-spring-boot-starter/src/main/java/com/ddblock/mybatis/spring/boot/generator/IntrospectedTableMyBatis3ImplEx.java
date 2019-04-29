package com.ddblock.mybatis.spring.boot.generator;

import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.SimpleModelGenerator;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * 定制Mybatis Generator的入口类。在generatorConfig.xml的context节点下的targetRuntime属性中配置本类全路径名称
 *
 * Author XiaoJia Date 2019-03-11 17:45
 */
public class IntrospectedTableMyBatis3ImplEx extends IntrospectedTableMyBatis3Impl {

    private static volatile boolean isAdd = false;

    // 每个表都会执行一次
    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        AbstractJavaGenerator javaGenerator = new SimpleModelGenerator();
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        javaModelGenerators.add(javaGenerator);

        javaGenerator = new ExampleGeneratorEx();
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        javaModelGenerators.add(javaGenerator);

        javaGenerator = new CommonDaoFactoryJavaGenerator();
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

    // 每个表都会执行一次
    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> generatedJavaFiles = super.getGeneratedJavaFiles();

        if (!isAdd) {
            synchronized (IntrospectedTableMyBatis3ImplEx.class) {
                if (!isAdd) {

                    CompilationUnit compilationUnit = CommonDaoFactoryJavaGenerator.getCompilationUnit();
                    GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        context.getJavaModelGeneratorConfiguration().getTargetProject(),
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());

                    generatedJavaFiles.add(gjf);

                    isAdd = true;
                }
            }
        }

        return generatedJavaFiles;
    }
}
