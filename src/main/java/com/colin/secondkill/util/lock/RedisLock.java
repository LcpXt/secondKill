package com.colin.secondkill.util.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * 2024年07月13日上午10:01
 */
@Component
public class RedisLock {

    private static final String doLockLua = "if (redis.call(\"exists\", KEYS[1]) == 1) then\n" +
            "    return 0\n" +
            "else\n" +
            "    redis.call(\"set\", KEYS[1], ARGV[1])\n" +
            "    redis.call(\"expire\", KEYS[1], 5)\n" +
            "    return 1\n" +
            "end";

    private static final String deleteLockLua = "local lockValue = redis.call(\"get\", KEYS[1])\n" +
            "\n" +
            "if (lockValue == ARGV[1]) then\n" +
            "    redis.call(\"del\", KEYS[1])\n" +
            "end";


//    public void lock(Integer goodsId){
//        Jedis resource = jedisPool.getResource();
//        String randomValue = "1-" + UUID.randomUUID();
//        //如果没有锁
//        if (!resource.exists("lock-" +goodsId)){
//            resource.set("lock-" +goodsId, randomValue);
//            //为了解决长时间不释放锁的死锁问题
//            resource.expire("lock-" +goodsId, 4);
//            //
//            if (resource.get("lock-" +goodsId).equals(randomValue)){
//                //解决因为网络延迟等原因，导致当前的线程删了别的线程的锁，并发下混乱不堪
//                resource.del("lock-" +goodsId);
//            }
//        }else {
//            System.out.println("其他线程正在操作数据");
//        }
//    }

    public Long doLock(Jedis connection, List<String> keyList, List<String> valueList){
        String executeLua = connection.scriptLoad(doLockLua);
        return (Long) connection.evalsha(executeLua, keyList, valueList);
    }

    public void deleteLock(Jedis connection, List<String> keyList, List<String> valueList) {
        String executeLua = connection.scriptLoad(deleteLockLua);
        connection.evalsha(executeLua, keyList, valueList);
    }

}
