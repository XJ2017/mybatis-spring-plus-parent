package com.ddblock.mybatis.spring.plus.util;

/**
 * 异常处理类工具
 *
 * Author XiaoJia
 * Date 2019-03-13 11:38
 */
public class ExceptionUtil {

    /**
     * 包装异常
     *
     * @param message 异常提示信息
     * @param e       原始异常
     * @param arg     异常提示信息的参数
     *
     * @return 运行时异常对象
     */
    public static RuntimeException wrapException(String message, Exception e, Object... arg) {
        message = String.format(message, arg);

        // TODO 后期完善
//        if (e instanceof RuntimeException) {
//            return (RuntimeException) e;
//        } else {
//            return new RuntimeException(message, e);
//        }

        return new RuntimeException(message, e);
    }

    /**
     * 包装异常
     *
     * @param message 异常提示信息
     * @param arg     异常提示信息的参数
     *
     * @return 运行时异常对象
     */
    public static RuntimeException wrapException(String message, Object... arg) {
        message = String.format(message, arg);
        return new RuntimeException(message);
    }

}
