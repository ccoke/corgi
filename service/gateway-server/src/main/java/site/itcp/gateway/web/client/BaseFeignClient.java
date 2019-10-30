package site.itcp.gateway.web.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("base-server")
@Component
public interface BaseFeignClient {
    @GetMapping("/hello")
    String hello();
}
