package cn.ivan.mountain.proxy.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import cn.ivan.mountain.client.impl.WebClientImpl;
import cn.ivan.mountain.proxy.ApiProxyCreator;
import cn.ivan.mountain.proxy.MethodMetadataBuilder;
import cn.ivan.mountain.proxy.MethodMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author yanqi
 * @date 2020-06-04 04:50
 */
@Slf4j
public class CglibApiProxyCreator implements ApiProxyCreator {


    @Override
    public Object creator(Class<?> type) {
        MountainClient client = new WebClientImpl();
        cn.ivan.mountain.annotation.MountainClient annotation = type.getAnnotation(cn.ivan.mountain.annotation.MountainClient.class);
        ApiMetadata apiMetadata = ApiMetadata.builder().baseUrl(annotation.value()).build();
        client.init(apiMetadata);
        log.info("web client init by cglib proxy complete serviceUrl : {} service interface : {}", annotation.value(), type.getName());
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{type});
        enhancer.setCallback((MethodInterceptor) (o, method, args, methodProxy) -> {
            log.info("method class {}", String.format("%s.%s", type.getName(), method.getName()));
            MethodMetadata methodMetadata = MethodMetadataRepository.get(String.format("%s.%s", type.getName(), method.getName()), (key) ->
                    MethodMetadataBuilder.getInstance().build(method, args)
            );
            log.info("extractMethod : {}", methodMetadata);
            return client.invoke(methodMetadata);
        });
        return enhancer.create();
    }
}
