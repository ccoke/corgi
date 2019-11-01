//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package site.itcp.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.zookeeper")
@Data
public class ZookeeperProperties {
    private String connectString = "localhost:2181";
    private Integer baseSleepTimeMs = 50;
    private Integer maxRetries = 10;
    private Integer maxSleepMs = 500;
}
