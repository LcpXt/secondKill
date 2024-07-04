package com.colin.secondkill.exception;

/**
 * 2024年06月07日16:28
 */
public class NullFileException extends FileException{
    private static final long serialVersionUID = 9079600624379242263L;

    public NullFileException(String message) {
        super(message);
    }
}
