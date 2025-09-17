package pt.allanborges.restaurant.security.dtos;

import jakarta.validation.constraints.NotEmpty;
import pt.allanborges.restaurant.model.enums.Role;

import java.util.Set;

public record UpdateUserRolesRequest(
        @NotEmpty Set<Role> roles,
        Boolean enabled
) {}