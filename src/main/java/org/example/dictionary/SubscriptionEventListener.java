package org.example.dictionary;

import org.example.dictionary.entities.SubscriptionEntity;
import org.example.dictionary.repositories.SubscriptionRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SubscriptionEventListener implements ApplicationListener<DictionaryEvent> {
    private final SubscriptionRepository subscriptionRepository;
    private final RestTemplate restTemplate;

    public SubscriptionEventListener(SubscriptionRepository subscriptionRepository, RestTemplate restTemplate) {
        this.subscriptionRepository = subscriptionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void onApplicationEvent(DictionaryEvent event) {
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
        for (SubscriptionEntity subscription : subscriptions) {
            try {
                restTemplate.postForEntity(subscription.getCallbackUrl(), event, Void.class);
            } catch (Exception e) {
                System.err.println("Ошибка при отправке уведомления: " + e.getMessage());
            }
        }
    }
}