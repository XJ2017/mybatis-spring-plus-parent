package com.ddblock.mybatis.spring.boot.generator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ddblock.mybatis.spring.boot.AbstractCommonDaoFactory;
import com.ddblock.mybatis.spring.plus.CommonDao;

/**
 * DAO对象工厂生成器（每个表都会实例化一个新的生成器）
 *
 * @author XiaoJia
 * @date 2019-03-12 11:35
 */
public class CommonDaoFactoryJavaGenerator extends AbstractJavaGenerator {

    private static TopLevelClass factoryTopLevelClass;
    private static volatile boolean isGenerated = false;

    public static CompilationUnit getCompilationUnit() {
        return factoryTopLevelClass;
    }

    // 每个表都会执行一次
    @Override
    public List<CompilationUnit> getCompilationUnits() {
        if (!isGenerated) {
            synchronized (CommonDaoFactoryJavaGenerator.class) {
                if (!isGenerated) {
                    String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
                    factoryTopLevelClass = generateDaoFactory(targetPackage);
                    isGenerated = true;
                }
            }
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);

        // 给生成的工厂类添加addDaoBean方法
        addDaoBeanMethod(topLevelClass);

        return new ArrayList<>();
    }

    /**
     * 生成DAO对象工厂的类
     *
     * @return TopLevelClass
     */
    private static TopLevelClass generateDaoFactory(String targetPackage) {
        int index = targetPackage.lastIndexOf(".");
        String packageName = targetPackage.substring(0, index) + ".dao.CommonDaoFactory";

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(packageName);
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        // 添加注解
        topLevelClass.addAnnotation("@Configuration");
        topLevelClass.addImportedType(Configuration.class.getName());

        // 添加基类
        FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(AbstractCommonDaoFactory.class.getSimpleName());
        topLevelClass.setSuperClass(superClass);
        topLevelClass.addImportedType(AbstractCommonDaoFactory.class.getName());

        // 添加方法需要的导包
        topLevelClass.addImportedType(Bean.class.getName());
        topLevelClass.addImportedType(CommonDao.class.getName());

        // 添加类注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * 所有Model DAO的工厂类。通过Spring提供的@Bean注解将DAO对象注入到Spring容器中");
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * @since " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        topLevelClass.addJavaDocLine(" */");

        return topLevelClass;
    }

    /**
     * 给生成的工厂类添加addDaoBean方法
     *
     * @param topLevelClass
     *            modelTopLevelClass
     *
     */
    private static void addDaoBeanMethod(TopLevelClass topLevelClass) {
        String modelName = topLevelClass.getType().getShortNameWithoutTypeArguments();
        String exampleName = modelName + "Example";

        // 设置返回信息
        FullyQualifiedJavaType retureType = new FullyQualifiedJavaType("CommonDao");
        retureType.addTypeArgument(new FullyQualifiedJavaType(modelName));
        retureType.addTypeArgument(new FullyQualifiedJavaType(exampleName));

        Method method = new Method();
        method.addAnnotation("@Bean");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("addDaoBean" + modelName);
        method.addBodyLine("return addDaoBean(" + modelName + ".class, " + exampleName + ".class);");
        method.setReturnType(retureType);

        String examplePackage = topLevelClass.getType().getPackageName();
        examplePackage = examplePackage.substring(0, examplePackage.lastIndexOf("."));

        // 添加导包信息
        factoryTopLevelClass.addImportedType(topLevelClass.getType());
        factoryTopLevelClass.addImportedType(examplePackage + ".example." + exampleName);
        factoryTopLevelClass.addMethod(method);
    }

}
