package pt.allanborges.restaurant.security.dtos;

import pt.allanborges.restaurant.model.dtos.AddressDTO;
import pt.allanborges.restaurant.model.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String name,
        String email,
        String nif,
        Set<Role> roles,
        boolean enabled,
        LocalDateTime inactivatedDate,
        List<AddressDTO> addresses
) {}