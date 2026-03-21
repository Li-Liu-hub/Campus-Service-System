package com.jsyl.module.admin.service.impl;

import com.jsyl.model.admin.entity.SensitiveWord;
import com.jsyl.module.admin.mapper.SensitiveWordMapper;
import com.jsyl.module.admin.service.SensitiveWordService;
import com.jsyl.utils.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    public void init() {
        initSensitiveWords();
    }

    @Override
    public void initSensitiveWords() {
        List<SensitiveWord> words = sensitiveWordMapper.getAllEnabled();
        Set<String> wordSet = words.stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());
        sensitiveWordFilter.initSensitiveWordMap(wordSet);
    }

    @Override
    public boolean containsSensitiveWord(String text) {
        return sensitiveWordFilter.containsSensitiveWord(text);
    }

    @Override
    public List<String> getSensitiveWords(String text) {
        return sensitiveWordFilter.getSensitiveWords(text);
    }

    @Override
    public String maskSensitiveWord(String text) {
        return sensitiveWordFilter.maskSensitiveWord(text);
    }

    @Override
    public List<SensitiveWord> getAllWords() {
        return sensitiveWordMapper.getAll();
    }

    @Override
    public void addWord(SensitiveWord word) {
        Integer count = sensitiveWordMapper.countByWord(word.getWord());
        if (count > 0) {
            throw new RuntimeException("敏感词已存在");
        }
        sensitiveWordMapper.insert(word);
        initSensitiveWords();
    }

    @Override
    public void updateWord(SensitiveWord word) {
        sensitiveWordMapper.update(word);
        initSensitiveWords();
    }

    @Override
    public void deleteWord(Long id) {
        sensitiveWordMapper.delete(id);
        initSensitiveWords();
    }
}
