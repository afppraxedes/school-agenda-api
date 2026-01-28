//package com.schoolagenda.domain.service.impl;
//
//import com.schoolagenda.application.web.dto.request.CreateUserRequest;
//import com.schoolagenda.application.web.dto.request.UpdateUserRequest;
//import com.schoolagenda.application.web.dto.response.UserResponse;
//import com.schoolagenda.domain.model.User;
//import com.schoolagenda.domain.enums.UserRole;
//import com.schoolagenda.domain.repository.UserRepository;
//import com.schoolagenda.domain.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public UserResponse createUser(CreateUserRequest request) {
//        // Validate unique constraints
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists: " + request.getEmail());
//        }
//
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists: " + request.getUsername());
//        }
//
//        // ✅ CORREÇÃO: Usar builder em vez de construtor
//        User user = User.builder()
//                .email(request.getEmail())
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .name(request.getName())
//                .roles(request.getRoles() != null ? new HashSet<>(request.getRoles()) : new HashSet<>())
//                .pushSubscription(request.getPushSubscription())
//                .build();
//
//        User savedUser = userRepository.save(user);
//        return convertToResponse(savedUser);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserResponse getUserById(Long id) {
//        // ✅ CORREÇÃO: Usar findById se findByIdWithRoles não existir
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//
//        return convertToResponse(user);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<UserResponse> getAllUsers() {
//        // ✅ CORREÇÃO: Usar findAll se findAllWithRoles não existir
//        List<User> users = userRepository.findAll();
//
//        return users.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public UserResponse updateUser(Long id, UpdateUserRequest request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//
//        // Validate unique constraints for updates
//        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
//            if (userRepository.existsByEmail(request.getEmail())) {
//                throw new RuntimeException("Email already exists: " + request.getEmail());
//            }
//            user.setEmail(request.getEmail());
//        }
//
//        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
//            if (userRepository.existsByUsername(request.getUsername())) {
//                throw new RuntimeException("Username already exists: " + request.getUsername());
//            }
//            user.setUsername(request.getUsername());
//        }
//
//        if (request.getName() != null) {
//            user.setName(request.getName());
//        }
//
//        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        }
//
//        if (request.getRoles() != null) {
//            user.setRoles(new HashSet<>(request.getRoles()));
//        }
//
//        if (request.getPushSubscription() != null) {
//            user.setPushSubscription(request.getPushSubscription());
//        }
//
//        User updatedUser = userRepository.save(user);
//        return convertToResponse(updatedUser);
//    }
//
//    @Override
//    public void deleteUser(Long id) {
//
//    }
//
//    @Override
//    public List<UserResponse> getUserAll() {
//        return List.of();
//    }
//
//    @Override
//    public UserResponse getUserByEmail(String email) {
//        return null;
//    }
//
//    @Override
//    public UserResponse getUserByUsername(String username) {
//        return null;
//    }
//
//    // ✅ CORREÇÃO: Simplificar métodos que usam queries complexas
//    @Override
//    @Transactional(readOnly = true)
//    public List<UserResponse> getUsersByRole(UserRole role) {
//        List<User> users = userRepository.findAll().stream()
//                .filter(user -> user.getRoles().contains(role))
//                .collect(Collectors.toList());
//
//        return users.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<UserResponse> getUsersByRoles(List<UserRole> roles) {
//        List<User> users = userRepository.findAll().stream()
//                .filter(user -> user.getRoles().stream().anyMatch(roles::contains))
//                .collect(Collectors.toList());
//
//        return users.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<UserResponse> searchUsersByName(String name) {
//        // ✅ CORREÇÃO: Implementação alternativa se método não existir
//        List<User> users = userRepository.findAll().stream()
//                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
//                .collect(Collectors.toList());
//
//        return users.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    // TODO: ABAIXO MANTER OS MÉTODOS
//
//    @Transactional
//    @Override
//    public boolean validateUserCredentials(String username, String rawPassword) {
//        try {
//            User user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            return passwordEncoder.matches(rawPassword, user.getPassword());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    @Transactional
//    public UserResponse updatePushSubscription(Long userId, String pushSubscription) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        user.setPushSubscription(pushSubscription);
//        User updatedUser = userRepository.save(user);
//
//        return convertToResponse(updatedUser);
//    }
//
//    @Override
//    @Transactional
//    public UserResponse removePushSubscription(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        user.setPushSubscription(null);
//        User updatedUser = userRepository.save(user);
//
//        return convertToResponse(updatedUser);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public boolean emailExists(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//    @Override
//    // TODO: implemenar no "controller"!
//    public boolean validateCredentials(String username, String password) {
//        return userRepository.findByUsername(username)
//                .map(user -> passwordEncoder.matches(password, user.getPassword()))
//                .orElse(false);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public boolean usernameExists(String username) {
//        return userRepository.existsByUsername(username);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public boolean emailExistsForOtherUser(String email, Long userId) {
//        return userRepository.existsByEmailAndIdNot(email, userId);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public boolean usernameExistsForOtherUser(String username, Long userId) {
//        return userRepository.existsByUsernameAndIdNot(username, userId);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public long countUsersByRole(UserRole role) {
//        return userRepository.countByRole(role);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public long getTotalUsersCount() {
//        return userRepository.count();
//    }
//
//    @Override
//    @Transactional
//    public UserResponse addRoleToUser(Long userId, UserRole role) {
//        User user = userRepository.findByIdWithRoles(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        user.getRoles().add(role);
//        User updatedUser = userRepository.save(user);
//
//        return convertToResponse(updatedUser);
//    }
//
//    @Override
//    @Transactional
//    public UserResponse removeRoleFromUser(Long userId, UserRole role) {
//        User user = userRepository.findByIdWithRoles(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        user.getRoles().remove(role);
//        User updatedUser = userRepository.save(user);
//
//        return convertToResponse(updatedUser);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserResponse getCurrentUserProfile() {
//        String username = getCurrentUsername();
//        return getUserByUsername(username);
//    }
//
//    @Override
//    @Transactional
//    public UserResponse updateCurrentUserProfile(UpdateUserRequest request) {
//        String username = getCurrentUsername();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
//
//        return updateUser(user.getId(), request);
//    }
//
//    /**
//     * Get current authenticated username
//     */
//    private String getCurrentUsername() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        if (principal instanceof UserDetails) {
//            return ((UserDetails) principal).getUsername();
//        } else {
//            return principal.toString();
//        }
//    }
//
////    /**
////     * Converts User entity to Response DTO
////     */
////    private UserResponse convertToResponse(User user) {
////        String profileType = determineProfileType(user.getRoles());
////
////        return new UserResponse(
////                user.getId(),
////                user.getEmail(),
////                user.getUsername(),
////                user.getName(),
////                user.getRoles(),
////                user.getPushSubscription(),
////                profileType
////        );
////    }
////
////    /**
////     * Determines profile type based on roles
////     */
////    private String determineProfileType(Set<UserRole> roles) {
////        if (roles.contains(UserRole.DIRECTOR)) {
////            return "director";
////        } else if (roles.contains(UserRole.TEACHER)) {
////            return "teacher";
////        } else if (roles.contains(UserRole.RESPONSIBLE)) {
////            return "responsible";
////        } else {
////            return "user";
////        }
////    }
//
//    /**
//     * Converts User entity to Response DTO
//     */
//    private UserResponse convertToResponse(User user) {
//        String profileType = determineProfileType(user.getRoles());
//
//        return UserResponse.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .username(user.getUsername())
//                .name(user.getName())
//                .roles(user.getRoles())
//                .pushSubscription(user.getPushSubscription())
//                .profileType(profileType)
//                .build();
//    }
//
//    /**
//     * Determines profile type based on roles
//     */
//    private String determineProfileType(Set<UserRole> roles) {
//        if (roles.contains(UserRole.DIRECTOR)) {
//            return "director";
//        } else if (roles.contains(UserRole.TEACHER)) {
//            return "teacher";
//        } else if (roles.contains(UserRole.RESPONSIBLE)) {
//            return "responsible";
//        } else {
//            return "user";
//        }
//    }
//
//}

package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.CreateUserRequest;
import com.schoolagenda.application.web.dto.request.UpdateUserRequest;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.exception.DuplicateResourceException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .roles(request.getRoles() != null ? new HashSet<>(request.getRoles()) : new HashSet<>())
                .pushSubscription(request.getPushSubscription())
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Validações
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateEmailUniqueness(request.getEmail(), id);
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            validateUsernameUniqueness(request.getUsername(), id);
            user.setUsername(request.getUsername());
        }

        // Atualiza outros campos
        updateUserFields(user, request);

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    /**
     * Atualiza os demais campos do usuário
     */
    private void updateUserFields(User user, UpdateUserRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null) {
            user.setRoles(new HashSet<>(request.getRoles()));
        }

        if (request.getPushSubscription() != null) {
            user.setPushSubscription(request.getPushSubscription());
        }
    }

    private void validateEmailUniqueness(String email, Long currentUserId) {
        boolean emailExists = userRepository.existsByEmailAndIdNot(email, currentUserId);
        if (emailExists) {
            throw new DuplicateResourceException("Email '" + email + "' is already taken");
        }
    }

    private void validateUsernameUniqueness(String username, Long currentUserId) {
        boolean usernameExists = userRepository.existsByUsernameAndIdNot(username, currentUserId);
        if (usernameExists) {
            throw new DuplicateResourceException("Username '" + username + "' is already taken");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUserAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRoles(List<UserRole> roles) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(roles::contains))
                .collect(Collectors.toList());
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsersByName(String name) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateUserCredentials(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional
    public UserResponse updatePushSubscription(Long userId, String pushSubscription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setPushSubscription(pushSubscription);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse removePushSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setPushSubscription(null);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCredentials(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExistsForOtherUser(String email, Long userId) {
        return userRepository.findByEmail(email)
                .map(user -> !user.getId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExistsForOtherUser(String username, Long userId) {
        return userRepository.findByUsername(username)
                .map(user -> !user.getId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public UserResponse addRoleToUser(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse removeRoleFromUser(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
//        String username = getCurrentUsername();
        String username = getCurrentUsername();
//        return getUserByUsername(username);
        return getUserByEmail(username);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUserProfile(UpdateUserRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return updateUser(user.getId(), request);
    }

    @Override
    // Versão alternativa mais segura:
    public List<UserResponse> findAllByProfile(String profile) {
        // Valida se o perfil existe no enum
        UserRole role = Arrays.stream(UserRole.values())
                .filter(r -> r.getDescription().equalsIgnoreCase(profile) ||
                        r.name().equalsIgnoreCase(profile))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Perfil inválido: " + profile));

        List<User> users = userRepository.findAllByRolesContainingOrderByNameAsc(role);

        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getName()))
                .collect(Collectors.toList());
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private UserResponse convertToResponse(User user) {
        String profileType = determineProfileType(user.getRoles());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
//                .username(user.getActualUsername())
                .username(user.getUsername())
                .name(user.getName())
                .roles(user.getRoles())
                .pushSubscription(user.getPushSubscription())
                .profileType(profileType)
                .build();
    }

    private String determineProfileType(Set<UserRole> roles) {
        if (roles.contains(UserRole.DIRECTOR)) {
            return "director";
        } else if (roles.contains(UserRole.TEACHER)) {
            return "teacher";
        } else if (roles.contains(UserRole.RESPONSIBLE)) {
            return "responsible";
        } else {
            return "user";
        }
    }
}