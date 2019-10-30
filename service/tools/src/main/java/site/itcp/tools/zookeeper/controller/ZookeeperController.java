package site.itcp.tools.zookeeper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.itcp.tools.zookeeper.service.ZookeeperService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/zk")
public class ZookeeperController {

    @Autowired
    private ZookeeperService zookeeperService;

    /**
     * 更新配置
     * @param serverNames 服务名称
     */
    @PutMapping("/updateServers")
    public Map<Object, Object> updateServers(@RequestBody Set<String> serverNames) {
        return zookeeperService.updateServers(serverNames);
    }
}
