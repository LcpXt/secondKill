package com.colin.secondkill.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
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
        return longTokenId;
    }
}
