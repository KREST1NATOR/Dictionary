package org.example.dictionary.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String callbackUrl;

    @Column(nullable = false)
    private String accessToken;

    public SubscriptionEntity() {}

    public SubscriptionEntity(String callbackUrl, String accessToken) {
        this.callbackUrl = callbackUrl;
        this.accessToken = accessToken;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
}