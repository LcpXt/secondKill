package com.colin.secondkill.service;

import com.colin.secondkill.bean.User;
import com.colin.secondkill.exception.NullFileException;
import com.colin.secondkill.exception.ReadWriteFileException;
import com.colin.secondkill.util.response.ResponseResult;
import org.springframework.http.HttpRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 2024年03月23日17:01
 */
public interface UserService {

    ResponseResult<String> checkUsername(String username);

    ResponseResult<String> sendEmail(String email, Integer flag);

    ResponseResult<String> checkCode(String code, String email);

    Boolean doRegister(User user);

    boolean changePassword(String email, String password);

    User doLogin(String username, String password);

    ResponseResult<String> headImgUpload(MultipartFile multipartFile, HttpSession session, String md5) throws IOException, NullFileException, ReadWriteFileException;

    ResponseResult<String> updatePersonalInfo(User user, HttpSession session);
}
