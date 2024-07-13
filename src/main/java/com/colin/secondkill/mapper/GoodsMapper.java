package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.Goods;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2024年07月06日下午3:21
 */
@Repository
public interface GoodsMapper {
    int selectGoodsInventoryById(int goodsId);

    void updateGoodsInwentoryById(int goodsId);

    Goods selectGoodsById(int goodsId);

    List<Goods> selectSecondKillGoods();
}
