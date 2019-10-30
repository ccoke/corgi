package site.itcp.tools.zookeeper.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "zookeeper.config")
public class ZookeeperProperties {
    private String connectString;
    private Integer sessionTimeOut;
    private String file;
    private String root;
    private String publicRoot;
    private String defaultContext;
    private String profileSeparator;
}
