package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.SecondKillGoods;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2024年07月06日下午3:21
 */
@Repository
public interface GoodsMapper {
    int selectGoodsInventoryById(int goodsId);

    void updateGoodsInventoryById(int goodsId);

    Goods selectGoodsById(int goodsId);

    List<SecondKillGoods> selectSecondKillGoods();

    Goods getGoodsByGoodsId(Integer goodsId);

    List<Goods> getAllGoods();

    List<SecondKillGoods> getAllSecondKillGoods();

    int deleteGoodsById(Integer goodsId);

    int deleteSKGoodsByGoodsId(Integer goodsId);

    SecondKillGoods selectSecondKillGoodsByGoodsId(Integer goodsId);

    void updateGoodsById(Goods goods);

    SecondKillGoods getSecondKillGoodsById(int secondKillGoodsId);

    int getSecondKillGoodsIdByGoodsId(Integer goodsId);
}
