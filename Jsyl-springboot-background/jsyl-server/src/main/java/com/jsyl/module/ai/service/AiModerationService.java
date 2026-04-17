package com.jsyl.module.ai.service;

/**
 * AI 审核业务封装接口
 */
public interface AiModerationService {

    /**
     * 审核帖子内容
     * @param title 帖子标题
     * @param content 帖子正文
     * @throws IllegalArgumentException 如果内容审核不通过
     */
    void moderatePost(String title, String content);
}
