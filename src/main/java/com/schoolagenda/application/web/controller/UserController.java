package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.CreateUserRequest;
import com.schoolagenda.application.web.dto.request.UpdateUserRequest;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.service.UserService;
import com.schoolagenda.domain.service.impl.UserServiceImpl;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final UserService userService;

    public UserController(UserServiceImpl userServiceImpl, UserService userService) {
        this.userServiceImpl = userServiceImpl;
        this.userService = userService;
    }

    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Search and Filter Endpoints
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable UserRole role) {
        List<UserResponse> responses = userService.getUsersByRole(role);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsersByName(@RequestParam String name) {
        List<UserResponse> responses = userService.searchUsersByName(name);
        return ResponseEntity.ok(responses);
    }

    // Validation Endpoints
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/username/{username}/exists")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    // Push Subscription Endpoints
    @PatchMapping("/{id}/push-subscription")
    public ResponseEntity<UserResponse> updatePushSubscription(
            @PathVariable Long id, @RequestParam String subscription) {
        UserResponse response = userService.updatePushSubscription(id, subscription);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/push-subscription")
    public ResponseEntity<UserResponse> removePushSubscription(@PathVariable Long id) {
        UserResponse response = userService.removePushSubscription(id);
        return ResponseEntity.ok(response);
    }

    // Role Management Endpoints
    @PostMapping("/{id}/roles/{role}")
    public ResponseEntity<UserResponse> addRoleToUser(
            @PathVariable Long id, @PathVariable UserRole role) {
        UserResponse response = userService.addRoleToUser(id, role);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable Long id, @PathVariable UserRole role) {
        UserResponse response = userService.removeRoleFromUser(id, role);
        return ResponseEntity.ok(response);
    }

    // Current User Endpoints
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser() {
//        UserResponse response = userService.getCurrentUserProfile();
//        return ResponseEntity.ok(response);
//    }

    // TODO: Mas com "User" estou expondo minha entidade! Não está correto! O correto está acima, ter
    // como retorno um "UserResponse"! Mas estou apenas fazendo testes!
//    @GetMapping("/me")
//    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
//        User user = (User) authentication.getPrincipal(); // Agora é User -->
//        return ResponseEntity.ok(user);
//
//        //return "Hello " + user.getName() + "! Your roles: " + user.getRoles();
//    }

//    @PreAuthorize("hasAuthority('DIRECTOR')")
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser() {
//        UserResponse response = userService.getCurrentUserProfile();
//        return ResponseEntity.ok(response);
//    }

//    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'RESPONSIBLE')")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-all/director")
//    @PreAuthorize("hasAuthority('DIRECTOR')")
    public String getUsers() {
        return "List of users - DIRECTOR access only";
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(response);
    }

    // Statistics Endpoints
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalUsersCount() {
        long count = userService.getTotalUsersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/role/{role}/count")
    public ResponseEntity<Long> getUsersCountByRole(@PathVariable UserRole role) {
        long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }
}