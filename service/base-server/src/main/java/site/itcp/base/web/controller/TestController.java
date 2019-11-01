package site.itcp.base.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;
import site.itcp.core.lock.Callback;
import site.itcp.core.lock.DistributedLockTemplate;
import site.itcp.core.lock.DistributedLockAutoConfiguration;

import java.lang.reflect.Field;

@RestController
public class TestController {
    @Autowired
    private RedisConnectionFactory connectionFactory;


    @GetMapping("/hello")
    public String hello() {
        /*Field pool = ReflectionUtils.findField(connectionFactory.getClass(), "pool");
        ReflectionUtils.makeAccessible(pool);
        JedisPool jedisPool = (JedisPool) ReflectionUtils.getField(pool, connectionFactory);
        DistributedLockTemplate distributedLockTemplate = DistributedLockAutoConfiguration.distributedLockTemplate(1500, 1500, jedisPool);
        distributedLockTemplate.execute("hello", 1500, new Callback() {
            @Override
            public Object onGetLock() throws InterruptedException {
                System.out.println("hello");
                return null;
            }

            @Override
            public Object onTimeout() throws InterruptedException {
                System.out.println("out");
                return null;
            }
        });*/
        return "hello";
    }

}
