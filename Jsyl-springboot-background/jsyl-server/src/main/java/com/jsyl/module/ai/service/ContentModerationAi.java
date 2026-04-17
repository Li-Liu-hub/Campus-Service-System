package com.jsyl.module.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ContentModerationAi {

    record ModerationResult(boolean pass, String reason) {}

    @SystemMessage("""
        你是一个内容审核助手，负责审核校园服务平台的用户发布内容。
        判断内容是否包含以下违规信息：色情、暴力、广告引流、政治敏感、人身攻击、违法信息。
        """)
    @UserMessage("请审核以下内容：\n标题：{{title}}\n正文：{{content}}")
    ModerationResult moderate(@V("title") String title, @V("content") String content);
}