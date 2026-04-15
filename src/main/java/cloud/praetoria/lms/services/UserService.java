package cloud.praetoria.lms.services;

import cloud.praetoria.lms.dtos.AssignBlocksRequest;
import cloud.praetoria.lms.dtos.RoleUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;  // ← Utiliser UserResponse
import cloud.praetoria.lms.dtos.UserUpdateRequest;
import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    List<UserResponse> getAllUsersByPromotionId(Long promotionId);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse updateRole(Long id, RoleUpdateRequest request);

    UserResponse toggleEnabled(Long id);

    UserResponse assignPromotion(Long userId, Long promotionId);

    UserResponse removePromotion(Long userId);

    UserResponse assignBlocks(Long userId, AssignBlocksRequest request);

    void softDeleteUser(Long id);
}