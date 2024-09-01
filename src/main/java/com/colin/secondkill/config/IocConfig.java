package com.colin.secondkill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 2024年07月13日上午8:54
 */
@Configuration
public class IocConfig {

    @Bean
    public Map<String, Boolean> map(){
        return new HashMap<>();
    }
}
