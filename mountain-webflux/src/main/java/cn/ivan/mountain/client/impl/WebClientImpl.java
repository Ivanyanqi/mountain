package cn.ivan.mountain.client.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
@Slf4j
public class WebClientImpl implements MountainClient {

    private WebClient webClient;

    /**
     * 初始化客户端
     *
     * @param apiMetadata 服务元素据
     */
    @Override
    public void init(ApiMetadata apiMetadata) {
        log.info("web client init ...");
        this.webClient = WebClient.builder().baseUrl(apiMetadata.getBaseUrl()).filter(((request, next) -> {
            log.info("{} HTTP {} {}", request.logPrefix(), request.method(), request.url());
            return next.exchange(request);
        })).build();
    }

    /**
     * 实际接口方法请求
     *
     * @param methodMetadata 方法元数据
     * @return {@link Object}
     */
    @Override
    public Object invoke(MethodMetadata methodMetadata) {
        log.debug("request params : {} ", JSON.toJSONString(methodMetadata));
        WebClient.RequestBodySpec requestBodySpec = webClient.method(methodMetadata.getMethod())
                .uri(methodMetadata.getUri(), methodMetadata.getArgs());
        Mono body = methodMetadata.getBody();
        WebClient.ResponseSpec retrieve;
        if (body != null) {
            retrieve = requestBodySpec.bodyValue(body).retrieve();
        } else {
            retrieve = requestBodySpec.retrieve();
        }
        Object result;
        if (methodMetadata.isFlux()) {
            result = retrieve.bodyToFlux(methodMetadata.getReturnActualType()).filter(p -> {
                log.debug("response flux:{}", JSON.toJSONString(p));
                return true;
            });
        } else {
            result = retrieve.bodyToMono(methodMetadata.getReturnActualType()).filter(p -> {
                log.debug("response mono:{}", JSON.toJSONString(p));
                return true;
            });
        }
        log.debug("build request success");
        return result;
    }
}
