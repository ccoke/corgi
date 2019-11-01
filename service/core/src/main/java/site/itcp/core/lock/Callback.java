package site.itcp.core.lock;

public interface Callback {

    /**
     * 获取锁要做的事情
     */
    Object onGetLock() throws InterruptedException;

    /**
     * 超时要做的事情
     */
    Object onTimeout() throws InterruptedException;
}
