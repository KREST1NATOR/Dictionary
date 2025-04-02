package org.example.dictionary;

import org.example.dictionary.entities.SubscriptionEntity;
import org.example.dictionary.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<SubscriptionEntity> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public SubscriptionEntity subscribe(SubscriptionEntity subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void unsubscribe(UUID subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }
}