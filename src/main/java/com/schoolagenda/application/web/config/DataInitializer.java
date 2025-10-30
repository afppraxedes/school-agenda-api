package com.schoolagenda.application.web.config;

import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.model.UserRole;
import com.schoolagenda.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create sample users
        if (userRepository.findByUsername("director").isEmpty()) {
            User director = new User();
            director.setUsername("director");
            director.setPassword(passwordEncoder.encode("password"));
            director.setName("João Director");
            director.setRoles(Set.of(UserRole.DIRECTOR));
            userRepository.save(director);
        }

        if (userRepository.findByUsername("teacher").isEmpty()) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("password"));
            teacher.setName("Maria Professor");
            teacher.setRoles(Set.of(UserRole.TEACHER));
            userRepository.save(teacher);
        }

        if (userRepository.findByUsername("responsible").isEmpty()) {
            User responsible = new User();
            responsible.setUsername("responsible");
            responsible.setPassword(passwordEncoder.encode("password"));
            responsible.setName("Carlos Responsável");
            responsible.setRoles(Set.of(UserRole.RESPONSIBLE));
            userRepository.save(responsible);
        }

        System.out.println("=== Sample Users Created ===");
        System.out.println("Director:    usuario: director, senha: password");
        System.out.println("Teacher:     usuario: teacher, senha: password");
        System.out.println("Responsible: usuario: responsible, senha: password");
        System.out.println("============================");
    }
}