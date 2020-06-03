package cn.ivan.mountain.client.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
public class WebClientImpl implements MountainClient {

    private WebClient webClient;

    /**
     * 初始化客户端
     *
     * @param apiMetadata 服务元素据
     */
    @Override
    public void init(ApiMetadata apiMetadata) {
        this.webClient = WebClient.create(apiMetadata.getBaseUrl());
    }

    /**
     * 实际接口方法请求
     *
     * @param methodMetadata 方法元数据
     * @return {@link Object}
     */
    @Override
    public Object invoke(MethodMetadata methodMetadata) {
        WebClient.RequestBodySpec requestBodySpec = webClient.method(methodMetadata.getMethod())
                .uri(methodMetadata.getUri(), methodMetadata.getArgs());
        Mono body = methodMetadata.getBody();
        WebClient.ResponseSpec retrieve;
        if(body != null){
            retrieve = requestBodySpec.bodyValue(body).retrieve();
        }else {
            retrieve = requestBodySpec.retrieve();
        }
        Object result;
        if(methodMetadata.isFlux()){
            result = retrieve.bodyToFlux(methodMetadata.getReturnActualType());
        }else {
            result = retrieve.bodyToMono(methodMetadata.getReturnActualType());
        }
        return result;
    }
}
