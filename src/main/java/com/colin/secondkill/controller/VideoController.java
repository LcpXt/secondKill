package com.colin.secondkill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 2024年05月20日20:05
 */
@Controller
@RequestMapping("/video")
public class VideoController {

    @RequestMapping("/getHomeVideos")
    public String getHomeVideos(Model model){
        //势必会获取数据放入model中，所以用转发
        return "home";
    }

}
