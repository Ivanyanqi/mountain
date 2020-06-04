package cn.ivan.mountain.client;

import cn.ivan.mountain.client.impl.WebClientImpl;
import lombok.Setter;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
public class MountainClientFactory {

    @Setter
    private MountainClient mountainClient;

    public MountainClient getMountainClient(){
        return mountainClient == null ? new WebClientImpl() : mountainClient;
    }

}
