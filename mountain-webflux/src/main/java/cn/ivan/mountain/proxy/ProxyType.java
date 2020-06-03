package cn.ivan.mountain.proxy;

import cn.ivan.mountain.proxy.impl.CglibApiProxyCreator;
import cn.ivan.mountain.proxy.impl.JdkApiProxyCreator;

/**
 * @author yanqi
 * @date 2020-06-04 04:48
 */
public enum  ProxyType {

     JDK(JdkApiProxyCreator.class),CGLIB(CglibApiProxyCreator.class);

     Class<?> proxyClass;

     ProxyType(Class<?> proxyClass){
         this.proxyClass = proxyClass;
     }

    public Class<?> getProxyClass() {
        return proxyClass;
    }
}
