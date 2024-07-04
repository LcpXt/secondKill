package com.colin.secondkill.exception.user;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-27
 * {@code @time} 11:19
 */
public class UserException extends RuntimeException{
    private static final long serialVersionUID = -3786435577345447047L;

    public UserException(String message) {
        super(message);
    }
}
