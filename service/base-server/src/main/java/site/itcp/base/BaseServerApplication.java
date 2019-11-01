package site.itcp.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import site.itcp.core.lock.EnableDistributedLock;


@EnableDistributedLock
@SpringBootApplication
@EnableDiscoveryClient
public class BaseServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseServerApplication.class, args);
    }

}
