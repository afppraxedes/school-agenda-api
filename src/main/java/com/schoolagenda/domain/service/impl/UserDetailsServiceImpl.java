package com.schoolagenda.domain.service.impl;


import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final var user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // ✅ USANDO O MÉTODO COM ENTITY (MAIS SIMPLES)
        return UserDetailsDTO.create(user);
    }
}

// Serviço utilizado pelo Spring para logar o usuário
//@Service
//@RequiredArgsConstructor
//    public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
//        final var entity = this.userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//        return UserDetailsDTO.create(
//                String.valueOf(entity.getId()),
//                entity.getName(),
//                entity.getEmail(),
//                entity.getPassword(),
//                entity.getRoles().stream().map(x -> new SimpleGrantedAuthority(x.getDescription())).collect(Collectors.toSet()));
////                .build();
//    }
//
//}
