package cn.ivan.mountain.service;

import cn.ivan.mountain.annotation.MountainClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author yanqi
 * @date 2020-06-03 20:51
 */
@MountainClient("http://yqyfw.online:8080")
public interface BasicService {

    @GetMapping("/api/rector/say")
    Mono<String> say();


    @GetMapping("/api/rector/flux")
    Flux<String> sayFluxs();

    @GetMapping("/api/rector/stream/flux")
    Flux<String> sayFluxsSteam();
}
