package com.colin.secondkill.exception.user;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-27
 * {@code @time} 11:19
 */
public class AccountErrorException extends UserException{
    private static final long serialVersionUID = -8376550581889153348L;

    public AccountErrorException(String message) {
        super(message);
    }
}
