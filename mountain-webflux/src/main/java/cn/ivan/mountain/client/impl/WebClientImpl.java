package cn.ivan.mountain.client.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import cn.ivan.mountain.exception.MountainHttpException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
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
        retrieve.onStatus(HttpStatus::is5xxServerError,this::error)
                .onStatus(HttpStatus::is4xxClientError,this::error)
                .onStatus(HttpStatus::isError,this::error);
        Object result;
        if (methodMetadata.isFlux()) {
            result = retrieve.bodyToFlux(methodMetadata.getReturnActualType()).doOnNext(p->log.info("response body : {}", JSON.toJSONString(p)));
        } else {
            result = retrieve.bodyToMono(methodMetadata.getReturnActualType()).doOnNext(p->log.info("response body : {}", JSON.toJSONString(p)));
        }
        log.debug("build request success");
        return result;
    }

    private Mono<MountainHttpException> error(ClientResponse clientResponse){
        return Mono.error(new MountainHttpException(clientResponse.statusCode(),"error code " + clientResponse.rawStatusCode()));
    }
}
