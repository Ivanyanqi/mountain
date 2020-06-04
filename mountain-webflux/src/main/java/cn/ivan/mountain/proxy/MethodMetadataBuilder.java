package cn.ivan.mountain.proxy;

import cn.ivan.mountain.bean.MethodMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
@Slf4j
public class MethodMetadataBuilder {

    /**
     *  单例对象
     */
    private final static MethodMetadataBuilder INSTANCE = new MethodMetadataBuilder();

    /**
     *  构造私有化
     */
    private MethodMetadataBuilder() {

    }

    /**
     *  获取对象
     * @return {@link this}
     */
    public static MethodMetadataBuilder getInstance() {
        return INSTANCE;
    }

    /**
     *  构建 MethodMetadata
     * @param method 方法
     * @param args 方法参数
     * @return {@link MethodMetadata}
     */
    public MethodMetadata build(Method method, Object... args) {
        return this.extractMethod(method, args);
    }

    /**
     * 获取请求方法信息
     *
     * @param method 接口方法
     * @param args   请求参数
     */
    private MethodMetadata extractMethod(Method method, Object[] args) {
        MethodMetadata methodMetadata = new MethodMetadata();
        methodMetadata.setName(method.getName());
        // 获取http请求方法
        setHttpMethodAndUri(method.getAnnotations(), methodMetadata);
        // 返回值类型
        methodMetadata.setReturnType(method.getReturnType());
        //实际数据类型
        methodMetadata.setReturnActualType(getReturnActualType(method.getGenericReturnType()));
        //获取参数
        Parameter[] parameters = method.getParameters();
        Map<String, Object> params = new HashMap<>(16);
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                // pathVariable 类型
                PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    params.put(pathVariable.value(), args[i]);
                    continue;
                }
                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    params.put(requestParam.value(), args[i]);
                    continue;
                }
                //
                RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
                if (requestBody != null) {
                    methodMetadata.setBody((Mono) args[i]);
                    methodMetadata.setRequestActualType(getReturnActualType(parameters[i].getParameterizedType()));
                }
            }
        }
        methodMetadata.setArgs(params);
        return methodMetadata;
    }

    /**
     * 获取返回值数据类型
     *
     * @param type 泛型类型
     */
    private Class<?> getReturnActualType(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * 获取 http 请求方法
     *
     * @param annotations 方法上的所有注解
     * @return {@link HttpMethod}
     */
    private void setHttpMethodAndUri(Annotation[] annotations, MethodMetadata methodMetadata) {
        Arrays.stream(annotations).filter(this::isRequestAnnotation).findFirst().ifPresent(annotation -> {
            if (annotation.annotationType() == RequestMapping.class) {
                RequestMapping requestMapping = (RequestMapping) annotation;
                methodMetadata.setUri(requestMapping.value()[0]);
                RequestMethod[] requestMethods = (requestMapping).method();
                if (requestMethods.length > 0) {
                    methodMetadata.setMethod(HttpMethod.resolve(requestMethods[0].name()));
                } else {
                    methodMetadata.setMethod(HttpMethod.GET);
                }
            } else if (annotation instanceof GetMapping) {
                GetMapping getMapping = (GetMapping) annotation;
                methodMetadata.setUri(getMapping.value()[0]);
                methodMetadata.setMethod(HttpMethod.GET);
            } else if (annotation instanceof PostMapping) {
                PostMapping postMapping = (PostMapping) annotation;
                methodMetadata.setUri(postMapping.value()[0]);
                methodMetadata.setMethod(HttpMethod.POST);
            } else if (annotation instanceof PutMapping) {
                PutMapping putMapping = (PutMapping) annotation;
                methodMetadata.setUri(putMapping.value()[0]);
                methodMetadata.setMethod(HttpMethod.PUT);
            } else if (annotation instanceof DeleteMapping) {
                DeleteMapping deleteMapping = (DeleteMapping) annotation;
                methodMetadata.setUri(deleteMapping.value()[0]);
                methodMetadata.setMethod(HttpMethod.DELETE);
            }
        });

    }

    private boolean isRequestAnnotation(Annotation annotation) {
        return annotation.annotationType() == RequestMapping.class || annotation.annotationType().getAnnotation(RequestMapping.class) != null;
    }


}
