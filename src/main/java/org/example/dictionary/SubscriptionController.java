package org.example.dictionary;

import org.example.dictionary.entities.SubscriptionEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dictionaries/callbacks/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @PostMapping
    public ResponseEntity<SubscriptionEntity> subscribe(@RequestBody SubscriptionEntity subscription) {
        SubscriptionEntity savedSubscription = subscriptionService.subscribe(subscription);
        return ResponseEntity.ok(savedSubscription);
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestParam UUID subscriptionId) {
        subscriptionService.unsubscribe(subscriptionId);
        return ResponseEntity.noContent().build();
    }
}