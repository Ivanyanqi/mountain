package cn.ivan.mountain.service;

import cn.ivan.mountain.annotation.MountainClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
@MountainClient("http://www.baidu.com")
public interface BaiduService {

    @GetMapping("")
    Mono<String> index();
}
