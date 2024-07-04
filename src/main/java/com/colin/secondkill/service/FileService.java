package com.colin.secondkill.service;

import com.colin.secondkill.util.response.ResponseResult;


import javax.servlet.http.HttpSession;

/**
 * 2024年06月07日19:49
 */
public interface FileService {


    ResponseResult<String> checkFileMD5(String md5, HttpSession session);
}
