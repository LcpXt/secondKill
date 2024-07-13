package com.colin.secondkill.service.impl;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.service.GoodsService;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.util.lock.RedisLock;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 2024年07月06日下午3:07
 */
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService , InitializingBean {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private Map<String, Boolean> map;
    @Autowired
    private RedisLock redisLock;

    @Override
    @Transactional
    public ResponseResult<Order> doSecondKill(int goodsId, String longToken) throws UnsupportedEncodingException {

        if (map.get("goods-" + goodsId)){
            return new ResponseResult<>(
                    Status.INSUFFICIENT_INVENTORY,
                    "商品库存不足",
                    null
            );
        }

        Jedis resource = jedisPool.getResource();
        String[] keyArr = {"lock-" + goodsId};
        List<String> keyList = Arrays.asList(keyArr);
        // 第三个参数 是作为redis操作的value的 list
        String randomValue = "1-" + UUID.randomUUID();
        String[] valueArr = {randomValue};
        List<String> valueList = Arrays.asList(valueArr);

        ResponseResult<Order> result;

        //1.商品库存扣减
        if (redisLock.doLock(resource, keyList, valueList) == 1){
            this.decrRedisInventory(goodsId);
            //2.下订单，返回商品信息
            Order order = orderService.createSecondKillOrder(goodsId, longToken);
            result = new ResponseResult<>(
                    Status.SUCCESS,
                    "秒杀商品成功",
                    order
            );
            redisLock.deleteLock(resource, keyList, valueList);
        }else{
            log.error("未获取到锁");
            result = new ResponseResult<>(
                    Status.ERROR,
                    "服务器内部异常 秒杀失败",
                    null
            );
        }
        resource.close();
        return result;
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
            map.put("goods-" + goods.getId(), false);
        }
        resource.close();
    }

    public void decrRedisInventory(Integer goodsId) {
        Jedis resource = jedisPool.getResource();
        long result = resource.decr("goods-" + goodsId);
        if (result <= 0){
            map.put("goods-" + goodsId, true);
        }
        resource.close();
    }
}
