package pt.allanborges.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pt.allanborges.restaurant.security.JwtService;
import pt.allanborges.restaurant.security.dtos.AuthRequest;
import pt.allanborges.restaurant.security.dtos.AuthResponse;
import pt.allanborges.restaurant.security.dtos.RegisterUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;
import pt.allanborges.restaurant.service.UserAccountService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final UserAccountService userService;

    @Value("${app.admin.bootstrap-secret}")
    private String adminBootstrapSecret;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest req) {
        return ResponseEntity.status(201).body(userService.registerPending(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        var user = userService.findByUsername(req.username()).orElseThrow();

        if (!user.isEnabled() || user.getInactivatedDate() != null)
            return ResponseEntity.status(403).build();

        String token = jwt.generate(user.getUsername(), user.getRoles());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Object> registerAdmin(
            @RequestBody @Valid RegisterUserRequest req,
            @RequestHeader(name = "X-ADMIN-SECRET", required = false) String providedSecret) {

        if (providedSecret == null ||
                !MessageDigest.isEqual(
                        providedSecret.getBytes(StandardCharsets.UTF_8),
                        adminBootstrapSecret.getBytes(StandardCharsets.UTF_8))) {

            var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
            problem.setDetail("Forbidden");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
        }

        try {
            var resp = userService.registerAdmin(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            problem.setDetail(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
        }
    }

}