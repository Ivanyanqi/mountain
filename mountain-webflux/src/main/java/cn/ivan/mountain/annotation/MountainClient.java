package cn.ivan.mountain.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  客户端注解
 * @author yanqi69
 * @date 2020/6/3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MountainClient {

    /**
     *  请求服务地址
     * @return
     */
    String value() default "";
}
