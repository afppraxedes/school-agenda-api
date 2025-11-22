package com.schoolagenda.application.web.security.dtos;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsDTO implements UserDetails {

    private String id;
    private String name;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // ✅ APENAS CONSTRUTOR PADRÃO (sem parâmetros)
    public UserDetailsDTO() {
    }

    // ✅ FACTORY METHOD PÚBLICO (CORRIGIDO)
    public static UserDetailsDTO create(String id, String name, String username,
                                        String password, Collection<? extends GrantedAuthority> authorities) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.id = id;
        dto.name = name;
        dto.username = username;
        dto.password = password;
        dto.authorities = authorities;
        return dto;
    }

    // ✅ GETTERS (mantenha como estava)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // ✅ SETTERS PÚBLICOS (adicione se não existirem)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    // ✅ Outros métodos do UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
