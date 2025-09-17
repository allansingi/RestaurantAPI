package pt.allanborges.restaurant.security.dtos;

import pt.allanborges.restaurant.model.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id, String username,
        Set<Role> roles,
        boolean enabled,
        LocalDateTime inactivatedDate
) {}