package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.UserDTO;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO: tem que ter como parâmetro um "UserDTO". Criar o "ModelMapper" ou o outro utilizado no curso de "FBE"!
    @GetMapping
    public List<User> findAll() {
        return this.userService.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.findByUsername(username)
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setName(user.getName());
                    userDTO.setRoles(user.getRoles());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}