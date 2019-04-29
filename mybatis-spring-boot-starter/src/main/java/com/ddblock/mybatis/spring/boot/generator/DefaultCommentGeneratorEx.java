package com.ddblock.mybatis.spring.boot.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import com.ddblock.mybatis.spring.plus.model.annotation.Id;
import com.ddblock.mybatis.spring.plus.model.annotation.Table;

/**
 * 生成属性与方法注释的生成器（包括注解生成）
 *
 * @author XiaoJia
 * @date 2019-03-11 18:22
 */
public class DefaultCommentGeneratorEx extends DefaultCommentGenerator {

    private Properties properties = new Properties();
    private IntrospectedColumn keyColumn;

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        // 获取表注释
        String remarks = introspectedTable.getRemarks();

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + remarks);
        topLevelClass.addJavaDocLine(" * @since " + dateFormatter.format(new Date()));
        topLevelClass.addJavaDocLine(" */");

        // 添加注解所需要的类
        topLevelClass.addImportedType(Table.class.getName());
        topLevelClass.addImportedType(com.ddblock.mybatis.spring.plus.model.annotation.Field.class.getName());

        // 添加ID注解需要的类
        if (introspectedTable.getPrimaryKeyColumns().size() > 0) {
            topLevelClass.addImportedType(Id.class.getName());
        }

        // 添加注解
        topLevelClass.addAnnotation("@Table");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
        IntrospectedColumn introspectedColumn) {
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + remarks);
        field.addJavaDocLine(" */");

        // 添加Id注解
        List<IntrospectedColumn> pkColumns = introspectedTable.getPrimaryKeyColumns();
        if (pkColumns.size() > 0 && pkColumns.contains(introspectedColumn)) {
            field.addAnnotation("@Id");
        }

        // 添加Field注解
        field.addAnnotation("@Field");
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
        IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**");

        sb.append(" * 获取表字段 ");
        sb.append(introspectedColumn.getActualColumnName());
        sb.append(" 的值");
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *");

        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
        IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**");

        sb.append(" * 设置表字段 ");
        sb.append(introspectedColumn.getActualColumnName());
        sb.append(" 的值");
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *");

        Parameter parm = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(parm.getName());
        sb.append(' ');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" */");
    }

    // ------------ 以下是生成 **Example.java 时生成的注释

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        // 空实现
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        // 空实现
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        // 空实现
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        // 空实现
    }

}
