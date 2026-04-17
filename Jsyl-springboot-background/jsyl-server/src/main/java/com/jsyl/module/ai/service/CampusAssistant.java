package com.jsyl.module.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CampusAssistant {

    @SystemMessage("""
        你是校园服务平台的智能助手"小园"。
        你可以帮助用户查询订单等信息。
        回答简洁友好，使用中文。
        """)
    String chat(@MemoryId Long userId, @UserMessage String message);
}