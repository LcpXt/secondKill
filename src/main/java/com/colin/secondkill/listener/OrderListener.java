package com.colin.secondkill.listener;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * 2024年07月14日下午3:06
 */
@Component
public class OrderListener {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 服务器Id
     */
    @Value("${my.project.param.serverId}")
    private String serverId;

    /**
     * 监听延迟队列，进行订单的超时处理
     * @param jsonOrder
     * @param channel
     */
    @RabbitListener(queues = "delay.queue", ackMode = "MANUAL")
    @Transactional(rollbackFor = Exception.class)
    public void delayListen(String jsonOrder, Channel channel, Message message) throws IOException {
        System.out.println(LocalDateTime.now());
        Jedis resource = null;
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag, false);
            resource = jedisPool.getResource();
            Order order = JSONObject.parseObject(jsonOrder, Order.class);
            int orderId = order.getId();
            //这个订单的当前状态
            Order currentOrder = orderMapper.selectOrderByOrderId(orderId);
            System.out.println(order);
            //TODO 如果状态是未支付，就让他超时
            if (currentOrder.getStatus() == 0){
                currentOrder.setStatus(2);
                orderMapper.updateOrder(currentOrder);
                //TODO 超时要把库存加回去
                int secondKillGoodsId = goodsMapper.getSecondKillGoodsIdByGoodsId(order.getGoodsId());
                resource.incr("secondKillGoods-" + secondKillGoodsId);
                //TODO 取消这个商品的限购
                resource.del("secondKillGoodsId-" + order.getGoodsId() + "userId-" + order.getUser().getId());
            }
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            System.out.println(e);
            throw new RuntimeException(e);
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
//        resource.close();

    }

    @RabbitListener(queues = "direct.order.queue",ackMode = "AUTO")
    @Transactional(rollbackFor = {Exception.class})
    public void directListen(String jsonOrder, Channel channel,Message message) throws IOException {
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        try {
            Jedis resource = jedisPool.getResource();
            Order order = JSONObject.parseObject(jsonOrder, Order.class);
            //向订单表中添加这条订单数据
            orderMapper.insertOrder(order);
//            int orderId = order.getId();
            //当前订单是秒杀订单，再单独向秒杀订单表提交这条订单数据
            orderMapper.insertSecondKillOrder(order);
            jsonOrder = JSONObject.toJSONString(order);
        System.out.println(jsonOrder);
//            channel.basicAck(deliveryTag, false);
            //生产者可靠发送
            MessageProperties messageProperties = new MessageProperties();
            message = new Message(jsonOrder.getBytes(StandardCharsets.UTF_8), messageProperties);
            CorrelationData correlationData = new CorrelationData();
            String messageId = serverId + "-" + UUID.randomUUID();
            correlationData.setId(messageId);
            correlationData.setReturnedMessage(message);
            //消费者消费以后把这条消息放入延迟队列，等待三十分钟后检查是否过期
//        System.out.println(LocalDateTime.now());
            rabbitTemplate.convertAndSend("order.exchange", "order", message);
            //提交订单以后，用redis中的唯一标识限购
            resource.set("secondKillGoodsId-" + order.getGoodsId() + "userId-" + order.getUser().getId(), 1 + "");
            resource.close();
//        } catch (IOException | AmqpException e) {
//            channel.basicNack(deliveryTag, false, false);
//            throw new RuntimeException(e);
//        }
    }

}
