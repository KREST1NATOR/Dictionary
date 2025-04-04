package org.example.dictionary.callback;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CallbackMessage implements Serializable {
    private static final long serialVersionUID = 1L; // Добавляем версию

    private String callbackUrl;
    private String accessToken;
    private String key;
    private String value;
    private LocalDateTime operationTimestamp;

    public CallbackMessage() {}

    public CallbackMessage(String callbackUrl, String accessToken, String key, String value, LocalDateTime operationTimestamp) {
        this.callbackUrl = callbackUrl;
        this.accessToken = accessToken;
        this.key = key;
        this.value = value;
        this.operationTimestamp = operationTimestamp;
    }

    // Геттеры и сеттеры
    public String getCallbackUrl() {
        return callbackUrl;
    }
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public LocalDateTime getOperationTimestamp() {
        return operationTimestamp;
    }
    public void setOperationTimestamp(LocalDateTime operationTimestamp) {
        this.operationTimestamp = operationTimestamp;
    }

    @Override
    public String toString() {
        return "CallbackMessage{" +
                "callbackUrl='" + callbackUrl + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", operationTimestamp=" + operationTimestamp +
                '}';
    }
}