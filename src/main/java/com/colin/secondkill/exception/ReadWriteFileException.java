package com.colin.secondkill.exception;

/**
 * 2024年06月09日15:23
 */
public class ReadWriteFileException extends FileException{
    private static final long serialVersionUID = 4742794237089839943L;

    public ReadWriteFileException(String message) {
        super(message);
    }
}
