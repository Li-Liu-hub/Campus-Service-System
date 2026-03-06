package com.jsyl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SensitiveWordFilter {
    private final ResourceLoader resourceLoader;
    private final Node root = new Node();

    @Autowired
    public SensitiveWordFilter(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:sensitive-words.txt");
            if (!resource.exists()) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        addWord(word);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public SensitiveFilterResult filter(String text) {
        if (text == null || text.isEmpty()) {
            return new SensitiveFilterResult(text, false);
        }
        char[] chars = text.toCharArray();
        boolean matched = false;
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            Node node = root;
            int j = i;
            int lastMatch = -1;
            while (j < length) {
                node = node.children.get(chars[j]);
                if (node == null) {
                    break;
                }
                if (node.end) {
                    lastMatch = j;
                }
                j++;
            }
            if (lastMatch >= i) {
                matched = true;
                for (int k = i; k <= lastMatch; k++) {
                    chars[k] = '*';
                }
                i = lastMatch;
            }
        }
        return new SensitiveFilterResult(new String(chars), matched);
    }

    private void addWord(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        node.end = true;
    }

    public void initSensitiveWordMap(Set<String> words) {
        if (words == null || words.isEmpty()) {
            return;
        }
        for (String word : words) {
            addWord(word);
        }
    }

    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        SensitiveFilterResult result = filter(text);
        return result.hasSensitive();
    }

    public List<String> getSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> foundWords = new ArrayList<>();
        char[] chars = text.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            Node node = root;
            int j = i;
            StringBuilder matchedWord = new StringBuilder();
            while (j < length) {
                node = node.children.get(chars[j]);
                if (node == null) {
                    break;
                }
                matchedWord.append(chars[j]);
                if (node.end) {
                    foundWords.add(matchedWord.toString());
                }
                j++;
            }
        }
        return foundWords.stream().distinct().collect(Collectors.toList());
    }

    public String maskSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        SensitiveFilterResult result = filter(text);
        return result.getFilteredText();
    }

    private static class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private boolean end;
    }
}
