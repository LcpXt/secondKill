package com.colin.secondkill.service.impl;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.service.GoodsService;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 2024年07月06日下午3:07
 */
@Service
public class GoodsServiceImpl implements GoodsService , InitializingBean {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;

    @Override
    @Transactional
    public ResponseResult<Order> doSecondKill(int goodsId, String longToken) throws UnsupportedEncodingException {

        Jedis resource = jedisPool.getResource();
        ResponseResult<Order> result = null;
        //1.商品库存扣减
        if (this.decrRedisInventory(goodsId) >= 0){
            Goods goods = goodsMapper.selectGoodsById(goodsId);
            //2.下订单，返回商品信息
            Order order = orderService.createSecondKillOrder(goods, longToken);
            result = new ResponseResult<>(
                    Status.SUCCESS,
                    "秒杀商品成功",
                    order
            );
        }else{
            result = new ResponseResult<>(
                    Status.INSUFFICIENT_INVENTORY,
                    "商品库存不足",
                    null
            );
        }
        resource.close();
        return result;


//        //1.商品库存扣减
//        //秒杀商品表的对应id的商品库存大于0才能去扣减库存
//        if (goodsMapper.selectGoodsInventoryById(goodsId) > 0){
//            goodsMapper.updateGoodsInwentoryById(goodsId);
//        }else {
//            return new ResponseResult<Order>(
//                    Status.INSUFFICIENT_INVENTORY,
//                    "商品库存不足",
//                    null
//            );
//        }
//        Goods goods = goodsMapper.selectGoodsById(goodsId);
//        //2.下订单，返回商品信息
//        Order order = orderService.createSecondKillOrder(goods, longToken);
//
//        return new ResponseResult<Order>(
//                Status.SUCCESS,
//                "秒杀商品成功",
//                order
//        );
    }

    /**
     * 服务启动时把库存量放入redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Goods> goodsList = goodsMapper.selectSecondKillGoods();
        Jedis resource = jedisPool.getResource();
        for (Goods goods : goodsList) {
            resource.set("goods-" + goods.getId(), goods.getInventory() + "");
        }
        resource.close();
    }

    public synchronized long decrRedisInventory(Integer goodsId) {
        Jedis resource = jedisPool.getResource();
        long result = resource.decr("goods-" + goodsId);
        resource.close();
        return result;
    }
}
