package org.example.dictionary.callback;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CallbackListener {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);

    public CallbackListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = "callbackQueue")
    public void handleCallback(CallbackMessage callbackMessage) {
        log.info("Получено сообщение из очереди: {}", callbackMessage);
        try {
            restTemplate.postForEntity(callbackMessage.getCallbackUrl(), callbackMessage, Void.class);
            log.info("Коллбэк успешно отправлен на {}", callbackMessage.getCallbackUrl());
        } catch (Exception e) {
            log.error("Ошибка при отправке коллбэка на {}: {}", callbackMessage.getCallbackUrl(), e.getMessage(), e);
        }
    }
}