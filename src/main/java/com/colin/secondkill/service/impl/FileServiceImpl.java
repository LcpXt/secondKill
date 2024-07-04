package com.colin.secondkill.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.HeadImg;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.jedis.FileJedisOperate;
import com.colin.secondkill.mapper.FileMapper;
import com.colin.secondkill.mapper.UserMapper;
import com.colin.secondkill.service.FileService;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpSession;

/**
 * 2024年06月07日19:49
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private ResponseResult<String> responseResult;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileJedisOperate fileJedisOperate;


    /**
     * 检查缓存的md5值是否存在，存在返回相应文件信息
     * 不存在返回一个结果
     *
     * @param md5
     * @param session
     * @return
     */
    @Override
    @Transactional
    public ResponseResult<String> checkFileMD5(String md5, HttpSession session) {
//        Jedis connection = jedisPool.getResource();
//        Long md5Index = connection.zrank("md5FileCache", md5);
//        if (md5Index == null) {
//            responseResult.setStatus(Status.FILE_NOT_EXISTS);
//            responseResult.setMessage("文件不存在，允许上传");
//            return responseResult;
//        }
//        Double fileId = connection.zscore("md5FileCache", md5);
//        String s = fileId + "";
//        String subString = s.substring(0, s.lastIndexOf("."));
//        int id = Integer.parseInt(subString);
        int fileId = 0;
        if ((fileId = fileJedisOperate.getFileId(md5)) == 0) {
            responseResult.setStatus(Status.FILE_NOT_EXISTS);
            responseResult.setMessage("文件可以上传");
            return responseResult;
        }
        HeadImg headImg =  fileMapper.selectFileById(fileId);
        //1、就算服务端存在文件，但是当前用户的意思是要修改自己的，因此也要改动
        User loginUser = (User) session.getAttribute("loginUser");
        loginUser.setHeadImg(headImg.getMappingPath());
        session.setAttribute("loginUser", loginUser);
        //2、改动user表中绑定的head_img
        userMapper.updateHeadImgById(loginUser.getId(), headImg.getMappingPath());
        responseResult.setStatus(Status.FILE_EXISTS);
        responseResult.setMessage("文件已存在，返回服务端文件信息");
        responseResult.setData(JSONObject.toJSONString(headImg));
        return responseResult;
    }
}
