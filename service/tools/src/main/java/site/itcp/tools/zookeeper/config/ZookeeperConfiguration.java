package site.itcp.tools.zookeeper.config;

import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Log4j2
@Configuration
public class ZookeeperConfiguration {

    private final ZookeeperProperties zookeeperProperties;

    @Autowired
    public ZookeeperConfiguration(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    @Bean
    public ZooKeeper getZookeeper() throws IOException {
        return new ZooKeeper(zookeeperProperties.getConnectString(), zookeeperProperties.getSessionTimeOut(), event -> {
            // 监听类型
            Watcher.Event.EventType type = event.getType();
            if(Watcher.Event.EventType.None == type){
                // 监听路径
                String path = event.getPath();
                // 监听状态
                Watcher.Event.KeeperState state = event.getState();
                log.info("zookeeper已连接: 路径[{}], 状态[{}], 类型[{}]", path, state, type);
            }
        });
    }
}
