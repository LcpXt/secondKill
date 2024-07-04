package com.colin.secondkill.controller;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.exception.NullFileException;
import com.colin.secondkill.exception.ReadWriteFileException;
import com.colin.secondkill.mapper.UserMapper;
import com.colin.secondkill.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.colin.secondkill.service.UserService;
import com.colin.secondkill.bean.User;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.ws.Response;
import java.io.IOException;
import java.util.UUID;

/**
 * 2024年07月03日上午10:22
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Qualifier("userMapper")
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/checkUsername/{username}")
    @ResponseBody
    public ResponseResult<String> checkUsername(
            @PathVariable("username") String username
    ) {
        return userService.checkUsername(username);
    }

    @RequestMapping("/sendEmail/{email}/{flag}")
    @ResponseBody
    public ResponseResult<String> sendEmail(
            @PathVariable("email") String email,
            @PathVariable("flag") Integer flag
    ) {
        return userService.sendEmail(email, flag);
    }

    @RequestMapping("/checkCode/{code}/{email}")
    @ResponseBody
    public ResponseResult<String> checkCode(
            @PathVariable("code") String code,
            @PathVariable("email") String email
    ) {
        return userService.checkCode(code, email);
    }

    @RequestMapping("/doRegister")
    public String doRegister(@Valid User user, Model model) {
        if (!userService.doRegister(user)) {
            model.addAttribute("fail", null);
            return "register";
        } else {
            model.addAttribute("success", true);
            return "register";
        }
    }
    @RequestMapping("/changePassword/{email}")
    public String changePassword(
            @PathVariable("email") String email,
            @RequestParam("password") String password,
            Model model
    ) {
        if (userService.changePassword(email, password)) {
            model.addAttribute("changePasswordSuccess", "true");
        } else {
            model.addAttribute("changePasswordError", "true");
        }
        return "changePassword";
    }

    /**
     * 分布式系统用户登录三个框架
     * spring security
     * shiro
     * sa-token
     * @param username
     * @param password
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            HttpServletResponse response,
            Model model
    ) {
        MD5 md5 = MD5.create();
        long ttl = System.currentTimeMillis() + (20 * 60 * 1000);
        User user = null;
        if ((user = userService.doLogin(username, password)) == null){
            model.addAttribute("fail", true);
            return "login";
        }
        //生成短token，生成后只作为cookie发到客户端存储
        String prefix = user.getId() + "-" +ttl;
        String shortToken = prefix + "-" + md5.digestHex16(prefix);
        Cookie shortCookie = new Cookie("shortToken", shortToken);
        shortCookie.setMaxAge(20 * 60 * 1000);
        response.addCookie(shortCookie);

        //生成长token，生成后作为永不过期的cookie发到客户端，并且同时存到redis中，设置过期时间为30天
        String uuid = UUID.randomUUID().toString();
        String longCookieId = uuid + 8000 +System.currentTimeMillis();
        String signature = md5.digestHex16(longCookieId);
        String longToken = longCookieId + "-" + signature;

        Cookie longCookie = new Cookie("longToken", longToken);
        longCookie.setMaxAge(-1);
        response.addCookie(longCookie);
        Jedis resource = jedisPool.getResource();
        resource.setex(longCookieId, 30 * 24 * 60 * 60, JSONObject.toJSONString(user));
        resource.close();

        session.setAttribute("loginUser", user);
        //如果一次接口或者视图的跳转没有用到Request域对象或者Model，能用重定向就用重定向
        //因为用户可能刷新浏览器
        //防止因为地址栏不变导致的表单重复提交
        return "redirect:/video/getHomeVideos";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/toLogin";
    }
    @RequestMapping("updatePersonalInfo")
    @ResponseBody
    public ResponseResult<String> updatePersonalInfo(@RequestBody User user, HttpSession session){
        return userService.updatePersonalInfo(user, session);
    }
    @RequestMapping("/headImgUpload")
    @ResponseBody
    public ResponseResult<String> headImgLoad(@RequestParam("headImg") MultipartFile headImg,
                                              @RequestParam("md5") String md5,
                                              HttpSession session) throws IOException, NullFileException, ReadWriteFileException {
        return userService.headImgUpload(headImg, session, md5);
    }
}
