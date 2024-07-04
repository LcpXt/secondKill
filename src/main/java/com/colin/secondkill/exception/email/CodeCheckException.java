package com.colin.secondkill.exception.email;

/**
 * {@code @Info}
 *
 * 验证码校验异常
 *
 * @author 777
 * {@code @date} 2024-03-26
 * {@code @time} 19:21
 */
public class CodeCheckException extends EmailException{
    private static final long serialVersionUID = -5550108257835019530L;

    public CodeCheckException(String message) {
        super(message);
    }
}
