package com.jsyl.module.admin.service;

import com.jsyl.model.admin.entity.SensitiveWord;

import java.util.List;

public interface SensitiveWordService {

    void initSensitiveWords();

    boolean containsSensitiveWord(String text);

    List<String> getSensitiveWords(String text);

    String maskSensitiveWord(String text);

    List<SensitiveWord> getAllWords();

    void addWord(SensitiveWord word);

    void updateWord(SensitiveWord word);

    void deleteWord(Long id);
}
