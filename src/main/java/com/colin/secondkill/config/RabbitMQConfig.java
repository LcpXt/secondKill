package com.colin.secondkill.config;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2024年07月13日下午4:45
 */
@Configuration
public class RabbitMQConfig {
    @Autowired
    private SKConfirmCallBack skConfirmCallBack;
    @Autowired
    private SKReturnCallBack skReturnCallBack;
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setHost("localhost");
        cachingConnectionFactory.setPort(5672);
        return cachingConnectionFactory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(skConfirmCallBack);
        rabbitTemplate.setReturnCallback(skReturnCallBack);
        //开returnCallBack必须声明
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }
}
