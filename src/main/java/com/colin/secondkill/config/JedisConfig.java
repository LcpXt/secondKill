package com.colin.secondkill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}
