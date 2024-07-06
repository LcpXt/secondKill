package com.colin.secondkill.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.bean.HeadImg;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.jedis.FileJedisOperate;
import com.colin.secondkill.mapper.FileMapper;
import com.colin.secondkill.mapper.UserMapper;
import com.colin.secondkill.service.FileService;
import com.colin.secondkill.util.TokenUtil;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

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
     * @param
     * @return
     */
    @Override
    @Transactional
    public ResponseResult<String> checkFileMD5(String md5, String shortToken, String longToken) throws UnsupportedEncodingException {
        int fileId = 0;
        if ((fileId = fileJedisOperate.getFileId(md5)) == 0) {
            responseResult.setStatus(Status.FILE_NOT_EXISTS);
            responseResult.setMessage("文件可以上传");
            return responseResult;
        }
        // 根据服务端已经存在的文件id (从·redis·中获取的) 去数据库得到完整的文件信息
        HeadImg headImg =  fileMapper.selectFileById(fileId);
        //1、就算服务端存在文件，但是当前用户的意思是要修改自己的，因此也要改动
        Integer userId = TokenUtil.getIdFromShortToken(shortToken);
        String longTokenId = TokenUtil.getLongTokenIdFromLongToken(longToken);
//        String jsonUser = TokenUtil.getJSONUserByLongToken(longToken, jedisPool);
//        User user = JSONObject.parseObject(jsonUser, User.class);
//
//        user.setHeadImg(headImg.getMappingPath());
//        session.setAttribute("loginUser", loginUser);
        Jedis resource = jedisPool.getResource();
        String jsonUser = resource.get(longTokenId);
        User loginUser = JSONObject.parseObject(jsonUser, User.class);
        loginUser.setHeadImg(headImg.getMappingPath());
        String jsonUserTemp = JSONObject.toJSONString(loginUser);
        resource.set(longTokenId, jsonUserTemp);
        //2、改动user表中绑定的head_img
        userMapper.updateHeadImgById(userId, headImg.getMappingPath());
        responseResult.setStatus(Status.FILE_EXISTS);
        responseResult.setMessage("文件已存在，返回服务端文件信息");
        responseResult.setData(JSONObject.toJSONString(headImg));
        resource.close();
        return responseResult;
    }
}
