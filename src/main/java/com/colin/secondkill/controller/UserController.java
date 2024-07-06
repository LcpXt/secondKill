package com.colin.secondkill.controller;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.annotation.LoginStatus;
import com.colin.secondkill.exception.NullFileException;
import com.colin.secondkill.exception.ReadWriteFileException;
import com.colin.secondkill.mapper.UserMapper;
import com.colin.secondkill.util.response.ResponseResult;
import org.springframework.beans.factory.InitializingBean;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 2024年07月03日上午10:22
 */
@Controller
@RequestMapping("/user")
public class UserController implements InitializingBean {

    @Autowired
    private UserService userService;
    @Qualifier("userMapper")
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisPool jedisPool;
    private List<Integer> ageList;

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.ageList = new ArrayList<Integer>();
        for (int i = 17; i <= 61; i++){
            this.ageList.add(i);
        }
    }
    @RequestMapping("/getLoginUserInfo")
    @LoginStatus
    public String getLoginUserInfo(Model model, @CookieValue("longToken") String longToken) throws UnsupportedEncodingException {
        User user = userService.getLoginUserInfo(longToken);
        model.addAttribute("loginUser", user);
        model.addAttribute("ageList", this.ageList);
        return "personalCenter";
    }
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
        if ((user = userService.doLogin(username, password, response)) == null){
            model.addAttribute("fail", true);
            return "login";
        }
        model.addAttribute("loginUser", user);
        //如果一次接口或者视图的跳转没有用到Request域对象或者Model，能用重定向就用重定向
        //因为用户可能刷新浏览器
        //防止因为地址栏不变导致的表单重复提交
        return "home";
    }
    @RequestMapping("/logout")
    @LoginStatus
    public String logout(@CookieValue("shortToken") String shortToken,
                         @CookieValue("longToken") String longToken,
                         HttpServletResponse response) throws UnsupportedEncodingException {
        userService.logOut(shortToken, longToken, response);
        return "redirect:/toLogin";
    }
    @RequestMapping("updatePersonalInfo")
    @ResponseBody
    @LoginStatus
    public ResponseResult<String> updatePersonalInfo(@RequestBody User user,
                                                     @CookieValue("shortToken") String shortToken,
                                                     @CookieValue("longToken") String longToken){
        return userService.updatePersonalInfo(user, shortToken, longToken);
    }
    @RequestMapping("/headImgUpload")
    @ResponseBody
    @LoginStatus
    public ResponseResult<String> headImgLoad(@RequestParam("headImg") MultipartFile headImg,
                                              @RequestParam("md5") String md5,
                                              @CookieValue("shortToken") String shortToken,
                                              @CookieValue("longToken") String longToken) throws IOException, NullFileException, ReadWriteFileException {
        return userService.headImgUpload(headImg, shortToken,longToken, md5);
    }
}
