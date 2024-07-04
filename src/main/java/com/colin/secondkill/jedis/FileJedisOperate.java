package com.colin.secondkill.jedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 2024年06月09日16:54
 */
@Component
public class FileJedisOperate {
    @Autowired
    private JedisPool jedisPool;

    /**
     * 根据文件md5值 在zset集合中得到 作为分数的文件id
     * 如果不存在 那么返回0
     * 如果存在 返回文件id
     * @param md5
     * @return
     */
    public int getFileId(String md5) {
        try (Jedis connection = jedisPool.getResource()) {
            Long md5Index = connection.zrank("md5FileCache", md5);
            if (md5Index == null) {
                return 0;
            }

            Double fileId = connection.zscore("md5FileCache", md5);
            return fileId.intValue();
        }

    }
}
