package com.colin.secondkill.exception.email;

/**
 * {@code @Info}
 *
 * 验证码生成异常
 *
 * @author 777
 * {@code @date} 2024-03-26
 * {@code @time} 18:43
 */
public class CodeGeneralException extends EmailException{
    private static final long serialVersionUID = -7074364585037682711L;

    public CodeGeneralException(String message) {
        super(message);
    }
}
