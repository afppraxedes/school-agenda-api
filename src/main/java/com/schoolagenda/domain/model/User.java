//package com.schoolagenda.domain.model;

//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
////@With
////@Builder
//@Entity
//// TODO: COLOCAR O NOME DAS TABELAS NO SINGULAR QUANDO FOR UTILIZAR O "MYSQL" (user)
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String email;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
//    private Set<UserRole> roles = new HashSet<>();
//
//    @Column(length = 2000)
//    private String pushSubscription;
//
//      // TODO: verificar apenas se terei que deixar o atributo abaixo (ACHO QUE NÃO!)!
//      // @Column(nullable = false)
//      // private String profileType; // "responsible", "teacher", "director"
//
//    public User() {}
//
//    public User(String email, String username, String password, String name, Set<UserRole> roles) {
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.name = name;
//        this.roles = roles;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Set<UserRole> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<UserRole> roles) {
//        this.roles = roles;
//    }
//
//    public String getPushSubscription() {
//        return pushSubscription;
//    }
//
//    public void setPushSubscription(String pushSubscription) {
//        this.pushSubscription = pushSubscription;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", email='" + email + '\'' +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", name='" + name + '\'' +
//                ", roles=" + roles +
//                ", pushSubscription='" + pushSubscription + '\'' +
//                '}';
//    }
//}

package com.schoolagenda.domain.model;

import com.schoolagenda.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) // ✅ Mudei para user_roles
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @Column(length = 2000)
    private String pushSubscription;

    // ✅ MÉTODOS DO USERDETAILS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email; // Spring Security usa email como username
    }

    public String getActualUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class User implements UserDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String email;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
//    private Set<UserRole> roles = new HashSet<>();
//
//    @Column(length = 2000)
//    private String pushSubscription;
//
//    // ✅ MÉTODOS DO USERDETAILS IMPLEMENTADOS
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email; // ✅ Usa email como username para autenticação
//    }
//
//    // ✅ Método auxiliar para obter o username real se necessário
//    public String getActualUsername() {
//        return this.username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", email='" + email + '\'' +
//                ", username='" + username + '\'' +
//                ", name='" + name + '\'' +
//                ", roles=" + roles +
//                '}';
//    }
//}