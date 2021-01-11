package cn.ivan.mountain.proxy.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import cn.ivan.mountain.client.MountainClientFactory;
import cn.ivan.mountain.client.impl.WebClientImpl;
import cn.ivan.mountain.proxy.ApiProxyCreator;
import cn.ivan.mountain.proxy.MethodMetadataBuilder;
import cn.ivan.mountain.proxy.MethodMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

/**
 * jdk 动态代理实现代理类创建
 *
 * @author yanqi69
 * @date 2020/6/3
 */
@Slf4j
public class JdkApiProxyCreator implements ApiProxyCreator{

    /**
     * 创建方法
     *
     * @param type 接口类型
     * @return {@link Object}
     */
    @Override
    public Object creator(Class<?> type, Environment environment) {
        MountainClient client = MountainClientFactory.getMountainClient();
        cn.ivan.mountain.annotation.MountainClient annotation = type.getAnnotation(cn.ivan.mountain.annotation.MountainClient.class);
        String baseUrl = environment.resolvePlaceholders(annotation.value());
        ApiMetadata apiMetadata = ApiMetadata.builder().baseUrl(baseUrl).build();
        client.init(apiMetadata);
        log.info("web client init by jdk proxy complete serviceUrl : {} service interface : {}", baseUrl, type.getName());
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            log.info("method class {}", String.format("%s.%s", type.getName(), method.getName()));
            MethodMetadata methodMetadata = MethodMetadataRepository.get(String.format("%s.%s", type.getName(), method.getName()), (key) ->
                    MethodMetadataBuilder.getInstance().build(method, args)
            );
            log.info("extractMethod : {}", methodMetadata);
            return client.invoke(methodMetadata);
        });
    }


}
