package com.colin.secondkill.controller;

import com.colin.secondkill.annotation.LoginStatus;
import com.colin.secondkill.service.FileService;
import com.colin.secondkill.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * 2024年06月07日19:45
 */
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping("/file/checkFileMD5/{md5}")
    @ResponseBody
    @LoginStatus
    public ResponseResult<String> checkFileMD5(@PathVariable String md5,
                                               @CookieValue("shortToken") String shortToken,
                                               @CookieValue("longToken") String longToken) throws UnsupportedEncodingException {
        return fileService.checkFileMD5(md5, shortToken, longToken);
    }
}
