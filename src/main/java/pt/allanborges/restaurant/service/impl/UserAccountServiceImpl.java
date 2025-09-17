package pt.allanborges.restaurant.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.controller.handlers.exceptions.NoSuchElementException;
import pt.allanborges.restaurant.model.entities.UserAccount;
import pt.allanborges.restaurant.model.enums.Role;
import pt.allanborges.restaurant.repository.UserAccountRepository;
import pt.allanborges.restaurant.security.dtos.ApproveUserRequest;
import pt.allanborges.restaurant.security.dtos.RegisterUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;
import pt.allanborges.restaurant.service.UserAccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserResponse registerPending(RegisterUserRequest req) {
        users.findByUsername(req.username()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });

        UserAccount userAccount = UserAccount.builder()
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .roles(java.util.Set.of(Role.CLIENT))    // default role
                .enabled(false)                          // pending
                .build();

        // mark as “inactive” until admin approves
        userAccount.setInactivatedDate(LocalDateTime.now());

        userAccount = users.save(userAccount);
        return toResp(userAccount);
    }

    @Override
    @Transactional
    public UserResponse approveUser(Long id, ApproveUserRequest req, String by) {
        UserAccount user = users.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // roles: keep current if not provided
        if (req.roles() != null && !req.roles().isEmpty()) {
            user.setRoles(req.roles());
        } else if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(java.util.Set.of(Role.CLIENT));
        }

        // enable defaults to true when null
        boolean enable = req.enabled() == null || req.enabled();
        user.setEnabled(enable);

        // activate = clear inactivatedDate; keep it set if not enabling
        if (enable) {
            user.setInactivatedDate(null);
            user.setInactivatedBy(by);     // keep audit info
        }

        return toResp(users.save(user));
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return users.findByUsername(username);
    }

    @Override
    public List<UserResponse> listAll() {
        return users.findAll().stream().map(this::toResp).toList();
    }

    @Override
    @Transactional
    public UserResponse registerAdmin(RegisterUserRequest req) {
        users.findByUsername(req.username()).ifPresent(userAccount -> {
            throw new IllegalArgumentException("Username already exists");
        });
        UserAccount admin = UserAccount.builder()
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .roles(java.util.Set.of(Role.ADMIN))
                .enabled(true)
                .build();
        admin.setInactivatedDate(null);
        admin.setInactivatedBy(null);
        admin = users.save(admin);
        return toResp(admin);
    }

    private UserResponse toResp(UserAccount userAccount) {
        return new UserResponse(userAccount.getId(), userAccount.getUsername(), userAccount.getRoles(),
                userAccount.isEnabled(), userAccount.getInactivatedDate());
    }

}