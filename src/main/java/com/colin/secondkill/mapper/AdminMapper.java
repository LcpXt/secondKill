package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.Admin;
import com.colin.secondkill.bean.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2024年07月18日上午9:52
 */
@Repository
public interface AdminMapper {

    Admin selectUserByUsernameAndPassword(String adminName, String password);

    List<Order> getAllOrders();
}
