package cn.ivan.mountain;

import cn.ivan.mountain.facorty.EnableMountainClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMountainClient(basePackage = "cn.ivan")
@SpringBootApplication
public class MountainTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainTestApplication.class, args);
    }

}
