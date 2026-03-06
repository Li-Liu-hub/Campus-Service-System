package com.jsyl.service;

import com.jsyl.entity.SensitiveWord;

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
