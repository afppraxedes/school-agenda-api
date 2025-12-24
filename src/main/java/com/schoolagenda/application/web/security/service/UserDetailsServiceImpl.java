package com.schoolagenda.application.web.security.service;

import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
//        final var user = this.userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//        // ✅ GARANTIR que retorna UserDetailsDTO, não User
//        return convertToUserDetailsDTO(user);
//    }
//
//    private UserDetailsDTO convertToUserDetailsDTO(User user) {
//        Collection<GrantedAuthority> authorities = user.getRoles().stream()
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
//}

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return AgendaUserDetails.create(user);
    }
}
