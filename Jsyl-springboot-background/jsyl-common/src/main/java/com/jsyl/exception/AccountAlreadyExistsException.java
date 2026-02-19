package com.jsyl.exception;

/**
 * 账号已存在异常
 */
public class AccountAlreadyExistsException extends BaseException {

    public AccountAlreadyExistsException() {
    }

    public AccountAlreadyExistsException(String msg) {
        super(msg);
    }

}
