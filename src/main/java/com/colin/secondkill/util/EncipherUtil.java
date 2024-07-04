package com.colin.secondkill.util;

import cn.hutool.crypto.digest.MD5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 2024年05月16日19:41
 */
@Component
public class EncipherUtil {

    @Value("${my.project.param.salt}")
    private String salt;

    public String doEncipher(String originalPassword){
        MD5 md5 = MD5.create();
        String first = this.salt + "$" + md5.digestHex16(originalPassword);
        return md5.digestHex(first);
    }
}
