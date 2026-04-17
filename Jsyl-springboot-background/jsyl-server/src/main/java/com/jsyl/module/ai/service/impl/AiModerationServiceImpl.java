package com.jsyl.module.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsyl.common.exception.ModerationRejectException;
import com.jsyl.module.ai.service.AiModerationService;
import com.jsyl.module.ai.service.ContentModerationAi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiModerationServiceImpl implements AiModerationService {

    @Autowired
    private ContentModerationAi contentModerationAi;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void moderatePost(String title, String content) {
        try {
            ContentModerationAi.ModerationResult result = contentModerationAi.moderate(title, content);
            log.info("AI审核结果：pass={}, reason={}", result.pass(), result.reason());

            if (!result.pass()) {
                throw new ModerationRejectException("AI内容审核不通过：" + result.reason());
            }
        } catch (ModerationRejectException e) {
            throw e;
        } catch (Exception e) {
            log.warn("AI审核服务异常，跳过AI审核：{}", e.getMessage());
        }
    }
}
