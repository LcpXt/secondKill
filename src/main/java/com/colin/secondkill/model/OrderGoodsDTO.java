package com.colin.secondkill.model;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2024年07月16日下午3:22
 * 用于关联商品和订单
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderGoodsDTO {

    /**
     * 订单
     */
    private Order order;
    /**
     * 商品
     */
    private Goods goods;

}
