package com.colin.secondkill.service;

import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.model.OrderGoodsDTO;
import com.colin.secondkill.util.response.ResponseResult;
import com.github.pagehelper.PageInfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 2024年07月06日下午3:01
 */
public interface OrderService {
    Order createSecondKillOrder(Integer goodsId, User user) throws UnsupportedEncodingException;

    List<Order> getAllOrdersByUserId(Integer userId);

//    PageInfo<OrderGoodsDTO> findPaginated(int page, int size, User user);

    List<OrderGoodsDTO> findPaginated(List<OrderGoodsDTO> orderGoodsDTOs, int page, int size);

    ResponseResult<String> getOrder(Integer secondKillGoodsId, String longToken) throws UnsupportedEncodingException;

    ResponseResult<String> doPay(Order order);
}
