package pt.allanborges.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.repository.UserAccountRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) {
        var u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());

        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!u.isEnabled() || u.getInactivatedDate() != null)
                .build();
    }

}