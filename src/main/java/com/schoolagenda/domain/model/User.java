package com.schoolagenda.domain.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
// TODO: COLOCAR O NOME DAS TABELAS NO SINGULAR QUANDO FOR UTILIZAR O "MYSQL" (user)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserRole> roles = new HashSet<>();

    @Column(length = 2000)
    private String pushSubscription;

//    @Column(nullable = false)
//    private String profile;

    public User() {}

    public User(String email, String username, String password, String name, Set<UserRole> roles) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }

    public String getPushSubscription() { return pushSubscription; }
    public void setPushSubscription(String pushSubscription) { this.pushSubscription = pushSubscription; }
}