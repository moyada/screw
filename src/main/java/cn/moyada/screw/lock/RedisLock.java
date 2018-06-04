package cn.moyada.screw.lock;

import cn.moyada.screw.utils.CommonUtil;
import cn.moyada.screw.yaml.YamlAnalyzer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xueyikang
 * @create 2018-06-05 02:23
 */
public class RedisLock implements DistributionLock {

    private final Jedis jedis;
    private final JedisCluster jedisCluster;

    public RedisLock() {
        this.jedis = new Jedis("115.29.10.121", 6379);

        Set<HostAndPort> hostAndPortSet = getConfig("/redis.yaml");
//        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        this.jedisCluster = new JedisCluster(hostAndPortSet, 1000, 1000, 1, null, new JedisPoolConfig());
    }

    private static String SERVICE = "service";
    private static String HOST = "host";
    private static String PORT = "port";

    @SuppressWarnings("unchecked")
    protected static Set<HostAndPort> getConfig(String redisConf) {
        Map<String, Object> configMap = YamlAnalyzer.analyze(redisConf);
        List<Map<String, String>> services = (List)configMap.get(SERVICE);

        Set<HostAndPort> hostAndPortSet = new HashSet<>(services.size(), 1);

        services.forEach(item -> {
            String host = item.get(HOST);
            Integer port = Integer.valueOf(item.get(PORT));
            HostAndPort hostAndPort = new HostAndPort(host, port);
            hostAndPortSet.add(hostAndPort);
        });

        return hostAndPortSet;
    }

    private static final String LOCK_SUCCESS ="OK";
    private static final String SET_IF_NOT_EXIST ="NX";
    private static final String SET_IF_ALREADY_EXIST ="XX";
    private static final String EXPIRE_TIME_MILLI_SECONDS ="PX";
    private static final String EXPIRE_TIME_SECONDS ="EX";

    private static final long expireTime = 3000L;
    private static final Map<String, String> requestMap = new ConcurrentHashMap<>();

    /**
     * 尝试获取分布式锁
     * @param key 锁
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(String key) {
        String requestId = CommonUtil.getUUID();
        String result = jedisCluster.set(key, requestId, SET_IF_NOT_EXIST, EXPIRE_TIME_MILLI_SECONDS, expireTime);
        if(LOCK_SUCCESS.equals(result)) {
            requestMap.put(key, requestId);
            return true;
        }
        return false;
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 释放分布式锁
     * @param key 锁
     * @return 是否释放成功
     */
    @Override
    public boolean release(String key) {
        String requestId = requestMap.get(key);
        if(null == requestId) {
            return false;
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedisCluster.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        if(RELEASE_SUCCESS.equals(result)) {
            requestMap.remove(key);
            return true;
        }
        return false;
    }
}
