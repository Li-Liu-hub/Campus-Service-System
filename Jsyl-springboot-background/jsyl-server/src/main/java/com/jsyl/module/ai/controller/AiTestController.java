package com.jsyl.module.ai.controller;

import com.jsyl.common.context.BaseContext;
import com.jsyl.common.exception.ModerationRejectException;
import com.jsyl.common.result.Result;
import com.jsyl.module.ai.service.AiModerationService;
import com.jsyl.module.ai.service.CampusAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiTestController {

    @Autowired
    private ChatLanguageModel chatLanguageModel;
    @Autowired
    private AiModerationService aiModerationService;
    @Autowired
    private CampusAssistant campusAssistant;

    @GetMapping("/chat")
    public Result<String> chat(@RequestParam(defaultValue = "你好") String message) {
        String answer = chatLanguageModel.chat(message);
        return Result.success(answer);
    }

    @PostMapping("/moderate")
    public Result<Map<String, Object>> moderate(@RequestParam String title,
                                                 @RequestParam String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            aiModerationService.moderatePost(title, content);
            result.put("pass", true);
            result.put("reason", "审核通过");
        } catch (ModerationRejectException e) {
            result.put("pass", false);
            result.put("reason", e.getMessage());
        }
        return Result.success(result);
    }
    @PostMapping("/assistant")
    public Result<String> assistant(@RequestParam String message) {
        Long userId = BaseContext.getCurrentId();
        String answer = campusAssistant.chat(userId, message);
        answer = answer.replaceAll("(?s)<think>.*?</think>", "").trim();
        return Result.success(answer);
    }
}
