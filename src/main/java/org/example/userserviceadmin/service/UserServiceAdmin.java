package org.example.userserviceadmin.service;

import org.example.userserviceadmin.model.UserEntity;

import java.util.Optional;

public interface UserServiceAdmin {
    UserEntity createUser(UserEntity user);

    Optional<UserEntity> getUserById(Long Id);

    void deleteUserById(Long id);
}