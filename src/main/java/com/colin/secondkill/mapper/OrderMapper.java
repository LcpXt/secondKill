package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2024年07月06日下午3:21
 */
@Repository
public interface OrderMapper {

    int insertOrder(Order order);

    void insertSecondKillOrder(Order order);

    List<Order> getAllOrdersByUserId(Integer userId);

    Order selectOrderByOrderId(int orderId);

    void updateOrder(Order order);
}
