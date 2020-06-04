package cn.ivan.mountain;

import cn.ivan.mountain.facorty.EnableMountainClient;
import cn.ivan.mountain.proxy.impl.CglibApiProxyCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMountainClient
@SpringBootApplication
public class MountainTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainTestApplication.class, args);
    }

}
