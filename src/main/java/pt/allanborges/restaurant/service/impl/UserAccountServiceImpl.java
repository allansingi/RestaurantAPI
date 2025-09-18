package pt.allanborges.restaurant.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.controller.handlers.exceptions.AuthManagerResolveException;
import pt.allanborges.restaurant.controller.handlers.exceptions.NoSuchElementException;
import pt.allanborges.restaurant.model.entities.UserAccount;
import pt.allanborges.restaurant.model.enums.Role;
import pt.allanborges.restaurant.model.mapper.UserAccountMapper;
import pt.allanborges.restaurant.repository.UserAccountRepository;
import pt.allanborges.restaurant.security.JwtService;
import pt.allanborges.restaurant.security.dtos.*;
import pt.allanborges.restaurant.service.UserAccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, UserDetailsService {

    @Value("${app.admin.bootstrap-secret}")
    private String adminBootstrapSecret;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper userAccountMapper;
    private final JwtService jwt;
    private final AuthenticationConfiguration authConfig;


    //UserAdminController methods
    @Override
    public List<UserResponse> findAllUserAccounts() {
        return userAccountMapper.toDTOList(userAccountRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponse approveUser(Long id, ApproveUserRequest req, String by) {
        if (req == null) {
            req = new ApproveUserRequest(null, true); // default enable=true, keep existing roles
        }

        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // roles: keep current if not provided
        if (req.roles() != null && !req.roles().isEmpty()) {
            user.setRoles(req.roles());
        } else if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(java.util.Set.of(Role.CLIENT));
        }

        // enable defaults to true when null
        boolean enable = (req.enabled() == null) || req.enabled();
        user.setEnabled(enable);

        // activate = clear inactivatedDate; record who reactivated
        if (enable) {
            user.setInactivatedDate(null);
            user.setInactivatedBy(by);
        }

        return userAccountMapper.toDTO(userAccountRepository.save(user));
    }

    //AuthController methods
    @Override
    public AuthResponse login(AuthRequest req) {
        AuthenticationManager authManager = resolveAuthManager();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserAccount user = userAccountRepository.findByUsername(req.username())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.isEnabled() || user.getInactivatedDate() != null)
            throw new AccessDeniedException("Account is not active");

        String token = jwt.generate(user.getUsername(), user.getRoles());
        return new AuthResponse(token);
    }

    private AuthenticationManager resolveAuthManager() {
        try {
            return authConfig.getAuthenticationManager();
        } catch (Exception e) {
            throw new AuthManagerResolveException("Failed to obtain AuthenticationManager", e);
        }
    }

    @Override
    @Transactional
    public UserResponse userRegister(RegisterUserRequest req) {
        userAccountRepository.findByUsername(req.username()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });

        UserAccount userAccount = UserAccount.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .roles(java.util.Set.of(Role.CLIENT))    // default role
                .enabled(false)                          // pending
                .build();

        // mark as “inactive” until admin approves
        userAccount.setInactivatedDate(LocalDateTime.now());

        userAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toDTO(userAccount);
    }

    @Override
    @Transactional
    public UserResponse registerAdmin(RegisterUserRequest req, String providedSecret) {
        // Validation - admin secret
        if (providedSecret == null || !java.security.MessageDigest.isEqual(
                providedSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                adminBootstrapSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Forbidden");
        }

        // Uniqueness check -> 409
        userAccountRepository.findByUsername(req.username()).ifPresent(u -> {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "Username already exists");
        });

        // Create ADMIN
        UserAccount admin = UserAccount.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .roles(java.util.Set.of(Role.ADMIN))
                .enabled(true)
                .build();
        admin.setInactivatedDate(null);
        admin.setInactivatedBy(null);

        admin = userAccountRepository.save(admin);
        return userAccountMapper.toDTO(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var authorities = userAccount.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());

        return User.withUsername(userAccount.getUsername())
                .password(userAccount.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!userAccount.isEnabled() || userAccount.getInactivatedDate() != null)
                .build();
    }

}