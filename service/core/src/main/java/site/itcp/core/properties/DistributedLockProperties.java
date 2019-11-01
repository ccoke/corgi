package site.itcp.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * 分布式锁配置
 */

@Validated
@Data
@ConfigurationProperties(
        prefix = "distributed.lock"
)
public class DistributedLockProperties {
    public static final String ZOOKEEPER = "zookeeper";
    public static final String REDIS = "redis";

    /**
     * 分布式锁重试等待时间 ms
     */
    private long retryAwait = 1500;

    /**
     * 分布式锁过期时间 ms
     */
    private long timeout = 2000;

    /**
     * 中间件类型
     */
    @NotNull
    private String middleware;


}
