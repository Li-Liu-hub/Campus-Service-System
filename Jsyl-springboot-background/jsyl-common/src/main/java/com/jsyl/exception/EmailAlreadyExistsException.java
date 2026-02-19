package com.jsyl.exception;

/**
 * 邮箱已存在异常
 */
public class EmailAlreadyExistsException extends BaseException {

    public EmailAlreadyExistsException() {
    }

    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }

}
