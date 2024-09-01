package com.colin.secondkill.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.mapper.OrderMapper;
import com.colin.secondkill.model.OrderGoodsDTO;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.util.TokenUtil;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

/**
 * 2024年07月06日下午3:02
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 服务器Id
     */
    @Value("${my.project.param.serverId}")
    private String serverId;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createSecondKillOrder(Integer secondKillGoodsId, User user) throws UnsupportedEncodingException {

        Jedis resource = jedisPool.getResource();
        Order order = new Order.OrderBuilder()
                    .id(null)
                    .userId(user)
                    .goodsId(secondKillGoodsId)
                    .status(0)
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .payTime(null)
                    .build();
        String jsonOrder = JSONObject.toJSONString(order);
        //生产者的可靠发送
        MessageProperties messageProperties = new MessageProperties();
        Message message = new Message(jsonOrder.getBytes(StandardCharsets.UTF_8), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        String messageId = serverId + "-" + UUID.randomUUID();
        correlationData.setId(messageId);
        correlationData.setReturnedMessage(message);

        rabbitTemplate.convertAndSend("direct.order.exchange", "direct.order", message);
        resource.close();
        return order;
    }

    @Override
    public List<Order> getAllOrdersByUserId(Integer userId) {
        return orderMapper.getAllOrdersByUserId(userId);
    }

    @Override
    public List<OrderGoodsDTO> findPaginated(List<OrderGoodsDTO> orderGoodsDTOs, int page, int size) {
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, orderGoodsDTOs.size());
        return orderGoodsDTOs.subList(fromIndex, toIndex);
    }

    @Override
    public ResponseResult<String> getOrder(Integer secondKillGoodsId, String longToken) throws UnsupportedEncodingException {
        Jedis resource = jedisPool.getResource();
        String jsonUser = TokenUtil.getJSONUserByLongToken(longToken, jedisPool);
        User user = JSONObject.parseObject(jsonUser, User.class);
        ResponseResult<String> responseResult = new ResponseResult<>(
                Status.ORDER_FAILED,
                "下单失败",
                null
        );
        if(Objects.equals(resource.get("secondKillGoodsId-" + secondKillGoodsId + "userId-" + user.getId()), "1")){
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("下单成功，请在30分钟内前往订单页面支付");
            //下单成功就把value变成2，1表示第一次买，后续判断是2的话就是限购
            resource.incr("secondKillGoodsId-" + secondKillGoodsId + "userId-" + user.getId());
        }else {
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("秒杀商品每人限购一次");
        }
        resource.close();
        return responseResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> doPay(Order order) {
        ResponseResult<String> responseResult = new ResponseResult<>(
                Status.PAYMENT_FAILED,
                "支付失败",
                null
        );
        try {
            order.setPayTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(1);
            orderMapper.updateOrder(order);
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("支付成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseResult;
    }


}
