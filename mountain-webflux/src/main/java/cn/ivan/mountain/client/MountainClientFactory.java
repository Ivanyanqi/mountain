package cn.ivan.mountain.client;

import cn.ivan.mountain.client.impl.WebClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
@Slf4j
public class MountainClientFactory implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    public static MountainClient getMountainClient() {
        // 获取client
        try {
            MountainClient mountainClient = beanFactory.getBean(MountainClient.class);
            log.debug("get bean from bean factory for user define , {} , object : {}", mountainClient.getClass(), mountainClient);
            return mountainClient;
        } catch (NoSuchBeanDefinitionException e) {
            log.debug("use default mountainClient is webClient");
            return new WebClientImpl();
        }
    }

    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after the population of normal bean properties
     * but before an initialization callback such as
     *
     * @param beanFactory owning BeanFactory (never {@code null}).
     *                    The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        MountainClientFactory.beanFactory = beanFactory;
    }
}
