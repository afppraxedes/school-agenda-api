package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.CreateUserRequest;
import com.schoolagenda.application.web.dto.request.UpdateUserRequest;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.enums.UserRole;

import java.util.List;

public interface UserService {
//    public List<User> findAll();
//    public Optional<User> findByUsername(String username);
//    public User save(User user);
//    void savePushSubscription(Long userId, String subscription);
//    boolean validateCredentials(String username, String password);

    // CRUD Operations
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);

    // Utility Methods
    List<UserResponse> getUserAll();
    UserResponse getUserByEmail(String email);
    UserResponse getUserByUsername(String username);
    List<UserResponse> getUsersByRole(UserRole role);
    List<UserResponse> getUsersByRoles(List<UserRole> roles);
    List<UserResponse> searchUsersByName(String name);
    boolean validateUserCredentials(String username, String rawPassword);

    // Profile Management
    UserResponse updatePushSubscription(Long userId, String pushSubscription);
    UserResponse removePushSubscription(Long userId);

    // Validation Methods
    boolean validateCredentials(String username, String password);
    boolean emailExists(String email);
    boolean usernameExists(String username);
    boolean emailExistsForOtherUser(String email, Long userId);
    boolean usernameExistsForOtherUser(String username, Long userId);

    List<UserResponse> findAllByProfile(String profile);

    // Count Methods
    long countUsersByRole(UserRole role);
    long getTotalUsersCount();

    // Role Management
    UserResponse addRoleToUser(Long userId, UserRole role);
    UserResponse removeRoleFromUser(Long userId, UserRole role);

    // Current User Operations
    UserResponse getCurrentUserProfile();
    UserResponse updateCurrentUserProfile(UpdateUserRequest request);
}
