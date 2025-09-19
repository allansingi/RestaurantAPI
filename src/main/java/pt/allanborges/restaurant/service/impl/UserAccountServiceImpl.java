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
import pt.allanborges.restaurant.controller.handlers.exceptions.*;
import pt.allanborges.restaurant.model.entities.Address;
import pt.allanborges.restaurant.model.entities.UserAccount;
import pt.allanborges.restaurant.model.enums.Role;
import pt.allanborges.restaurant.model.mapper.AddressMapper;
import pt.allanborges.restaurant.model.mapper.UserAccountMapper;
import pt.allanborges.restaurant.repository.UserAccountRepository;
import pt.allanborges.restaurant.security.JwtService;
import pt.allanborges.restaurant.security.dtos.*;
import pt.allanborges.restaurant.service.UserAccountService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, UserDetailsService {

    @Value("${app.admin.bootstrap-secret}")
    private String adminBootstrapSecret;

    private static final String USER_NOT_FOUND = "User not found";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper userAccountMapper;
    private final AddressMapper addressMapper;

    private final JwtService jwt;
    private final AuthenticationConfiguration authConfig;


    @Override
    @Transactional
    public List<UserResponse> findAllUserAccounts() {
        var users = userAccountRepository.findAll();
        return userAccountMapper.toDTOList(users);
    }

    @Override
    @Transactional
    public UserResponse approveUser(Long id, ApproveUserRequest req, String by) {
        if (req == null)
            req = new ApproveUserRequest(null, true); // default enable=true, keep existing roles

        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        if (user.getRoles() != null && user.getRoles().contains(Role.ADMIN))
            throw new AdminApprovalNotAllowedException("Admin users cannot be approved/modified via this endpoint.");
        if (req.roles() != null && req.roles().contains(Role.ADMIN))
            throw new AdminApprovalNotAllowedException("Granting ADMIN role is not allowed via this endpoint.");

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
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

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
        userAccountRepository.findByEmail(req.email()).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail already exists");
        });
        if (req.nif() != null) {
            userAccountRepository.findByNif(req.nif()).ifPresent(u -> {
                throw new IllegalArgumentException("NIF already exists");
            });
        }

        UserAccount userAccount = UserAccount.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .email(req.email())
                .nif(req.nif())
                .roles(Set.of(Role.CLIENT))
                .enabled(false)
                .build();

        if (req.addresses() != null && !req.addresses().isEmpty()) {
            List<Address> addresses = req.addresses().stream()
                    .map(addressMapper::toEntity)
                    .toList();
            userAccount.setAddresses(addresses);
        }

        userAccount.setInactivatedDate(LocalDateTime.now());
        userAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toDTO(userAccount);
    }

    @Override
    @Transactional
    public UserResponse registerAdmin(RegisterUserRequest req, String providedSecret) {
        // Validation - admin secret
        validateAdminSecret(providedSecret);
        // Uniqueness check -> 409
        userAccountRepository.findByUsername(req.username()).ifPresent(u -> {
            throw new UsernameAlreadyExistsException("Username already exists");
        });

        // Create ADMIN
        UserAccount admin = UserAccount.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .email(req.email())
                .roles(java.util.Set.of(Role.ADMIN))
                .enabled(true)
                .build();
        admin.setInactivatedDate(null);
        admin.setInactivatedBy(null);

        admin = userAccountRepository.save(admin);
        return userAccountMapper.toDTO(admin);
    }

    private void validateAdminSecret(String providedSecret) {
        if (providedSecret == null)
            throw new InvalidAdminSecretException("Invalid admin bootstrap secret");

        var provided = providedSecret.getBytes(StandardCharsets.UTF_8);
        var expected = adminBootstrapSecret.getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(provided, expected))
            throw new InvalidAdminSecretException("Invalid admin bootstrap secret");

    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

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