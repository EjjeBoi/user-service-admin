package org.example.userserviceadmin.service;

import lombok.RequiredArgsConstructor;
import org.example.userserviceadmin.model.UserEntity;
import org.example.userserviceadmin.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.cache.CacheManager;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceAdminImpl implements UserServiceAdmin {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CacheManager cacheManager;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routing-key}")
    private String routingKey;

    @Override
    @Cacheable(value = "users", key = "#user.id")
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted());
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteUserById(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> {
            user.setDeleted(true);
            userRepository.save(user);
            rabbitTemplate.convertAndSend(exchange, routingKey, id.toString());
        });
    }

    @RabbitListener(queues = "user-deletion-queue")
    public void handleUserDeleted(Long userId) {
        cacheManager.getCache("users").evict(userId);
    }
}