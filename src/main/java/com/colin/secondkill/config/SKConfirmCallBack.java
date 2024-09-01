package com.colin.secondkill.config;

import com.rabbitmq.client.ConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 2024年07月22日上午9:15
 */
@Component
@Slf4j
public class SKConfirmCallBack implements RabbitTemplate.ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //可靠发送 回调方法
        if (!ack){
            log.error("生产者发送消息出现异常，消息的全局id：{}", correlationData.getId());
            log.error("异常原因：{}", cause);
            Message message = correlationData.getReturnedMessage();
            byte[] body = message.getBody();
            log.error("此次发送消息内容：{}", new String(body, StandardCharsets.UTF_8));

            rabbitTemplate.convertAndSend("backup.order.queue", "backup.order", message, correlationData);
        }else {
            log.info("OK");
        }
    }
}
