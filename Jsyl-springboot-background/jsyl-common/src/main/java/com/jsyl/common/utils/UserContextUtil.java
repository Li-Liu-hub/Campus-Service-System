package com.jsyl.common.utils;

import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.exception.UserNotLoginException;

public class UserContextUtil {

    public static Integer getCurrentUserId(){
        Long userIdLong = getCurrentUserIdLong();
        return userIdLong.intValue();
    }
    public static Long getCurrentUserIdLong(){
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }
        return userIdLong;
    }
}
