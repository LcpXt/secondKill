package com.colin.secondkill.controller;

import com.colin.secondkill.annotation.LoginStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 2024年07月03日上午10:43
 */
@Controller
public class RouterController {

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }
    @RequestMapping("/toForgetPassword")
    public String toForgetPassword() {
        return "forgetPassword";
    }

    @RequestMapping("/toChangePassword/{email}")
    public String toChangePassword(
            @PathVariable("email") String email,
            Model model
    ) {
        model.addAttribute("email", email);
        return "changePassword";
    }



}
