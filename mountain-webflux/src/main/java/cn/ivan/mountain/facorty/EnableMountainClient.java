package cn.ivan.mountain.facorty;

import cn.ivan.mountain.proxy.ApiProxyCreator;
import cn.ivan.mountain.proxy.impl.JdkApiProxyCreator;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yanqi
 * @date 2020-06-03 23:21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MountainClientScannerRegistrar.class)
public @interface EnableMountainClient {

    /**
     *  扫描包
     */
    @AliasFor("basePackage")
    String [] value() default {};

    /**
     *  扫描包
     */
    @AliasFor("value")
    String[] basePackage() default {};

    /**
     *  创建代理方式
     */
    Class<? extends ApiProxyCreator> proxyClass() default JdkApiProxyCreator.class;
}
