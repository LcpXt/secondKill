package com.colin.secondkill.service;

import com.colin.secondkill.bean.Order;
import com.colin.secondkill.util.response.ResponseResult;

import java.io.UnsupportedEncodingException;

/**
 * 2024年07月06日下午3:06
 */
public interface GoodsService {
    ResponseResult<Order> doSecondKill(int goodsId, String longToken) throws UnsupportedEncodingException;
}
