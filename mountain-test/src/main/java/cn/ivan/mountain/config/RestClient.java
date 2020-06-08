package cn.ivan.mountain.config;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/8
 */
@Slf4j
public class RestClient implements MountainClient {

    private RestTemplate restTemplate;

    /**
     * 初始化客户端
     *
     * @param apiMetadata 服务元素据
     */
    @Override
    public void init(ApiMetadata apiMetadata) {
        log.info("rest client init");
        this.restTemplate = new RestTemplateBuilder().rootUri(apiMetadata.getBaseUrl()).build();
    }

    /**
     * 实际接口方法请求
     *
     * @param methodMetadata 方法元数据
     * @return {@link Object}
     */
    @Override
    public Object invoke(MethodMetadata methodMetadata) {
        log.info("rest client invoke");
        return Mono.just("rest client");
    }
}
