package cn.ivan.mountain.api;

import cn.ivan.mountain.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author yanqi
 * @date 2020-06-03 20:53
 */
@RestController
public class ApiController {

    @Autowired
    private BasicService basicService;

    @GetMapping("/api/say")
    public Mono<String> say(){
        return basicService.say();
    }
}
