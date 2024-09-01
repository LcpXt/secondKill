package com.colin.secondkill.controller;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.annotation.LoginStatus;
import com.colin.secondkill.bean.Goods;
import com.colin.secondkill.bean.Order;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.mapper.GoodsMapper;
import com.colin.secondkill.model.OrderGoodsDTO;
import com.colin.secondkill.service.OrderService;
import com.colin.secondkill.service.UserService;
import com.colin.secondkill.util.response.ResponseResult;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 2024年07月06日上午9:26
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsMapper goodsMapper;

    @RequestMapping("/myOrder")
    @LoginStatus
    public String myOrder(@CookieValue("longToken") String longToken, Model model,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size) throws UnsupportedEncodingException {
        User user = userService.getLoginUserInfo(longToken);
//        PageInfo<OrderGoodsDTO> pageInfo = orderService.findPaginated(page,size,user);
        List<Order> orderList = orderService.getAllOrdersByUserId(user.getId());
        List<OrderGoodsDTO> allOrderGoodsDTOList = new ArrayList<>();
        for (int i = orderList.size() - 1; i >= 0; i--) {
            OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
            Order order = orderList.get(i);
            Integer goodsId = order.getGoodsId();
            Goods goods = goodsMapper.getGoodsByGoodsId(goodsId);
            orderGoodsDTO.setGoods(goods);
            orderGoodsDTO.setOrder(order);
            allOrderGoodsDTOList.add(orderGoodsDTO);
        }
        List<OrderGoodsDTO> orderGoodsDTOList = orderService.findPaginated(allOrderGoodsDTOList, page, size);
//        model.addAttribute("pageInfo", pageInfo);
        String jsonUser = JSONObject.toJSONString(user);
        model.addAttribute("jsonUser", jsonUser);
        model.addAttribute("loginUser", user);

        model.addAttribute("orderDTOList", orderGoodsDTOList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (allOrderGoodsDTOList.size() + size - 1) / size);
        return "order";
    }
    @RequestMapping("/getOrder/{secondKillGoodsId}")
    @ResponseBody
    @LoginStatus
    public ResponseResult<String> getOrder(@PathVariable("secondKillGoodsId") Integer secondKillGoodsId,
                                           @CookieValue("longToken")String longToken) throws UnsupportedEncodingException {
        return orderService.getOrder(secondKillGoodsId, longToken);
    }
    @RequestMapping("/doPay")
    @LoginStatus
    @ResponseBody
    public ResponseResult<String> doPay(@RequestBody Order order) throws UnsupportedEncodingException {
        return orderService.doPay(order);
    }
}
