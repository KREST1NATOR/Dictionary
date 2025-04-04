package org.example.dictionary.callback;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue callbackQueue() {
        return QueueBuilder.durable("callbackQueue")
                .deadLetterExchange("callbackExchange.dlx")
                .build();
    }

    @Bean
    public DirectExchange callbackExchange() {
        return new DirectExchange("callbackExchange");
    }

    @Bean
    public Binding binding(Queue callbackQueue, DirectExchange callbackExchange) {
        return BindingBuilder.bind(callbackQueue).to(callbackExchange).with("callbackRoutingKey");
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("callbackQueue.dlq").build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("callbackExchange.dlx");
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("callbackRoutingKey");
    }
}