package com.schoolagenda.application.web.security.dto;

import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AgendaUserDetails implements UserDetails {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public AgendaUserDetails(Long id, String name, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static AgendaUserDetails create(User user) {
        // ⭐ CORREÇÃO: Use role.name() para consistência
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // ⬅️ role.name()
                .collect(Collectors.toList());

        return new AgendaUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public static AgendaUserDetails create(Long id, String name, String email,
                                           String password,
                                           Collection<? extends GrantedAuthority> authorities) {
        return new AgendaUserDetails(id, name, email, password, authorities);
    }

    public boolean hasRole(UserRole role) {
        String roleName = role.name(); // ⬅️ role.name()
        return authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getDisplayName() {
        return this.name;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}