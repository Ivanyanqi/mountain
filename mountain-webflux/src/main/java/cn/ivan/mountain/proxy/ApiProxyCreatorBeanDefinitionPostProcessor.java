package cn.ivan.mountain.proxy;

import cn.ivan.mountain.proxy.impl.JdkApiProxyCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * @author yanqi
 * @date 2020-06-04 04:17
 */
@Slf4j
public class ApiProxyCreatorBeanDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor {



    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("=======regiest");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(JdkApiProxyCreator.class);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        registry.registerBeanDefinition(AnnotationBeanNameGenerator.INSTANCE.generateBeanName(beanDefinition,registry),beanDefinition);


    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
