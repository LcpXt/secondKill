package com.colin.secondkill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 2024年07月06日上午9:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    /**
     * 商品id
     */
    private Integer id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品图片路径
     */
    private String img;
    /**
     * 商品库存
     */
    private Integer inventory;
//    /**
//     * 秒杀开始时间
//     */
//    private Timestamp startTime;
//    /**
//     * 秒杀结束时间
//     */
//    private Timestamp endTime;

    private Goods(GoodsBuilder goodsBuilder) {
        this.id = goodsBuilder.id;
        this.name = goodsBuilder.name;
        this.price = goodsBuilder.price;
        this.img = goodsBuilder.img;
        this.inventory = goodsBuilder.inventory;
//        this.startTime = goodsBuilder.startTime;
//        this.endTime = goodsBuilder.endTime;
    }

    public static class GoodsBuilder{
        private Integer id;
        private String name;
        private BigDecimal price;
        private String img;
        private Integer inventory;
        private Timestamp startTime;
        private Timestamp endTime;
        public GoodsBuilder id(Integer id){
            this.id = id;
            return this;
        }
        public GoodsBuilder name(String name){
            this.name = name;
            return this;
        }
        public GoodsBuilder price(BigDecimal price){
            this.price = price;
            return this;
        }
        public GoodsBuilder img(String img){
            this.img = img;
            return this;
        }
        public GoodsBuilder inventory(Integer inventory){
            this.inventory = inventory;
            return this;
        }
//        public GoodsBuilder startTime(Timestamp startTime){
//            this.startTime = startTime;
//            return this;
//        }
//        public GoodsBuilder endTime(Timestamp endTime){
//            this.endTime = endTime;
//            return this;
//        }
        public Goods build(){
            return new Goods(this);
        }
    }
}
