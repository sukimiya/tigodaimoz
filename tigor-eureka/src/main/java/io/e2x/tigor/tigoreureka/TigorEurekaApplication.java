package io.e2x.tigor.tigoreureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TigorEurekaApplication {

    public static void main(String[] args) {

        SpringApplication.run(TigorEurekaApplication.class, args);
    }

}
