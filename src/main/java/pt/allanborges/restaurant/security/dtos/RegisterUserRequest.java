package pt.allanborges.restaurant.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pt.allanborges.restaurant.model.dtos.AddressDTO;

import java.util.List;

public record RegisterUserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank String name,
        @NotBlank @Email String email,
        @Pattern(regexp = "\\d{9}", message = "NIF must be 9 digits")
        @NotBlank String nif,
        List<AddressDTO> addresses
) {}