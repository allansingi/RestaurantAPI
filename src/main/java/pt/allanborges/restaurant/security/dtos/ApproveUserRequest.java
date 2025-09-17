package pt.allanborges.restaurant.security.dtos;

import pt.allanborges.restaurant.model.enums.Role;

import java.util.Set;

public record ApproveUserRequest(
        Set<Role> roles,
        Boolean enabled
) {}