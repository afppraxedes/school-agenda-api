package com.schoolagenda.domain.service;

import com.schoolagenda.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public List<User> findAll();
    public Optional<User> findByUsername(String username);
    public User save(User user);
    void savePushSubscription(Long userId, String subscription);
    boolean validateCredentials(String username, String password);
}
