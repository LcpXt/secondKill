package com.colin.secondkill.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.HeadImg;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.exception.NullFileException;
import com.colin.secondkill.exception.ReadWriteFileException;
import com.colin.secondkill.exception.user.UpdateUserInfoException;
import com.colin.secondkill.jedis.UserJedisOperate;
import com.colin.secondkill.mapper.FileMapper;
import com.colin.secondkill.mapper.UserMapper;
import com.colin.secondkill.service.UserService;
import com.colin.secondkill.util.EncipherUtil;
import com.colin.secondkill.util.FileUtils;
import com.colin.secondkill.util.MailUtils;
import com.colin.secondkill.util.TokenUtil;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2024年03月23日17:01
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserJedisOperate userJedisOperate;
    @Autowired
    private EncipherUtil encipherUtil;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private ResponseResult<String> responseResult;
    @Value("${my.project.user.head-img.default-mapping-path}")
    private String defaultImgPath;
    @Value("${my.project.user.head-img.resource-location}")
    private String headImgResourceLocationPrefix;
    /**
     * 程序访问头像的虚拟路径前缀
     */
    @Value("${my.project.user.head-img.resource-handler}")
    private String headImgResourceHandlerPrefix;

    @Override
    public ResponseResult<String> checkUsername(String username) {

        Pattern compile = Pattern.compile("^[a-zA-Z0-9]{3,12}$");
        Matcher matcher = compile.matcher(username);
        ResponseResult<String> responseResult = new ResponseResult<>(Status.PATTERN_ERROR, "用户名不合法", null);
        if (!matcher.matches()){
            return responseResult;
        }
        if ( userMapper.selectIdByUsername(username) == null){
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("用户名可以使用");
            return responseResult;
        }
        responseResult.setStatus(Status.USERNAME_EXISTS);
        responseResult.setMessage("用户名已被使用");
        return responseResult;
    }

    @Override
    public ResponseResult<String> sendEmail(String email, Integer flag) {
        // 1. 校验邮箱是否符合邮箱规则
        ResponseResult<String> responseResult = new ResponseResult<>(Status.PATTERN_ERROR, "邮箱不合法", null);
        Pattern compile = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = compile.matcher(email);
        if (matcher.matches()){
            String random = Math.random() + "";
            String randomString = random.substring(2, 8);
            final boolean idExists = userJedisOperate.checkEmailExists(email);
            // 如果邮箱注册过 并且是注册请求 返回邮箱已存在
            if (idExists && flag == 0){
                responseResult.setStatus(Status.EMAIL_EXISTS);
                responseResult.setMessage("邮箱已存在");
                return responseResult;
            }
            //邮箱没注册过，并且是修改密码请求，返回邮箱未注册
            if (!idExists && flag == 1){
                responseResult.setStatus(Status.ERROR);
                responseResult.setMessage("邮箱尚未注册");
                return responseResult;
            }
            MailUtils.sendMail(email, "验证码是 : " + randomString, "验证码");
           if(userJedisOperate.generateCode(email, randomString).equals("success")){
               responseResult.setStatus(Status.SUCCESS);
               responseResult.setMessage("验证码生成成功");
           }else {
               responseResult.setStatus(Status.ERROR);
               responseResult.setMessage("验证码生成失败");
           }
            return responseResult;
        }
        return responseResult;
    }

    @Override
    public ResponseResult<String> checkCode(String code, String email) {
        return userJedisOperate.checkCode(code, email);
    }

    @Override
    public Boolean doRegister(User user) {
        //加盐加密
        final String originalPassword = user.getPassword();
        final String finalPassword = encipherUtil.doEncipher(originalPassword);
        user.setPassword(finalPassword);
        //补齐缺少属性
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setRegisterTime(timestamp);
        user.setUpdateTime(timestamp);
        user.setHeadImg(this.defaultImgPath);
        user.setUserType(0);
        //插入数据表
        if(userMapper.insertUser(user)){
            userJedisOperate.cacheEmail(user.getEmail());
            userJedisOperate.delCache(user.getEmail());
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String email, String password) {
        Boolean updateResult = false;
        Pattern compile = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = compile.matcher(email);


        if (matcher.matches()) {
            String finalPassword = encipherUtil.doEncipher(password);
            updateResult = userMapper.updatePasswordByEmail(email, finalPassword);
        }

        return updateResult;
    }

    @Override
    public User doLogin(String username, String password, HttpServletResponse response) {
        User user = null;
        final String finalPassword = encipherUtil.doEncipher(password);
        if ((user = userMapper.selectUserByUsernameAndPassword(username, finalPassword)) == null){
            return null;
        }
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        userMapper.updateLastLoginTime(currentTime, username);
        user.setUpdateTime(currentTime);
        String jsonUser = JSONObject.toJSONString(user);
        //双token缓存登录态
        String shotToken = TokenUtil.getShortToken(user.getId());
        Cookie shortTokenCookie = new Cookie("shortToken", shotToken);
        shortTokenCookie.setMaxAge(365 * 24 * 60 * 60);
        shortTokenCookie.setDomain("localhost");
        shortTokenCookie.setPath("/");
        response.addCookie(shortTokenCookie);

        String longToken = TokenUtil.getLongToken(jedisPool, jsonUser);
        Cookie longTokenCookie = new Cookie("longToken", longToken);
        longTokenCookie.setMaxAge(365 * 24 * 60 * 60);
        longTokenCookie.setDomain("localhost");
        longTokenCookie.setPath("/");
        response.addCookie(longTokenCookie);

        return user;

    }

    @Override
    @Transactional
    public ResponseResult<String> headImgUpload(MultipartFile multipartFile, String shortToken, String longToken, String md5) throws IOException, NullFileException, ReadWriteFileException {
        User loginUser = null;
        HeadImg headImg = null;
        Jedis connection = null;
        try {
            String longTokenId = TokenUtil.getLongTokenIdFromLongToken(longToken);
            connection = jedisPool.getResource();
            String jsonUser = connection.get(longTokenId);
            loginUser = JSONObject.parseObject(jsonUser, User.class);
            String username = loginUser.getUsername();
            //1.接受文件并存储
            //如果把文件存储在服务所在的主机上，一切和文件相关的路径最好都用变量声明
            String originalFilename = multipartFile.getOriginalFilename();
            InputStream inputStream = multipartFile.getInputStream();
            long currentTime = System.currentTimeMillis();
            //解决重名问题，每个用户都单独建立一个文件夹，并且在文件名后拼接上时间戳
            //解析文件名test.txt
            //获取文件中最后一个"."的索引
            //通过自己封装的文件读写方法，将文件写入本地盘符，并返回文件最终名称
            String finalFileName = FileUtils.readWriteFile(headImgResourceLocationPrefix,
                                                           username,
                                                           originalFilename,
                                                           currentTime,
                                                           inputStream);
            String suffixName = FileUtils.getSuffixName(originalFilename);
            //2.用数据表保存文件的相关信息
            //获取文件本身大小
            File file1 = new File(headImgResourceLocationPrefix + username + "/" + finalFileName);
            long length = file1.length();
            headImg = new HeadImg.HeadImgBuilder()
                    .user(loginUser)
                    .uploadTime(new Timestamp(currentTime))
                    .originalPath(headImgResourceLocationPrefix + username + "/" + finalFileName)
                    .mappingPath(headImgResourceHandlerPrefix + username + "/" + finalFileName)
                    .imgSize(length + "")
                    .imgType(suffixName.substring(1))//0位置是.，1到最后是后缀
                    .originalName(finalFileName)
                    .build();
            //基于@Transactional声明式事务回滚
            fileMapper.insertHeadImg(headImg);
            userMapper.updateHeadImgById(loginUser.getId(), headImg.getMappingPath());
            connection.zadd("md5FileCache", headImg.getId(), md5);
            loginUser.setHeadImg(headImg.getMappingPath());
            String jsonUserTemp = JSONObject.toJSONString(loginUser);
            connection.set(longTokenId, jsonUserTemp);
        } catch (Exception e) {
            File file = null;
            if (headImg != null) {
                file = new File(headImg.getOriginalPath());
            }
            if (file != null && file.exists()) {
                file.delete();
            }
            throw new RuntimeException(e);
        }finally {
            if (connection != null) {
                connection.close();
            }
        }

        //3.返回结果
        responseResult.setStatus(Status.SUCCESS);
        return responseResult;
    }

    @Override
    @Transactional
    public ResponseResult<String> updatePersonalInfo(User user, String shortToken, String longToken) {
        Jedis resource = null;
        try {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            user.setUpdateTime(currentTime);
            if (user.getPassword() != null) {
                user.setPassword(encipherUtil.doEncipher(user.getPassword()));
            }
            userMapper.updateUserById(user);
            user = userMapper.selectUserById(user.getId());
            String longTokenId = TokenUtil.getLongTokenIdFromLongToken(longToken);
            resource = jedisPool.getResource();
            resource.set(longTokenId, JSONObject.toJSONString(user));
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("修改成功");
        } catch (Exception e) {
            responseResult.setStatus(Status.ERROR);
            responseResult.setMessage("服务器异常，请重试");
            throw new UpdateUserInfoException("修改个人信息异常");
//            System.out.println(e);
        }finally {
            if (resource != null) {
                resource.close();
            }
        }
        return responseResult;
    }

    @Override
    public User getLoginUserInfo(String longToken) throws UnsupportedEncodingException {
        String longTokenId =  TokenUtil.getLongTokenIdFromLongToken(longToken);
        Jedis resource = jedisPool.getResource();
        String jsonUser = resource.get(longTokenId);
        resource.close();
        return JSONObject.parseObject(jsonUser, User.class);
    }

    @Override
    public void logOut(String shortToken, String longToken, HttpServletResponse response) throws UnsupportedEncodingException {
        // 1. 删除Redis中longToken中longTokenId的kv (该操作是用户登出逻辑的关键)
        String longTokenId = TokenUtil.getLongTokenIdFromLongToken(longToken);
        Jedis resource = jedisPool.getResource();
        resource.del(longTokenId);
        // 2. 用重名key 和 同样域的 cookie 覆盖掉客户端的short & long Token 起到删除cookie的作用
        Cookie cookie1 = new Cookie("shortToken", null);
        cookie1.setMaxAge(0);
        cookie1.setDomain("localhost");
        cookie1.setPath("/");
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("longToken", null);
        cookie2.setMaxAge(0);
        cookie2.setDomain("localhost");
        cookie2.setPath("/");
        response.addCookie(cookie2);
        resource.close();
    }

    @Override
    public ResponseResult<String> updateHeadImg(Integer userId, String mappingPath, String longTokenId) {
        if (userMapper.updateHeadImgById(userId, mappingPath)) {
            User user = userMapper.selectUserById(userId);
            Jedis resource = jedisPool.getResource();
            resource.set(longTokenId, JSONObject.toJSONString(user));
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("修改成功");
            resource.close();
            return responseResult;
        }

        responseResult.setStatus(Status.ERROR);
        responseResult.setMessage("修改失败");
        return responseResult;
    }
}