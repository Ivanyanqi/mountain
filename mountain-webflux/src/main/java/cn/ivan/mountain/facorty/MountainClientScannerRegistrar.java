package cn.ivan.mountain.facorty;

import cn.ivan.mountain.proxy.ApiProxyCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;

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
        // 获取注解属性
        AnnotationAttributes annotationAttributes = Objects.requireNonNull(AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableMountainClient.class.getName())),
                "must given proxyClass extends ApiProxyCreator");

        // 扫描的包
        String[] basePackages = annotationAttributes.getStringArray("basePackage");
        if (basePackages.length == 0) {
            basePackages = new String[]{ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }
        log.debug("scanner annotation MountainClient basePackages :{}", Arrays.toString(basePackages));

        // 获取代理类生成实现
        ApiProxyCreator apiProxyCreator = this.apiProxyCreator(annotationAttributes, registry);

        // 扫描特定注解 (MountainClient)
        AnnotationScanner scanner = new AnnotationScanner(registry);
        scanner.doScan(basePackages).forEach(beanDefinitionHolder -> {
            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            log.debug(beanDefinition.getBeanClassName());
            try {
                // 为接口生成代理类
                Class<?> aClass = Class.forName(beanDefinition.getBeanClassName());
                beanFactory.registerSingleton(beanDefinitionHolder.getBeanName(), apiProxyCreator.creator(aClass));
                log.debug("registerSingleton bean complete : {}", aClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 获取代理生成类
     *
     * @param annotationAttributes annotation metadata of the importing class
     * @param registry             current bean definition registry
     * @return {@link ApiProxyCreator}
     */
    private ApiProxyCreator apiProxyCreator(AnnotationAttributes annotationAttributes, BeanDefinitionRegistry registry) {
        // 获取代理类生成实现
        ApiProxyCreator apiProxyCreator;
        try {
            apiProxyCreator = beanFactory.getBean(ApiProxyCreator.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.info("init default apiProxyCreator");
            Class<? extends ApiProxyCreator> proxyClass = annotationAttributes.getClass("proxyClass");
            log.debug("default apiProxyCreator class is {}", proxyClass.getName());
            AbstractBeanDefinition proxyBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(proxyClass).getBeanDefinition();
            String beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(proxyBeanDefinition, registry);
            registry.registerBeanDefinition(beanName, proxyBeanDefinition);
            apiProxyCreator = beanFactory.getBean(ApiProxyCreator.class);
        }
        return apiProxyCreator;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
