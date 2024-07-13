package com.colin.secondkill.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.mapper.OrderMapper;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.jws.soap.SOAPBinding;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

/**
 * 2024年07月06日下午3:02
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order createSecondKillOrder(Integer goodsId, String longToken) throws UnsupportedEncodingException {

        Jedis resource = jedisPool.getResource();
        String jsonUser = TokenUtil.getJSONUserByLongToken(longToken, jedisPool);
        User user = JSONObject.parseObject(jsonUser, User.class);
        Order order = new Order.OrderBuilder()
                    .id(null)
                    .userId(user)
                    .goodsId(goodsId)
                    .status(0)
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .payTime(null)
                    .build();
        //向订单表中添加这条订单数据
        int orderId = orderMapper.insertOrder(order);
        order.setId(orderId);
        //当前订单是秒杀订单，再单独向秒杀订单表提交这条订单数据
        orderMapper.insertSecondKillOrder(order);
        resource.close();
        return order;
    }
}
