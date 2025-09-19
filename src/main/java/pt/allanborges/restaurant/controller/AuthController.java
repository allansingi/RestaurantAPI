package pt.allanborges.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.allanborges.restaurant.controller.apidocs.AuthApiDocs;
import pt.allanborges.restaurant.security.dtos.AuthRequest;
import pt.allanborges.restaurant.security.dtos.AuthResponse;
import pt.allanborges.restaurant.security.dtos.RegisterUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;
import pt.allanborges.restaurant.service.UserAccountService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final UserAccountService userService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> userRegister(@RequestBody @Valid RegisterUserRequest req) {
        return ResponseEntity.status(201).body(userService.userRegister(req));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestBody @Valid RegisterUserRequest req,
            @RequestHeader(name = "X-ADMIN-SECRET", required = false) String providedSecret) {

        var resp = userService.registerAdmin(req, providedSecret);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

}