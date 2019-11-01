package site.itcp.core.lock;

public interface DistributedLockTemplate {
    Object execute(String lockId, long timeout, Callback callback);
}
