package cn.ivan.mountain.client;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
public class MountainClientFactory implements FactoryBean<MountainClient> {


    public MountainClient getObject() throws Exception {
        return null;
    }

    public Class<?> getObjectType() {
        return MountainClient.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
