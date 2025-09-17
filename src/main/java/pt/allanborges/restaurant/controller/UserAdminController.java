package pt.allanborges.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.allanborges.restaurant.security.dtos.ApproveUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;
import pt.allanborges.restaurant.service.UserAccountService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAccountService userService;

    @GetMapping
    public List<UserResponse> list() {
        return userService.listAll();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<UserResponse> approve(@PathVariable Long id,
                                                @RequestBody(required = false) ApproveUserRequest req,
                                                Principal principal) {
        if (req == null) req = new ApproveUserRequest(null, true); // default enable
        return ResponseEntity.ok(userService.approveUser(id, req, principal.getName()));
    }

}