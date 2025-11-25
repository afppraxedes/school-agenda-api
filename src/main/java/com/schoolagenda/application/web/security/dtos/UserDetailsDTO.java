package com.schoolagenda.application.web.security.dtos;

import com.schoolagenda.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// 📁 security/dtos/UserDetailsDTO.java
public class UserDetailsDTO implements UserDetails {
    private final String id;
    private final String name;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Construtor
    public UserDetailsDTO(String id, String name, String email, String password,
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // ✅ DOIS MÉTODOS CREATE (para flexibilidade)
    public static UserDetailsDTO create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getDescription()))
                .collect(Collectors.toList());

        return new UserDetailsDTO(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public static UserDetailsDTO create(String id, String name, String email,
                                        String password,
                                        Collection<? extends GrantedAuthority> authorities) {
        return new UserDetailsDTO(id, name, email, password, authorities);
    }

    // ✅ Getters para o JWTUtils
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String getUsername() {
        return this.email; // ✅ Documente isso claramente
    }

    // Método auxiliar se precisar do nome real
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

//    // ✅ Método estático para criar a partir da Entity
//    public static UserDetailsDTO create(User user) {
//        List<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(Collectors.toList());
//
//        return new UserDetailsDTO(
//                user.getId().toString(),
//                user.getName(),
//                user.getEmail(),
//                user.getPassword(),
//                authorities
//        );
//    }
}

//public class UserDetailsDTO implements UserDetails {
//
//    private String id;
//    private String name;
//    private String username;
//    private String password;
//    private Collection<? extends GrantedAuthority> authorities;
//
//    // ✅ APENAS CONSTRUTOR PADRÃO (sem parâmetros)
//    public UserDetailsDTO() {
//    }
//
//    // ✅ FACTORY METHOD PÚBLICO (CORRIGIDO)
//    public static UserDetailsDTO create(String id, String name, String username,
//                                        String password, Collection<? extends GrantedAuthority> authorities) {
//        UserDetailsDTO dto = new UserDetailsDTO();
//        dto.id = id;
//        dto.name = name;
//        dto.username = username;
//        dto.password = password;
//        dto.authorities = authorities;
//        return dto;
//    }
//
//    // ✅ GETTERS (mantenha como estava)
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    // ✅ SETTERS PÚBLICOS (adicione se não existirem)
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
//        this.authorities = authorities;
//    }
//
//    // ✅ Outros métodos do UserDetails
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
//}
