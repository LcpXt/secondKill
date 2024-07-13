package com.colin.secondkill.service;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;

import java.io.UnsupportedEncodingException;

/**
 * 2024年07月06日下午3:01
 */
public interface OrderService {
    Order createSecondKillOrder(Integer goodsId, String longToken) throws UnsupportedEncodingException;
}
