package com.jsyl.controller.admin;

import com.jsyl.entity.SensitiveWord;
import com.jsyl.result.Result;
import com.jsyl.service.SensitiveWordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/admin/sensitive-word")
@Api(tags = "敏感词管理接口")
@Slf4j
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping
    @ApiOperation("获取所有敏感词")
    public Result<List<SensitiveWord>> getAllWords() {
        List<SensitiveWord> words = sensitiveWordService.getAllWords();
        return Result.success(words);
    }

    @PostMapping
    @ApiOperation("添加敏感词")
    public Result<String> addWord(@RequestBody SensitiveWord word) {
        sensitiveWordService.addWord(word);
        return Result.success("添加成功");
    }

    @PutMapping
    @ApiOperation("更新敏感词")
    public Result<String> updateWord(@RequestBody SensitiveWord word) {
        sensitiveWordService.updateWord(word);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除敏感词")
    public Result<String> deleteWord(@PathVariable Long id) {
        sensitiveWordService.deleteWord(id);
        return Result.success("删除成功");
    }

    @PostMapping("/reload")
    @ApiOperation("重新加载敏感词")
    public Result<String> reloadWords() {
        sensitiveWordService.initSensitiveWords();
        return Result.success("重新加载成功");
    }
}
