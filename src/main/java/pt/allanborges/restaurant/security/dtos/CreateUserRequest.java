package pt.allanborges.restaurant.security.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import pt.allanborges.restaurant.model.enums.Role;

import java.util.Set;

public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotEmpty Set<Role> roles,
        boolean enabled
) {}