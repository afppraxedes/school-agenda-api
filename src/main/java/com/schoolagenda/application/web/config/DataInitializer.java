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
            director.setEmail("director.mail@mail.com");
            director.setUsername("director");
            director.setPassword(passwordEncoder.encode("password"));
            director.setName("João Director");
            director.setRoles(Set.of(UserRole.DIRECTOR));
            userRepository.save(director);
        }

        if (userRepository.findByUsername("teacher").isEmpty()) {
            User teacher1 = new User();
            teacher1.setEmail("teacher1.mail@mail.com");
            teacher1.setUsername("teacher1");
            teacher1.setPassword(passwordEncoder.encode("password"));
            teacher1.setName("Maria Professor");
            teacher1.setRoles(Set.of(UserRole.TEACHER));
            userRepository.save(teacher1);

            User teacher2 = new User();
            teacher2.setEmail("teacher2.mail@mail.com");
            teacher2.setUsername("teacher2");
            teacher2.setPassword(passwordEncoder.encode("password"));
            teacher2.setName("Maria Professor");
            teacher2.setRoles(Set.of(UserRole.TEACHER));
            userRepository.save(teacher2);

            User teacher3 = new User();
            teacher3.setEmail("teacher3.mail@mail.com");
            teacher3.setUsername("teacher3");
            teacher3.setPassword(passwordEncoder.encode("password"));
            teacher3.setName("Maria Professor");
            teacher3.setRoles(Set.of(UserRole.TEACHER));
            userRepository.save(teacher3);
        }

        if (userRepository.findByUsername("responsible").isEmpty()) {
            User responsible1 = new User();
            responsible1.setEmail("responsible1.mail@mail.com");
            responsible1.setUsername("responsible1");
            responsible1.setPassword(passwordEncoder.encode("password"));
            responsible1.setName("Carlos Responsável");
            responsible1.setRoles(Set.of(UserRole.RESPONSIBLE));
            userRepository.save(responsible1);

            User responsible2 = new User();
            responsible2.setEmail("responsible2.mail@mail.com");
            responsible2.setUsername("responsible2");
            responsible2.setPassword(passwordEncoder.encode("password"));
            responsible2.setName("Mariana Responsável");
            responsible2.setRoles(Set.of(UserRole.RESPONSIBLE));
            userRepository.save(responsible2);

            User responsible3 = new User();
            responsible3.setEmail("responsible3.mail@mail.com");
            responsible3.setUsername("responsible3");
            responsible3.setPassword(passwordEncoder.encode("password"));
            responsible3.setName("Alexandre Responsável");
            responsible3.setRoles(Set.of(UserRole.RESPONSIBLE));
            userRepository.save(responsible3);
        }

        System.out.println("=== Sample Users Created ===");
        System.out.println("Director:    usuario: director, senha: password");
        System.out.println("Teacher:     usuario: teacher1 | teacher2 teacher3, senha: password");
        System.out.println("Responsible: usuario: responsible1 | responsible2 | responsible3, senha: password");
        System.out.println("============================");
    }
}