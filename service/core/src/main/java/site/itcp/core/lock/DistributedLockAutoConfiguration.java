package site.itcp.core.lock;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import site.itcp.core.properties.DistributedLockProperties;
import site.itcp.core.properties.ZookeeperProperties;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties({DistributedLockProperties.class, RedisProperties.class, ZookeeperProperties.class})
public class DistributedLockAutoConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(DistributedLockTemplate.class);
    private final DistributedLockProperties distributedLockProperties;
    private final RedisProperties redisProperties;
    private final ZookeeperProperties zookeeperProperties;

    @Autowired
    public DistributedLockAutoConfiguration(DistributedLockProperties distributedLockProperties, RedisProperties redisProperties, ZookeeperProperties zookeeperProperties) {
        this.distributedLockProperties = distributedLockProperties;
        this.redisProperties = redisProperties;
        this.zookeeperProperties = zookeeperProperties;
    }

    @Bean
    public DistributedLockTemplate distributedLockTemplate() throws IOException {
        if (DistributedLockProperties.REDIS.equals(distributedLockProperties.getMiddleware())) {
            return new RedisDistributedLockTemplate(distributedLockProperties.getRetryAwait(), distributedLockProperties.getTimeout(), jedisPool());
        } else if (DistributedLockProperties.ZOOKEEPER.equals(distributedLockProperties.getMiddleware())){
            return new ZookeeperDistributedLockTemplate(distributedLockProperties.getRetryAwait(), distributedLockProperties.getTimeout(), zooKeeper());
        } else {
            throw new IllegalArgumentException("分布式锁没有配置中间件");
        }
    }

    @ConditionalOnProperty(prefix = "distributed.lock", name = "middleware", havingValue = DistributedLockProperties.REDIS)
    @Bean
    public JedisPool jedisPool() {
        //创建配置实例
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池配置项值
        config.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        config.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
        config.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        //根据配置实例化连接池
        JedisPool jedisPool = StringUtils.isEmpty(redisProperties.getPassword()) ?
                new JedisPool(config, redisProperties.getUrl(), redisProperties.getPort(), (int)redisProperties.getTimeout().toMillis()) : new JedisPool(config, redisProperties.getUrl(), redisProperties.getPort(),(int)redisProperties.getTimeout().toMillis(), redisProperties.getPassword());
        logger.info("redis已连接");
        return jedisPool;
    }

    @ConditionalOnProperty(prefix = "distributed.lock", name = "middleware", havingValue = DistributedLockProperties.ZOOKEEPER)
    @Bean
    public ZooKeeper zooKeeper() throws IOException {
        return new ZooKeeper(zookeeperProperties.getConnectString(), (int)distributedLockProperties.getTimeout(), event -> {
            // 监听类型
            Watcher.Event.EventType type = event.getType();
            if(Watcher.Event.EventType.None == type){
                // 监听路径
                String path = event.getPath();
                // 监听状态
                Watcher.Event.KeeperState state = event.getState();
                logger.info("zookeeper已连接: 路径[{}], 状态[{}], 类型[{}]", path, state, type);
            }
        });
    }
}
