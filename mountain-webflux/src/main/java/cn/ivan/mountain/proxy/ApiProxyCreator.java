package cn.ivan.mountain.proxy;

import org.springframework.core.env.Environment;

/**
 *
 *  代理类创建接口
 * @author yanqi69
 * @date 2020/6/3
 */
public interface ApiProxyCreator {

    /**
     *  创建方法
     * @param type 接口类型
     * @param environment 环境变量
     * @return {@link java.lang.Object}
     */
    Object creator(Class<?> type, Environment environment);

}
