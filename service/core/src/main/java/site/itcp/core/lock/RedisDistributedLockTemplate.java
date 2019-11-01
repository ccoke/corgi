package site.itcp.core.lock;

import lombok.Cleanup;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 分布式锁
 * @author ccoke
 */
public class RedisDistributedLockTemplate implements DistributedLockTemplate {
    private final Long retryAwait;
    private final Long timeout;
    private final JedisPool jedisPool;

    private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};

    public RedisDistributedLockTemplate(Long retryAwait, Long timeout, JedisPool jedisPool) {
        this.retryAwait = retryAwait;
        this.timeout = timeout;
        this.jedisPool = jedisPool;
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
        boolean getLock=false;
        String value = null;
        // 尝试获取锁
        try {
            if((value = tryRedisLock(lockId, timeout, TimeUnit.MILLISECONDS)) !=null ){
                getLock=true;
                return callback.onGetLock();
            }else{
                return callback.onTimeout();
            }
        }catch (Exception e) {
            Thread.currentThread().interrupt();
        }finally {
            if(getLock) {
                unlockRedisLock(lockId, value);
            }
        }
        return null;
    }

    private String tryRedisLock(String lockId, long time, TimeUnit unit) {
        final long startMillis = System.currentTimeMillis();
        final long millisToWait = unit.toMillis(time);
        String lockValue = null;
        // 如果没有获取到锁，自旋等待
        while (lockValue == null){
            lockValue = createRedisKey(lockId);
            if(lockValue != null){
                break;
            }
            // 如果等待超时，则放弃
            if(System.currentTimeMillis() - startMillis - retryAwait > millisToWait){
                break;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
        }
        return lockValue;
    }

    private String createRedisKey(String key) {
        String value = key + randomId(1);
        String luaScript = ""
                + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1], ARGV[1]));"
                + "\nredis.call('PEXPIRE', KEYS[1], ARGV[2]);"
                + "\nreturn r";
        List<String> keys = List.of(key);
        List<String> values = List.of(value, String.valueOf(timeout));
        @Cleanup Jedis jedis = jedisPool.getResource();
        Long ret = (Long) jedis.evalsha(jedis.scriptLoad(luaScript), keys, values);
        if(ret == 1L){
            return value;
        }
        return null;
    }

    private void unlockRedisLock(String key, String value) {
        String luaScript=""
                +"\nlocal v = redis.call('GET', KEYS[1]);"
                +"\nlocal r= 0;"
                +"\nif v == ARGV[1] then"
                +"\nr =redis.call('DEL',KEYS[1]);"
                +"\nend"
                +"\nreturn r";
        List<String> keys = List.of(key);
        List<String> values = List.of(value);
        @Cleanup Jedis jedis = jedisPool.getResource();
        jedis.evalsha(jedis.scriptLoad(luaScript), keys, values);
    }

    private String randomId(int size) {
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[ThreadLocalRandom.current().nextInt(digits.length)];
        }
        return new String(cs);
    }
}
