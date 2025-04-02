package org.example.dictionary;

import org.springframework.context.ApplicationEvent;

public class DictionaryEvent extends ApplicationEvent {
    private final String action;
    private final String key;
    private final String dictionaryName;

    public DictionaryEvent(Object source, String action, String key, String dictionaryName) {
        super(source);
        this.action = action;
        this.key = key;
        this.dictionaryName = dictionaryName;
    }

    public String getAction() {
        return action;
    }

    public String getKey() {
        return key;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }
}