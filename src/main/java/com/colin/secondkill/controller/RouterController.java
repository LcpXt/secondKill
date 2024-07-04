package com.colin.secondkill.controller;

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
public class RouterController implements InitializingBean {
    private List<Integer> ageList;

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
    @RequestMapping("/toPersonalCenter")
    public String toPersonal(Model model) {
        model.addAttribute("ageList", ageList);
        return "personalCenter";
    }


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
}
