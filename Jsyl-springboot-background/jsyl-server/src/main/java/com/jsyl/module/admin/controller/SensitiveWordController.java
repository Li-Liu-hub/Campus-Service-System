package com.jsyl.module.admin.controller;

import com.jsyl.model.admin.entity.SensitiveWord;
import com.jsyl.common.result.Result;
import com.jsyl.module.admin.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/admin/sensitive-word")
@Slf4j
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping
    public Result<List<SensitiveWord>> getAllWords() {
        List<SensitiveWord> words = sensitiveWordService.getAllWords();
        return Result.success(words);
    }

    @PostMapping
    public Result<String> addWord(@RequestBody SensitiveWord word) {
        sensitiveWordService.addWord(word);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateWord(@RequestBody SensitiveWord word) {
        sensitiveWordService.updateWord(word);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteWord(@PathVariable Long id) {
        sensitiveWordService.deleteWord(id);
        return Result.success("删除成功");
    }

    @PostMapping("/reload")
    public Result<String> reloadWords() {
        sensitiveWordService.initSensitiveWords();
        return Result.success("重新加载成功");
    }
}
