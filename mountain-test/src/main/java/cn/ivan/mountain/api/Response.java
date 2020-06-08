package cn.ivan.mountain.api;

import lombok.Data;
import reactor.core.publisher.Mono;

/**
 * @author yanqi69
 * @date 2020/6/5
 */
@Data
public class Response<T> {

    private int code;

    private String message;

    private T data;


    public static <T> Mono<Response<T>> ok(T data){
        Response<T> response = new Response<>();
        response.setCode(0);
        response.setMessage("请求成功");
        response.setData(data);
        return Mono.just(response);
    }


}
