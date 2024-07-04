package com.colin.secondkill.exception.email;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-27
 * {@code @time} 10:44
 */
public class EmailException extends RuntimeException{
    private static final long serialVersionUID = -3682914747056992884L;

    public EmailException(String message) {
        super(message);
    }
}
