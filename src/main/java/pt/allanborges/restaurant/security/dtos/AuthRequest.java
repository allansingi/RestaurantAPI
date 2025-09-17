package pt.allanborges.restaurant.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
) {}