# mybatis-spring-plus

#### 项目介绍
基于Mybatis扩展的用于快速开发小项目（已在多个生产项目中使用）的ORM框架

#### 便捷点
1. 无需生成与编写 *xxxMapper.xml* 文件
2. 支持连接数据库自动生成Model对象、Example对象
3. 支持在方法参数中自定义SQL（适用小项目快速编写SQL）
4. 支持同一SQL的多表数据映射

#### 缺点
1. 虽然支持二级缓存（全局缓存），但由于共用CommonMapper导致操作同一个Cache，使得查询与修改缓存阻塞（Mybatis使用Synchronize同步）
2. 未将项目中sql以 *xxxMapper.xml* 文件抽象出来，不利于后期sql优化

#### 模块
* mybatis-spring-plus
* mybatis-spring-boot-starter

#### 快速测试
1. 克隆项目到本地
2. 在数据库中执行初始化脚本init.sql（在资源根目录下）

    ##### *mybatis-spring-plus*
    1. 进入模块mybatis-spring-plus
    2. 修改mybatis-config.xml中的数据库配置信息（在test的资源下）
    3. 运行测试类：com.ddblock.mybatis.spring.plus.CommonDaoTest

    #### *mybatis-spring-boot-starter*
    1. 进入模块mybatis-spring-boot-starter
    2. 修改application.properties中的数据库配置信息（在test的资源下）
    3. 运行测试类：com.ddblock.mybatis.spring.boot.CommonDaoTest（在test的类中）
    4. 修改generatorConfig.xml中的数据库配置信息（在test的资源下）
    5. 运行生成Model的测试类：com.ddblock.mybatis.spring.boot.generator.ShellRunnerTest
    6. **与Service对接的DEMO测试类：com.ddblock.mybatis.spring.boot.service.UserServiceTest**

#### 下一步打算
1. 完善与Spring-boot的对接
2. 根据项目需要完善其功能
3. 深入Mybatis，尽量按照大佬的思路玩

## 希望大家多提提意见，谢谢！！！
