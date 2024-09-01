package com.colin.secondkill.bean;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 2024年07月06日上午9:17
 */
@Data
@NoArgsConstructor
public class Order {
    /**
     * 订单id
     */
    private Integer id;
    /**
     * 下单用户
     */
    @JSONField(serializeFeatures = {JSONWriter.Feature.WriteNulls})
    private User user;
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 订单当前状态
     */
    private Integer status;
    /**
     * 订单创建时间
     */
    private Timestamp createTime;
    /**
     * 订单支付时间
     */
    @JSONField(serializeFeatures = {JSONWriter.Feature.WriteNulls})
    private Timestamp payTime;

    private Order(OrderBuilder orderBuilder){
        this.id = orderBuilder.id;
        this.user = orderBuilder.user;
        this.goodsId = orderBuilder.goodsId;
        this.status = orderBuilder.status;
        this.createTime = orderBuilder.createTime;
        this.payTime = orderBuilder.payTime;
    }

    public static class OrderBuilder {
        private Integer id;
        private User user;
        private Integer goodsId;
        private Integer status;
        private Timestamp createTime;
        private Timestamp payTime;
        public OrderBuilder id(Integer id) {
            this.id = id;
            return this;
        }
        public OrderBuilder userId(User user) {
            this.user = user;
            return this;
        }
        public OrderBuilder goodsId(Integer goodsId) {
            this.goodsId = goodsId;
            return this;
        }
        public OrderBuilder status(Integer status) {
            this.status = status;
            return this;
        }
        public OrderBuilder createTime(Timestamp createTime) {
            this.createTime = createTime;
            return this;
        }
        public OrderBuilder payTime(Timestamp payTime) {
            this.payTime = payTime;
            return this;
        }
        public Order build() {
            return new Order(this);
        }
    }
}
