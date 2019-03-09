# mybatis-spring-plus

#### 项目介绍
mybatis-spring-plus是为实现更便捷操作DB而生

#### 便捷点
1. 无需生成与编写 *xxxMapper.xml* 文件
2. 新增或修改表字段信息只需要直接操作表对应的Model类（不包括DB中的修改）
3. 支持在方法参数中自定义SQL

#### 模块
* mybatis-spring-plus
* mybatis-spring-boot-starter

#### 快速测试
1. 克隆项目到本地
2. 在数据库中执行初始化脚本init.sql（在资源根目录下）

    ##### *mybatis-spring-plus*
    1. 进入模块mybatis-spring-plus
    2. 修改mybatis-config.xml中的数据库配置信息（在test的资源下）
    3. 运行测试类：com.ddblock.mybatis.spring.plus.CommonDaoTest（在test的类中）

    #### *mybatis-spring-boot-starter*
    1. 进入模块mybatis-spring-boot-starter
    2. 修改application.properties中的数据库配置信息（在test的资源下）
    3. 运行测试类：com.ddblock.mybatis.spring.boot.CommonDaoTest（在test的类中）

#### 下一步打算
1. 完善与Spring-boot的对接
2. 根据项目需要完善其功能

## 希望大家多提提意见，谢谢！！！
