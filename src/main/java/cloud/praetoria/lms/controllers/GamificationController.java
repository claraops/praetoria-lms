package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.GamificationProfileDTO;
import cloud.praetoria.lms.dtos.LeaderboardDTO;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.GamificationService;

@Slf4j
@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "Endpoints pour la gamification (XP, badges, classements)")
public class GamificationController {

    private final GamificationService gamificationService;
    private final CurrentUserService currentUserService;

    @GetMapping("/profile")
    @Operation(summary = "Obtenir le profil de gamification de l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getMyProfile() {
        Long userId = currentUserService.getCurrentUserId();
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Obtenir le profil de gamification d'un utilisateur spécifique")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getUserProfile(@PathVariable Long userId) {
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Obtenir le classement global (top 10)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LeaderboardDTO>> getLeaderboard() {
        LeaderboardDTO leaderboard = gamificationService.getLeaderboard();
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/leaderboard/organization")
    @Operation(summary = "Obtenir le classement par organisation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LeaderboardDTO>> getOrganizationLeaderboard(
            @RequestParam Long organizationId) {
        LeaderboardDTO leaderboard = gamificationService.getOrganizationLeaderboard(organizationId);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    // ==================== MES NOUVEAUX ENDPOINTS DE GAMING ====================

    @GetMapping("/me")
    @Operation(summary = "Profil de gamification de l'utilisateur connecté (alias)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getMyGamificationProfile() {
        Long userId = currentUserService.getCurrentUserId();
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/badges")
    @Operation(summary = "Liste de tous les badges disponibles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAllBadges() {
        // À implémenter si besoin - retourne la liste de tous les badges
        return ResponseEntity.ok(ApiResponse.successVoid("Endpoint à implémenter"));
    }

    @GetMapping("/my-badges")
    @Operation(summary = "Badges débloqués par l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getMyBadges() {
        Long userId = currentUserService.getCurrentUserId();
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
        // Les badges sont déjà dans le DTO
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/leaderboard/my-organization")
    @Operation(summary = "Classement dans l'organisation de l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LeaderboardDTO>> getMyOrganizationLeaderboard() {
        User user = currentUserService.getCurrentUser();
        Long organizationId = user.getOrganization().getId();
        LeaderboardDTO leaderboard = gamificationService.getOrganizationLeaderboard(organizationId);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }
}