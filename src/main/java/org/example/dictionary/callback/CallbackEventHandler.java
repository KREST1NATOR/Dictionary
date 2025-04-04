package org.example.dictionary.callback;

import org.example.dictionary.DictionaryEvent;
import org.example.dictionary.entities.SubscriptionEntity;
import org.example.dictionary.repositories.SubscriptionRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CallbackEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallbackEventHandler.class);
    private final CallbackService callbackService;
    private final SubscriptionRepository subscriptionRepository;

    public CallbackEventHandler(CallbackService callbackService, SubscriptionRepository subscriptionRepository) {
        this.callbackService = callbackService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @EventListener
    public void handleDictionaryEvent(DictionaryEvent event) {
        logger.info(">> Received DictionaryEvent: {}", event);

        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
        for (SubscriptionEntity subscription : subscriptions) {
            CallbackMessage message = new CallbackMessage(
                    subscription.getCallbackUrl(),
                    subscription.getAccessToken(),
                    event.getKey(),
                    event.getValue(),
                    LocalDateTime.now()


            );
            logger.info(">> Sending callback message: {}", message);
            callbackService.sendCallback(message);
        }
    }
}