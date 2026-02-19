package com.jsyl.exception;

/**
 * 权限拒绝异常
 */
public class PermissionDeniedException extends BaseException {

    public PermissionDeniedException() {
    }

    public PermissionDeniedException(String msg) {
        super(msg);
    }

}
