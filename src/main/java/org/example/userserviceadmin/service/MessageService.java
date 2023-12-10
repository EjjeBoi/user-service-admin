package org.example.userserviceadmin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final CacheManager cacheManager;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routing-key}")
    private String routingKey;

    public void sendMessage(Long id) {
        rabbitTemplate.convertAndSend(exchange, routingKey, id.toString());
    }

    @RabbitListener(queues = "user-deletion-queue")
    public void handleUserDeleted(Long id) {
        cacheManager.getCache("users").evict(id);
    }
}