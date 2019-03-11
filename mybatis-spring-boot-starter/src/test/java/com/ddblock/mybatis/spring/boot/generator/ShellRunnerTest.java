package com.ddblock.mybatis.spring.boot.generator;

import org.junit.Test;
import org.mybatis.generator.api.ShellRunner;

/**
 * Author XiaoJia
 * Date 2019-03-11 17:10
 */
public class ShellRunnerTest {

    @Test
    public void testMain() {

        // 设置
        String[] args = new String[2];
        args[0] = "-configfile";
        args[1] = "F:\\company\\code\\mybatis-spring-plus-parent\\mybatis-spring-boot-starter\\src\\test\\resources\\generatorConfig.xml";

        ShellRunner.main(args);
    }

}
