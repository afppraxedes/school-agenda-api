//package com.schoolagenda.domain.repository;
//
//import com.schoolagenda.domain.model.User;
//import com.schoolagenda.domain.enums.UserRole;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    // Find by email
//    Optional<User> findByEmail(String email);
//
//    // Find by username
//    Optional<User> findByUsername(String username);
//
//    // Find by email or username
//    Optional<User> findByEmailOrUsername(String email, String username);
//
//    // Check if email exists
//    boolean existsByEmail(String email);
//
//    // Check if username exists
//    boolean existsByUsername(String username);
//
//    // Check if email exists excluding a specific user
//    boolean existsByEmailAndIdNot(String email, Long id);
//
//    // Check if username exists excluding a specific user
//    boolean existsByUsernameAndIdNot(String username, Long id);
//
//    // Find users by role
//    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
//    List<User> findByRole(@Param("role") UserRole role);
//
//    // Find users by multiple roles
//    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
//    List<User> findByRolesIn(@Param("roles") List<UserRole> roles);
//
//    // Find users by name containing (search)
//    List<User> findByNameContainingIgnoreCase(String name);
//
//    // Find all users with roles eager loaded
//    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
//    List<User> findAllWithRoles();
//
//    // Find user by ID with roles eager loaded
//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
//    Optional<User> findByIdWithRoles(@Param("id") Long id);
//
//    // Count users by role
//    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
//    long countByRole(@Param("role") UserRole role);
//}

//package com.schoolagenda.domain.repository;

//import com.schoolagenda.domain.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
//    boolean existsByEmail(String email);
//    boolean existsByUsername(String username);
//}

package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Métodos existentes
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // ✅ NOVOS MÉTODOS NECESSÁRIOS
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") UserRole role);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findByRolesIn(@Param("roles") List<UserRole> roles);

    List<User> findByNameContainingIgnoreCase(String name);

    // TODO: métodos que estavam anteriormente funcionando corretamente
//    boolean existsByEmailAndIdNot(String email, Long id);
//    boolean existsByUsernameAndIdNot(String username, Long id);

    // TODO: Novos método! Verificar no postman se está tudo correto com a validação de email!
    // Esse parâmetro "excludeId" está com nome "estranho"!
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    // Esse parâmetro "excludeId" está com nome "estranho"!
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") UserRole role);

    List<User> findAllByRolesContainingOrderByNameAsc(UserRole role);
}