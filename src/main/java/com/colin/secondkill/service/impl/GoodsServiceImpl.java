package com.colin.secondkill.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.SecondKillGoods;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.service.GoodsService;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.util.TokenUtil;
import com.colin.secondkill.util.lock.RedisLock;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
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
    /**
     * 服务器id
     */
    @Value("${my.project.param.serverId}")
    private String serverId;

    @Override
    @Transactional
    public ResponseResult<Order> doSecondKill(int secondKillGoodsId, String longToken) throws UnsupportedEncodingException {
        String jsonUser = TokenUtil.getJSONUserByLongToken(longToken, jedisPool);
        User user = JSONObject.parseObject(jsonUser, User.class);
        //判断秒杀商品是否在活动时间内
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        SecondKillGoods secondKillGoods = goodsMapper.getSecondKillGoodsById(secondKillGoodsId);
        if (currentTime.before(secondKillGoods.getStartTime()) || currentTime.after(secondKillGoods.getEndTime())){
            return new ResponseResult<>(
                    Status.ERROR,
                    "不在活动时间内",
                    null
            );
        }
        //判断是否限购
        Jedis resource = jedisPool.getResource();
        if (resource.get("secondKillGoodsId-" + secondKillGoodsId + "userId-" + user.getId()) != null){
            return new ResponseResult<>(
                    Status.PURCHASE_LIMIT,
                    "秒杀商品每人限购一次",
                    null
            );
        }
        //内存标记，true表示库存已经没了
        if (map.get("secondKillGoods-" + secondKillGoodsId)){
            return new ResponseResult<>(
                    Status.INSUFFICIENT_INVENTORY,
                    "商品库存不足，秒杀失败",
                    null
            );
        }
        String[] keyArr = {"lock-" + secondKillGoodsId};
        List<String> keyList = Arrays.asList(keyArr);
        // 第三个参数 是作为redis操作的value的 list
        String randomValue = serverId + "-" + UUID.randomUUID();
        String[] valueArr = {randomValue};
        List<String> valueList = Arrays.asList(valueArr);

        ResponseResult<Order> result;


        //1.商品库存扣减
        if (redisLock.doLock(resource, keyList, valueList) == 1){
            long i = resource.decr("secondKillGoods-" + secondKillGoodsId);
            //2.下订单，返回商品信息
            if (i >= 0){
                Order order = orderService.createSecondKillOrder(secondKillGoodsId, user);
                result = new ResponseResult<>(
                        Status.ORDERING_IN_PROGRESS,
                        "下单中",
                        order
                );
            }else {
                map.put("secondKillGoods-" + secondKillGoodsId, true);
                return new ResponseResult<>(
                        Status.INSUFFICIENT_INVENTORY,
                        "商品库存不足，秒杀失败",
                        null
                );
            }

            redisLock.deleteLock(resource, keyList, valueList);
        }else{
            log.error("未获取到锁");
            result = new ResponseResult<>(
                    Status.ERROR,
                    "异常火爆 秒杀失败",
                    null
            );
        }
        resource.close();
        return result;
    }

    @Override
    public ResponseResult<String> deleteGoods(Integer goodsId) {
        ResponseResult<String> responseResult = new ResponseResult<>(
                Status.ERROR,
                "删除失败",
                null
        );
        if(goodsMapper.selectSecondKillGoodsByGoodsId(goodsId) != null) {
            if (goodsMapper.deleteGoodsById(goodsId) != 0 && goodsMapper.deleteSKGoodsByGoodsId(goodsId) != 0) {
                responseResult.setStatus(Status.SUCCESS);
                responseResult.setMessage("删除成功");
            }
        }else if (goodsMapper.deleteGoodsById(goodsId) != 0){
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("删除成功");
        }
        return responseResult;
    }

    @Override
    public ResponseResult<String> updateGoodsInfo(Goods goods) {
        ResponseResult<String> responseResult = new ResponseResult<>(
                Status.ERROR,
                "修改商品信息失败",
                null
        );
        goodsMapper.updateGoodsById(goods);
        return null;
    }

    /**
     * 服务启动时把库存量放入redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<SecondKillGoods> secondKillGoodsList = goodsMapper.selectSecondKillGoods();
        Jedis resource = jedisPool.getResource();

        for (SecondKillGoods secondKillGoods : secondKillGoodsList) {
            resource.set("secondKillGoods-" + secondKillGoods.getId(), secondKillGoods.getInventory() + "");
            map.put("secondKillGoods-" + secondKillGoods.getId(), false);
        }
        resource.close();
    }
}
