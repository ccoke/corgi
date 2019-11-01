package site.itcp.tools.zookeeper.service;

import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.itcp.tools.zookeeper.config.ZookeeperProperties;

import java.util.*;

@Log4j2
@Service
public class ZookeeperService {

    private final ZooKeeper zooKeeper;
    private final ZookeeperProperties zookeeperProperties;

    @Autowired
    public ZookeeperService(ZookeeperProperties zookeeperProperties, ZooKeeper zooKeeper) {
        this.zookeeperProperties = zookeeperProperties;
        this.zooKeeper = zooKeeper;
    }

    /**
     * 更新微服务配置
     *
     * @param serverNames 服务名称
     */
    public Map<Object, Object> updateServers(Set<String> serverNames) {
        Map<String, Map<String, String>> serverMap = loadServices();
        if (serverMap.isEmpty()) {
            return Map.of("success", false, "message", "未从配置文件中[" + zookeeperProperties.getFile() + "]读取到服务参数");
        }
        if (serverNames.isEmpty()) {
            // 如果服务列表为空，更新所有服务配置
            serverNames.addAll(serverMap.keySet());
        }
        // 公共配置
        String publicRoot = zookeeperProperties.getPublicRoot();
        // 加入公共配置
        serverNames.add(publicRoot);
        // 公共配置参数
        Map<String, String> publicPropertyMap = serverMap.get(publicRoot);
        // 配置文件根目录
        String root = zookeeperProperties.getRoot();
        // 运行环境
        String profiles = publicPropertyMap.get("spring.profiles.active");
        try {
            // 开启事务
            Transaction transaction = zooKeeper.transaction();
            // 创建配置文件根目录
            if (zooKeeper.exists(root, true) == null) {
                transaction.create(root, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            for (String serverName : serverNames) {
                // 路径
                String separator = "/";
                StringBuffer pathStringBuffer = new StringBuffer()
                        .append(root)
                        .append(separator)
                        .append(serverName);
                // 非公共节点，需要添加环境标识
                if (!serverName.equals(publicRoot)) {
                    pathStringBuffer.append(zookeeperProperties.getProfileSeparator()).append(profiles);
                }
                String nodesPath = pathStringBuffer.toString();
                // 根据路径创建多层节点
                if (zooKeeper.exists(nodesPath, true) == null) {
                    transaction.create(nodesPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                String nodesPath2 = pathStringBuffer.append(separator).toString();
                Map<String, String> propertyMap = serverMap.get(serverName);
                // 创建配置
                for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    String nodePath = nodesPath2 + entry.getKey();
                    Stat stat = zooKeeper.exists(nodePath, true);
                    String newValue = Optional.ofNullable(entry.getValue()).orElse("");
                    if (stat == null) {
                        // 新建
                        transaction.create(nodePath, newValue.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    } else {
                        byte[] value = zooKeeper.getData(nodePath, true, stat);
                        String oldValue = value != null && value.length != 0 ? new String(value) : "";
                       if (!newValue.equals(oldValue)) {
                           // 更新
                           transaction.setData(nodePath, newValue.getBytes(), stat.getVersion());
                       }
                    }
                }
            }
            transaction.commit();
        } catch (Exception e) {
            log.error("创建节点失败", e);
            return Map.of("success", false, "message", "创建节点失败");
        }

        return Map.of("success", true);
    }


    /**
     * 获取服务配置
     */
    private Map<String, Map<String, String>> loadServices() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new FileSystemResource(zookeeperProperties.getFile()));
        Properties properties = factoryBean.getObject();
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        Map<String, Map<String, String>> services = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries) {
            String mapKey = (String) entry.getKey();
            int firstIndex = mapKey.indexOf(".");
            String key = mapKey.substring(0, firstIndex);
            services.computeIfAbsent(key, k -> new HashMap<>());
            // TODO 只考虑到了String的情况
            services.get(key).put(mapKey.substring(firstIndex + 1), entry.getValue().toString());
        }
        return services;
    }
}
