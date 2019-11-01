package site.itcp.core.lock;


import org.apache.zookeeper.ZooKeeper;

/**
 * 分布式锁
 * @author ccoke
 */
public class ZookeeperDistributedLockTemplate implements DistributedLockTemplate {
    private final Long retryAwait;
    private final Long timeout;
    private final ZooKeeper zooKeeper;

    public ZookeeperDistributedLockTemplate(Long retryAwait, Long timeout, ZooKeeper zooKeeper) {
        this.retryAwait = retryAwait;
        this.timeout = timeout;
        this.zooKeeper = zooKeeper;
    }

    /**
     *
     * @param lockId 锁id(对应业务唯一ID)
     * @param timeout 单位毫秒
     * @param callback 回调函数
     * @return
     */
    @Override
    public Object execute(String lockId, long timeout, Callback callback) {
        return null;
    }

}
