package pt.allanborges.restaurant.service;

import pt.allanborges.restaurant.model.entities.UserAccount;
import pt.allanborges.restaurant.security.dtos.ApproveUserRequest;
import pt.allanborges.restaurant.security.dtos.RegisterUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    UserResponse registerPending(RegisterUserRequest req);                // public
    UserResponse approveUser(Long id, ApproveUserRequest req, String by); // ADMIN
    Optional<UserAccount> findByUsername(String username);
    List<UserResponse> listAll();
    UserResponse registerAdmin(RegisterUserRequest req);
}