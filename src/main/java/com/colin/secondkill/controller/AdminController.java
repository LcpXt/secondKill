package com.colin.secondkill.controller;

import com.colin.secondkill.bean.Admin;
import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.SecondKillGoods;
import com.colin.secondkill.mapper.AdminMapper;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.model.GoodsSecondKillGoodsDTO;
import com.colin.secondkill.model.OrderGoodsDTO;
import com.colin.secondkill.service.AdminService;
import com.colin.secondkill.service.GoodsService;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2024年07月13日下午7:06
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam("adminName")String adminName,
                          @RequestParam("password")String adminPassword,
                          HttpSession session,
                          Model model,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size) {
        Admin admin = null;
        if ((admin = adminService.doLogin(adminName,adminPassword)) == null){
            model.addAttribute("fail", true);
            return "admin/adminLogin";
        }
        List<Goods> goodsList = goodsMapper.getAllGoods();
        session.setAttribute("goodsList", goodsList);
        session.setAttribute("currentPageOfGoods", page);
        session.setAttribute("totalPagesOfGoods", (goodsList.size() + size - 1) / size);
        session.setAttribute("admin", admin);
        return "admin/adminHome";
    }
    @RequestMapping("/getGoodsInfo")
    public String getGoodsInfo(HttpSession session){
        List<Goods> goodsList = goodsMapper.getAllGoods();
        session.setAttribute("goodsList", goodsList);
        return "admin/adminHome";
    }
    @RequestMapping("/updateGoodsInfo")
    @ResponseBody
    public ResponseResult<String> updateGoodsInfo(@RequestBody Goods goods){
        return goodsService.updateGoodsInfo(goods);
    }

    @RequestMapping("/secondKillGoodsManagement")
    public String secondKillGoodsManagement(Model model,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        List<SecondKillGoods> secondKillGoodsList = goodsMapper.getAllSecondKillGoods();
        List<GoodsSecondKillGoodsDTO> goodsSecondKillGoodsDTOList = new ArrayList<>();
        for(int i = secondKillGoodsList.size() - 1; i >= 0; i--){
            GoodsSecondKillGoodsDTO goodsSecondKillGoodsDTO = new GoodsSecondKillGoodsDTO();
            SecondKillGoods secondKillGoods = secondKillGoodsList.get(i);
            Integer goodsId = secondKillGoods.getGoodsId();
            Goods goods = goodsMapper.getGoodsByGoodsId(goodsId);
            goodsSecondKillGoodsDTO.setGoods(goods);
            goodsSecondKillGoodsDTO.setSecondKillGoods(secondKillGoods);
            goodsSecondKillGoodsDTOList.add(goodsSecondKillGoodsDTO);
        }
        model.addAttribute("goodsSecondKillGoodsDTOList", goodsSecondKillGoodsDTOList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (goodsSecondKillGoodsDTOList.size() + size - 1) / size);
        return "admin/adminSecondKillHome";
    }

    @RequestMapping("/deleteGoods/{goodsId}")
    @Transactional
    @ResponseBody
    public ResponseResult<String> deleteGoods(@PathVariable("goodsId") Integer goodsId) {
        return goodsService.deleteGoods(goodsId);
    }

}
