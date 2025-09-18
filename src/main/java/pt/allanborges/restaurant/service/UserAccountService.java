package pt.allanborges.restaurant.service;

import pt.allanborges.restaurant.security.dtos.*;

import java.util.List;

public interface UserAccountService {
    //UserAdminController methods
    List<UserResponse> findAllUserAccounts();
    UserResponse approveUser(final Long id, final ApproveUserRequest req, final String by);

    //AuthController methods
    AuthResponse login(final AuthRequest req) throws Exception;
    UserResponse userRegister(final RegisterUserRequest req);
    UserResponse registerAdmin(final RegisterUserRequest req, final String providedSecret);
}