package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.UserRequest;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    // TODO: tem que ter como parâmetro um "UserDTO". Criar o "ModelMapper" ou o outro utilizado no curso de "FBE"!
    @GetMapping
    public List<User> findAll() {
        return this.userServiceImpl.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<UserRequest> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userServiceImpl.findByUsername(username)
                .map(user -> {
                    UserRequest userRequest = new UserRequest();
                    userRequest.setId(user.getId());
                    userRequest.setUsername(user.getUsername());
                    userRequest.setName(user.getName());
                    userRequest.setRoles(user.getRoles());
                    return ResponseEntity.ok(userRequest);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}