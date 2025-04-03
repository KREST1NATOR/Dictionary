package org.example.dictionary;

import org.springframework.context.ApplicationEvent;

public class DictionaryEvent extends ApplicationEvent {
    private final String action;
    private final String key;
    private final String value;
    private final String dictionaryName;

    public DictionaryEvent(Object source, String action, String key, String value, String dictionaryName) {
        super(source);
        this.action = action;
        this.key = key;
        this.value = value;
        this.dictionaryName = dictionaryName;
    }

    public String getAction() {
        return action;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }
}