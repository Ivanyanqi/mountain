package cn.ivan.mountain.config;

import cn.ivan.mountain.client.MountainClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanqi
 * @date 2020-06-03 20:55
 */

@Configuration
public class ApiConfig {

    @Bean
    public MountainClientBuilder mountainClientBuilder(){
        MountainClientBuilder builder = new MountainClientBuilder();
        builder.setMountainClientClass(RestClient.class);
        return builder;
    }
}
