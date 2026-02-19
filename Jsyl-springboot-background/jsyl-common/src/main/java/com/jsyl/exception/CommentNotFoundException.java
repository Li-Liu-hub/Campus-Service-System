package com.jsyl.exception;

/**
 * 评论不存在异常
 */
public class CommentNotFoundException extends BaseException {

    public CommentNotFoundException() {
    }

    public CommentNotFoundException(String msg) {
        super(msg);
    }

}
