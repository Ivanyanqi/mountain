package cn.ivan.mountain.facorty;

import cn.ivan.mountain.proxy.ApiProxyCreator;
import cn.ivan.mountain.proxy.impl.JdkApiProxyCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
@Slf4j
public class MountainClientScannerRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {


    private DefaultListableBeanFactory beanFactory;


    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * <p>The default implementation is empty.
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMountainClient.class.getName()));
        log.info("======{}",annoAttrs);
        if(annoAttrs == null){
            annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ComponentScan.class.getName()));
        }
        if(annoAttrs == null){
            log.error("need basePackage");
            throw new RuntimeException("need basePackage");
        }
        log.info("======{}",annoAttrs);
        String[] basePackages = annoAttrs.getStringArray("basePackage");
        log.info("======{}", Arrays.toString(basePackages));
        AnnotationScanner scanner = new AnnotationScanner(registry);
        scanner.doScan(basePackages).forEach(beanDefinitionHolder -> {
            log.info(beanDefinitionHolder.getBeanName());
            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            log.info(beanDefinition.getBeanClassName());
            try {
                ApiProxyCreator apiProxyCreator = beanFactory.getBean(ApiProxyCreator.class);
                beanFactory.registerSingleton(beanDefinitionHolder.getBeanName(),apiProxyCreator.creator(Class.forName(beanDefinition.getBeanClassName())));
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(),e);
            }
        });


    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory)beanFactory;
    }
}
