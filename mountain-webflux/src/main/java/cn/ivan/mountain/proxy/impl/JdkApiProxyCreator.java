package cn.ivan.mountain.proxy.impl;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;
import cn.ivan.mountain.client.MountainClient;
import cn.ivan.mountain.client.impl.WebClientImpl;
import cn.ivan.mountain.proxy.ApiProxyCreator;
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
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * jdk 动态代理实现代理类创建
 *
 * @author yanqi69
 * @date 2020/6/3
 */
public class JdkApiProxyCreator implements ApiProxyCreator {

    /**
     * 创建方法
     *
     * @param type 接口类型
     * @return {@link Object}
     */
    @Override
    public Object creator(Class<?> type) {
        MountainClient client = new WebClientImpl();
        cn.ivan.mountain.annotation.MountainClient annotation = type.getAnnotation(cn.ivan.mountain.annotation.MountainClient.class);
        ApiMetadata apiMetadata = ApiMetadata.builder().baseUrl(annotation.value()).build();
        client.init(apiMetadata);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            MethodMetadata methodMetadata = extractMethod(method, args);
            return client.invoke(methodMetadata);
        });
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
