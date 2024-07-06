package com.colin.secondkill.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.misc.BASE64Decoder;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 2024年07月03日下午4:38
 */
public class TokenUtil {

    /**
     * token校验通过
     */
    public final static Integer CHECK_OK = 0;
    /**
     * token过期
     */
    public final static Integer OVER_TIME = 1;
    /**
     * token非法
     */
    public final static Integer ILLEGALITY = 2;

    public static String getShortToken(Integer id){
        MD5 md5 = MD5.create();
        long ttl = System.currentTimeMillis() + (20 * 60 * 1000);
        String prefix = id + "-" + ttl;
        return Base64.encode(prefix + "-" + md5.digestHex(prefix), "UTF-8");
    }

    public static String getLongToken(JedisPool jedisPool, String jsonUser){
        MD5 md5 = MD5.create();
        //生成长token，生成后作为永不过期的cookie发到客户端，并且同时存到redis中，设置过期时间为30天
        String uuid = UUID.randomUUID().toString();
        String longCookieId = uuid + 8000 +System.currentTimeMillis();
        String signature = md5.digestHex16(longCookieId);
        String longToken = longCookieId + "-" + signature;

        Jedis resource = jedisPool.getResource();
        resource.setex(longCookieId, 365 * 24 * 60 * 60, jsonUser);
        resource.close();
        return Base64.encode(longToken, "UTF-8");
    }

    public static Integer getIdFromShortToken(String shortToken){
        String[] shortTokenItems = getShortTokenItems(shortToken);
        return Integer.parseInt(shortTokenItems[0]);
    }
    public static Long getTTLFromShortToken(String shortToken){
        String[] shortTokenItems = getShortTokenItems(shortToken);
        return Long.parseLong(shortTokenItems[1]);
    }
    public static String getSignatureFromShortToken(String shortToken){
        String[] shortTokenItems = getShortTokenItems(shortToken);
        return shortTokenItems[2];
    }
    private static String[] getShortTokenItems(String shortToken){
        byte[] decode = Base64Decoder.decode(shortToken);
        shortToken = new String(decode, StandardCharsets.UTF_8);
        return shortToken.split("-");
    }

    public static String getLongTokenIdFromLongToken(String longToken) throws UnsupportedEncodingException {
        byte[] decode = Base64Decoder.decode(longToken);
        longToken = new String(decode, "UTF-8");
        return longToken.substring(0, longToken.lastIndexOf("-"));
    }

    /**
     * 校验长token
     * @param longToken
     * @return
     */
    public static String checkLongToken(String longToken, JedisPool jedisPool) {
        MD5 md5 = MD5.create();

        byte[] decode = Base64Decoder.decode(longToken);
        longToken = new String(decode, StandardCharsets.UTF_8);
        int index = longToken.lastIndexOf("-");
        String longTokenId = longToken.substring(0, index);
        String signature = longToken.substring(index + 1);
        String result = md5.digestHex16(longTokenId);
        if (!result.equals(signature)){
            return null;
        }
        Jedis resource = jedisPool.getResource();
        //长token虽然合法，但是在redis中已经过期了，还是需要重新登录
        //剩余时间小于10秒认为他走不完剩余逻辑，也算是过期
        if (resource.ttl(longTokenId) < 10){
            return null;
        }
        resource.close();
        return longTokenId;
    }

    public static Integer checkShortToken(String shortToken) {
        MD5 md5 = MD5.create();
        // 代码能执行到这 意味着短token不是null
        // 开始解析短token
        // split[0]=用户id
        // split[1]=短token过期时间
        // split[2]=0 + 1 进行 加密算法后得到的数字签名
        String[] split = shortToken.split("-");

        // 判断令牌是否合法
        String prefix = split[0] + "-" + split[1];
        String s = md5.digestHex(prefix);

        // 这个判断如果进去了 意味着此次客户端穿过来的token 我们拿到 id和过期时间戳 加密后算出来的值 和 客户端token的第三个部分 不一致
        // 也就是非法 那么直接返回
        if (!s.equals(split[2])) {
            return ILLEGALITY;
        }


        String ttl = split[1];
        Long l = Long.parseLong(ttl);
        // 如果短token ！= null 但是解析后发现短token已经过期 那么依赖于长token无感刷新一个新的短token 用cookie返回给客户端存储
        if (l - System.currentTimeMillis() < 0) {
            return OVER_TIME;
        }


        return CHECK_OK;
    }

    public static String getJSONUserByLongToken(String longToken, JedisPool jedisPool) throws UnsupportedEncodingException {
        Jedis resource = jedisPool.getResource();
        String longTokenIdFromLongToken = getLongTokenIdFromLongToken(longToken);
        String jsonUser = resource.get(longTokenIdFromLongToken);
        resource.close();
        return jsonUser;
    }
}
