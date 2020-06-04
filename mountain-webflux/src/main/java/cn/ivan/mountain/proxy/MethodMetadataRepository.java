package cn.ivan.mountain.proxy;


import cn.ivan.mountain.bean.MethodMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
public class MethodMetadataRepository {

    private static Map<String, MethodMetadata> repository = new ConcurrentHashMap<>();

    public static MethodMetadata get(String key, Function<String,MethodMetadata> mapping){
        return repository.computeIfAbsent(key,mapping);
    }


}
