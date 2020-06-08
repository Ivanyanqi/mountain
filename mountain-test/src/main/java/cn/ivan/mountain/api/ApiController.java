package cn.ivan.mountain.api;

import cn.ivan.mountain.service.BaiduService;
import cn.ivan.mountain.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.jws.soap.SOAPBinding;
import java.util.function.Function;

/**
 * @author yanqi
 * @date 2020-06-03 20:53
 */
@Slf4j
@RestController
public class ApiController {

    @Autowired
    private BasicService basicService;
    @Autowired
    private BaiduService baiduService;

    @GetMapping("/api/say")
    public Mono<User> say() {
        log.info(":::::::::::: api say mono start");
        Mono<User> mono = basicService.say().map(User::new);
        log.info(":::::::::::: api say mono end");
        return mono;
    }

    @GetMapping("/api/say/flux")
    public Flux<User> sayFlux() {
        log.info(":::::::::::: api say mono start");
        Flux<User> flux = basicService.sayFluxs().map(User::new);
        log.info(":::::::::::: api say flux end");
        return flux;
    }

    @GetMapping("/api/say/response")
    public Mono<Response<User>> sayResponse() {
        log.info(":::::::::::: api say mono start");
        Mono<User> mono = basicService.say().map(User::new);
        log.info(":::::::::::: api say mono end");
        return mono.flatMap(Response::ok);
    }

    /**
     * ,produces = MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @GetMapping(value = "/api/say/flux/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sayFluxsSteam() {
        return basicService.sayFluxsSteam();
    }

    @GetMapping("/api/baidu/index")
    public Mono<String> baiduIndex() {
        return baiduService.index();
    }

}
