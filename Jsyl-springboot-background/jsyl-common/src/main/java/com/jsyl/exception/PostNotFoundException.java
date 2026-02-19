package com.jsyl.exception;

/**
 * 帖子不存在异常
 */
public class PostNotFoundException extends BaseException {

    public PostNotFoundException() {
    }

    public PostNotFoundException(String msg) {
        super(msg);
    }

}
