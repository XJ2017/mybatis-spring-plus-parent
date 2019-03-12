package com.ddblock.mybatis.spring.boot.generator;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.mybatis3.model.SimpleModelGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 自定义生成Model的生成器
 *
 * Author XiaoJia
 * Date 2019-03-11 18:16
 */
public class SimpleModelGeneratorEx extends SimpleModelGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.8", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        // 给基类添加类型参数
//        FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(BaseModel.class.getSimpleName());
//        superClass.addTypeArgument(type);
//        factoryTopLevelClass.setSuperClass(superClass);
//        factoryTopLevelClass.addImportedType(BaseModel.class.getName());

        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass,
                introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    // 复制父类的方法
    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        List<IntrospectedColumn> constructorColumns = introspectedTable
                .getAllColumns();

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn
                    .getFullyQualifiedJavaType(), introspectedColumn
                    .getJavaProperty()));
        }

        StringBuilder sb = new StringBuilder();
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.setLength(0);
            sb.append("this."); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }
}
