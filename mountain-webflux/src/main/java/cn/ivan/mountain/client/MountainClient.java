package cn.ivan.mountain.client;

import cn.ivan.mountain.bean.ApiMetadata;
import cn.ivan.mountain.bean.MethodMetadata;

/**
 *  响应式http 客户端封装
 * @author yanqi69
 * @date 2020/6/3
 */
public interface MountainClient {

    /**
     *  初始化客户端
     * @param apiMetadata 服务元素据
     */
    void init(ApiMetadata apiMetadata);


    /**
     *  实际接口方法请求
     * @param methodMetadata 方法元数据
     * @return {@link java.lang.Object}
     */
    Object invoke(MethodMetadata methodMetadata);

}
