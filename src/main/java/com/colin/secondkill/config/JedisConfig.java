package com.colin.secondkill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 2024年07月02日下午8:17
 */
@Configuration
public class JedisConfig {
    @Bean
    public JedisPool jedisPool() {
        return new JedisPool();
    }

//    public static void main(String[] args) {
//        Jedis jedis = new Jedis("117.78.8.44", 6379);
//        System.out.println(jedis.ping());
//    }
}
