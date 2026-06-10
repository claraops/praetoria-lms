package cloud.praetoria.lms.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Long organizationId) {
        List<User> users;
        if (organizationId != null) {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getOrganization() != null && u.getOrganization().getId().equals(organizationId))
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll();
        }
        List<UserResponse> dtos = users.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user), "User toggled"));
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<UserResponse>> unlockUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user), "User unlocked"));
    }
}
