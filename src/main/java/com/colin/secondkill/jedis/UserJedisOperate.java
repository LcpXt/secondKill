package com.colin.secondkill.jedis;


import com.colin.secondkill.exception.email.CodeCheckException;
import com.colin.secondkill.exception.email.CodeGeneralException;
import com.colin.secondkill.exception.email.EmailException;
import com.colin.secondkill.util.response.ResponseResult;
import com.colin.secondkill.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 2024年05月15日19:11
 */
@Component
public class UserJedisOperate {

    @Autowired
    private JedisPool jedisPool;

    @Value("${my.project.param.code.expiration-time}")
    private Integer expirationTime;

    public String generateCode(String email, String code) {
        try (Jedis resource = jedisPool.getResource()) {

            // 在注册表单中 验证验证码是否发送过
            if (resource.exists(email)) {
                return "error";
            }

            resource.setex(email, this.expirationTime, code);
            return "success";

        } catch (Exception e) {
            throw new CodeGeneralException("验证码生成错误");
        }
    }

    public ResponseResult<String> checkCode(String code, String email) {
        try (Jedis resource = jedisPool.getResource()){
            ResponseResult<String> responseResult = new ResponseResult<>(Status.ERROR, "验证码错误", null);
            if(!resource.get(email).equals(code)){
                return responseResult;
            }
            responseResult.setStatus(Status.SUCCESS);
            responseResult.setMessage("验证码正确");
            return responseResult;
        }catch (Exception e){
            throw new CodeCheckException("验证码校验错误");
        }
    }

    public void delCache(String email) {
        try (Jedis resource = jedisPool.getResource()){
            resource.del(email);
        }catch (Exception e){
            throw new CodeCheckException("验证码校验错误");
        }
    }

    public void cacheEmail(String email) {
        try (Jedis resource = jedisPool.getResource()){
            resource.sadd("emailCache", email);
        }catch (Exception e){
            throw new EmailException("邮箱缓存错误");
        }
    }

    public boolean checkEmailExists(String email) {
        try (Jedis resource = jedisPool.getResource()){
            return resource.sismember("emailCache", email);
        }catch (Exception e){
            throw new EmailException("邮箱缓存错误");
        }
    }
}
