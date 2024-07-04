package com.colin.secondkill.exception.email;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-27
 * {@code @time} 10:15
 */
public class CheckEmailException extends EmailException{
    private static final long serialVersionUID = -5398727080922511304L;

    public CheckEmailException(String message) {
        super(message);
    }
}
