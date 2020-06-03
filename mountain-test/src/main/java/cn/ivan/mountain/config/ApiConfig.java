package cn.ivan.mountain.config;

import cn.ivan.mountain.proxy.ApiProxyCreator;
import cn.ivan.mountain.proxy.impl.JdkApiProxyCreator;
import cn.ivan.mountain.service.BasicService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanqi
 * @date 2020-06-03 20:55
 */
//@Configuration
public class ApiConfig {

    @Bean
    public ApiProxyCreator apiProxyCreator(){
        return new JdkApiProxyCreator();
    }

    @Bean
    public FactoryBean<BasicService> factoryBean(ApiProxyCreator apiProxyCreator){
        return new FactoryBean<BasicService>() {
            @Override
            public BasicService getObject() throws Exception {
                return (BasicService) apiProxyCreator.creator(BasicService.class);
            }

            @Override
            public Class<?> getObjectType() {
                return BasicService.class;
            }
        };
    }



}
