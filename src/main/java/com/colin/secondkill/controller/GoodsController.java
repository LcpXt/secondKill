package com.colin.secondkill.controller;

import com.colin.secondkill.annotation.LoginStatus;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.service.GoodsService;
import com.colin.secondkill.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * 2024年05月20日20:05
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/doSecondKill/{secondKillGoodsId}")
    @LoginStatus
    public ResponseResult<Order> doSecondKill(@PathVariable("secondKillGoodsId") int secondKillGoodsId,
                                              @CookieValue("longToken") String longToken) throws UnsupportedEncodingException {
        //前端用户点击进入商品详情页，详情页秒杀按钮进入此接口
        //回显给用户一个订单信息，在这个信息的基础上，准备一个支付的入口。
        return goodsService.doSecondKill(secondKillGoodsId, longToken);
    }

}
