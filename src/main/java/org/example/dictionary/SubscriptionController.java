package org.example.dictionary;

import org.example.dictionary.entities.SubscriptionEntity;
import org.example.dictionary.repositories.SubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dictionaries/callbacks/subscriptions")
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<SubscriptionEntity> subscribe(@RequestBody SubscriptionEntity subscription) {
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        return ResponseEntity.ok(savedSubscription);
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestParam UUID subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
        return ResponseEntity.noContent().build();
    }
}