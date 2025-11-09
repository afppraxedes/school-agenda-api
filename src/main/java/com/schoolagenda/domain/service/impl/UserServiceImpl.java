package com.schoolagenda.domain.service.impl;

import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        // A codificação da senha agora é feita no DataInitializer
        // Este método é mantido para consistência, mas a senha deve vir codificada
        return userRepository.save(user);
    }

    @Override
    // TODO: implemenar no "controller"!
    public void savePushSubscription(Long userId, String subscription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPushSubscription(subscription);
        userRepository.save(user);
    }

    @Override
    // TODO: implemenar no "controller"!
    public boolean validateCredentials(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}