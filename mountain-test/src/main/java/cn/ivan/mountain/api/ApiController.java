package cn.ivan.mountain.api;

import cn.ivan.mountain.service.BaiduService;
import cn.ivan.mountain.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author yanqi
 * @date 2020-06-03 20:53
 */
@RestController
public class ApiController {

    @Autowired
    private BasicService basicService;
    @Autowired
    private BaiduService baiduService;

    @GetMapping("/api/say")
    public Mono<String> say() {
        return basicService.say();
    }

    @GetMapping("/api/say/flux")
    public Flux<String> sayFlux() {
        return basicService.sayFluxs();
    }

    /**
     * ,produces = MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @GetMapping(value = "/api/say/flux/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sayFluxsSteam() {
        return basicService.sayFluxsSteam();
    }

    @GetMapping("/api/baidu/index")
    public Mono<String> baiduIndex() {
        return baiduService.index();
    }

}
