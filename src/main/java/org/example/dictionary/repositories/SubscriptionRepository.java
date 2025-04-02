package org.example.dictionary.repositories;

import org.example.dictionary.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
}