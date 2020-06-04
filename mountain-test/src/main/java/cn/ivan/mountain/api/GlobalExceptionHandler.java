package cn.ivan.mountain.api;

import cn.ivan.mountain.exception.MountainHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MountainHttpException.class)
    public ResponseEntity exception(MountainHttpException e){
        log.error(e.getMessage(),e);
        return ResponseEntity.status(e.getHttpStatus()).body(Mono.just("request rpc error"));
    }

}
