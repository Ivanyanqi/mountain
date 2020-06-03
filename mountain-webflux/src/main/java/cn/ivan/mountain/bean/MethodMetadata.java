package cn.ivan.mountain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
/**
 *  方法元数据信息
 * @author yanqi69
 * @date 2020/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MethodMetadata {

    /**
     *  方法名称
     */
    private String name;

    /**
     *  requestMapping
     */
    private String uri;

    /**
     *  请求类型
     */
    private HttpMethod method;

    /**
     *  请求参数(parameter)
     */
    private Map<String,Object> args;

    /**
     *  请求体 @requestBody
     */
    private Mono body;

    /**
     *  实际类型
     */
    private Class<?> requestActualType;

    /**
     *  返回值类型 Mono ,Flux
     */
    private Class<?> returnType;

    /**
     *  实际数据类型
     */
    private Class<?> returnActualType;

    /**
     *  返回值为Flux 集合数据
     */
    public boolean isFlux(){
        return returnType.isAssignableFrom(Flux.class);
    }
}
