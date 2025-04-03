package org.example.dictionary;

import org.example.dictionary.entities.SubscriptionEntity;
import org.example.dictionary.repositories.SubscriptionRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
            String callbackUrl = subscription.getCallbackUrl();
            String accessToken = subscription.getAccessToken();

            // Формируем заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("access-token", accessToken);

            // Формируем тело запроса
            Map<String, Object> requestBody = Map.of(
                    "key", event.getKey(),
                    "value", event.getValue(),
                    "operationTimestamp", Instant.now().toString()
            );

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                restTemplate.postForEntity(callbackUrl, requestEntity, Void.class);
            } catch (Exception e) {
                System.err.println("Ошибка при отправке коллбэка: " + e.getMessage());
            }
        }
    }
}