package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.AssignBlocksRequest;
import cloud.praetoria.lms.dtos.RoleUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.dtos.UserUpdateRequest;
import cloud.praetoria.lms.entities.UserCourseProgress;
import cloud.praetoria.lms.services.UserService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Endpoints pour la gestion des utilisateurs")
public class UserController {

    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/promotion/{id}")
    @Operation(summary = "Lister tous les utilisateurs d'une promotion")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByPromotionId(@PathVariable Long id) {
        List<UserResponse> users = userService.getAllUsersByPromotionId(id);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Utilisateur mis à jour avec succès"));
    }
    
    @PatchMapping("/{id}/role")
    @Operation(summary = "Assigner un rôle à un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        UserResponse userResponse = userService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Rôle de l'utilisateur mis à jour"));
    }
    
    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Activer ou désactiver un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> toggleEnabled(@PathVariable Long id) {
        UserResponse userResponse = userService.toggleEnabled(id);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "État de l'utilisateur mis à jour"));
    }

    @PatchMapping("/{userId}/promotion/{promotionId}")
    @Operation(summary = "Assigner une promotion à un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> assignPromotion(
            @PathVariable Long userId,
            @PathVariable Long promotionId) {
        UserResponse userResponse = userService.assignPromotion(userId, promotionId);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Promotion assignée avec succès"));
    }
    
    @DeleteMapping("/{userId}/promotion")
    @Operation(summary = "Retirer la promotion d'un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> removePromotion(@PathVariable Long userId) {
        UserResponse userResponse = userService.removePromotion(userId);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Promotion retirée avec succès"));
    }
    
    @PutMapping("/{userId}/blocks")
    @Operation(summary = "Assigner des blocs à un utilisateur")
    public ResponseEntity<ApiResponse<UserResponse>> assignBlocks(
            @PathVariable Long userId,
            @Valid @RequestBody AssignBlocksRequest request) {
        UserResponse userResponse = userService.assignBlocks(userId, request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Blocs assignés avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer logiquement un utilisateur")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Utilisateur supprimé avec succès"));
    }
    
   
}
