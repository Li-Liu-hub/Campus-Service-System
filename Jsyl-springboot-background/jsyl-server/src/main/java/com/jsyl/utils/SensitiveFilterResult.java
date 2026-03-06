package com.jsyl.utils;

public class SensitiveFilterResult {
    private final String filteredText;
    private final boolean hasSensitive;

    public SensitiveFilterResult(String filteredText, boolean hasSensitive) {
        this.filteredText = filteredText;
        this.hasSensitive = hasSensitive;
    }

    public String getFilteredText() {
        return filteredText;
    }

    public boolean hasSensitive() {
        return hasSensitive;
    }
}
