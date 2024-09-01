package com.colin.secondkill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 2024年07月19日上午8:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondKillGoods {
    /**
     * 商品id
     */
    private Integer id;
    /**
     * 对应的商品表的id
     */
    private Integer goodsId;
    /**
     * 商品名称
     */
//    private String name;
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
    /**
     * 秒杀开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startTime;
    /**
     * 秒杀结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endTime;

    private SecondKillGoods(SecondKillGoods.SecondKillGoodsBuilder secondKillGoodsBuilder) {
        this.id = secondKillGoodsBuilder.id;
        this.goodsId = secondKillGoodsBuilder.goodsId;
//        this.name = secondKillGoodsBuilder.name;
        this.price = secondKillGoodsBuilder.price;
        this.img = secondKillGoodsBuilder.img;
        this.inventory = secondKillGoodsBuilder.inventory;
        this.startTime = secondKillGoodsBuilder.startTime;
        this.endTime = secondKillGoodsBuilder.endTime;
    }

    public static class SecondKillGoodsBuilder{
        private Integer id;
        private Integer goodsId;
//        private String name;
        private BigDecimal price;
        private String img;
        private Integer inventory;
        private Timestamp startTime;
        private Timestamp endTime;
        public SecondKillGoodsBuilder id(Integer id){
            this.id = id;
            return this;
        }
        public SecondKillGoodsBuilder goodsId(Integer goodsId){
            this.goodsId = goodsId;
            return this;
        }
//        public SecondKillGoodsBuilder name(String name){
//            this.name = name;
//            return this;
//        }
        public SecondKillGoodsBuilder price(BigDecimal price){
            this.price = price;
            return this;
        }
        public SecondKillGoodsBuilder img(String img){
            this.img = img;
            return this;
        }
        public SecondKillGoodsBuilder inventory(Integer inventory){
            this.inventory = inventory;
            return this;
        }
        public SecondKillGoodsBuilder startTime(Timestamp startTime){
            this.startTime = startTime;
            return this;
        }
        public SecondKillGoodsBuilder endTime(Timestamp endTime){
            this.endTime = endTime;
            return this;
        }
        public SecondKillGoods build(){
            return new SecondKillGoods(this);
        }
    }
}

