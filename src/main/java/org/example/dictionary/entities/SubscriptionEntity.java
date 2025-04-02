package org.example.dictionary.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  // Уникальный идентификатор подписки

    @Column(nullable = false)
    private String callbackUrl;  // URL, на который будем отправлять уведомления

    public SubscriptionEntity() {}

    public SubscriptionEntity(String callbackUrl) {
        this.callbackUrl = callbackUrl;
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
}