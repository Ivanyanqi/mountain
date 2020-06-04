package cn.ivan.mountain.facorty;

import cn.ivan.mountain.annotation.MountainClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
public class AnnotationScanner extends ClassPathBeanDefinitionScanner {
    /**
     * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
     *
     * @param registry the {@code BeanFactory} to load bean definitions into, in the form
     *                 of a {@code BeanDefinitionRegistry}
     */
    public AnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * Register the default filter for {@link MountainClient @Component}.
     * {@link MountainClient @Component} meta-annotation including the
     */
    @Override
    protected void registerDefaultFilters() {
        // 添加需要扫描的注解
        this.addIncludeFilter(new AnnotationTypeFilter(MountainClient.class));
    }

    /**
     * Perform a scan within the specified base packages,
     * returning the registered bean definitions.
     * <p>This method does <i>not</i> register an annotation config processor
     * but rather leaves this up to the caller.
     *
     * @param basePackages the packages to check for annotated classes
     * @return set of beans registered if any for tooling registration purposes (never {@code null})
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    /**
     *  注册bean 为接口
     * @param beanDefinition beanDefinition 为接口
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface();
    }
}
