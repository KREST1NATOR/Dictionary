package org.example.dictionary.callback;

import org.example.dictionary.callback.CallbackMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CallbackService {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);

    public CallbackService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCallback(CallbackMessage callbackMessage) {
        log.info(">> Sending callback message: {}", callbackMessage);
        rabbitTemplate.convertAndSend("callbackExchange", "callbackRoutingKey", callbackMessage);
    }
}