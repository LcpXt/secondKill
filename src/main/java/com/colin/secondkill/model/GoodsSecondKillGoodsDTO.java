package com.colin.secondkill.model;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.SecondKillGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2024年07月19日上午9:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSecondKillGoodsDTO {
    /**
     * 普通商品
     */
    private Goods goods;
    /**
     * 秒杀商品
     */
    private SecondKillGoods secondKillGoods;
}
