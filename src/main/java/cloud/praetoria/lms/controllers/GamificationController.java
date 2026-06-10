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
import cloud.praetoria.lms.entities.Badge;
import java.util.List;
import java.util.stream.Collectors;

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
        Long currentUserId = currentUserService.getCurrentUserId();
        LeaderboardDTO leaderboard = gamificationService.getLeaderboard(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/leaderboard/organization")
    @Operation(summary = "Obtenir le classement par organisation")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')") 
    public ResponseEntity<ApiResponse<LeaderboardDTO>> getOrganizationLeaderboard(
            @RequestParam Long organizationId) {
        User currentUser = currentUserService.getCurrentUser();
        if (!currentUser.getOrganization().getId().equals(organizationId) 
            && !currentUser.getRole().getRoleName().toString().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Accès non autorisé à cette organisation"));
        }
        LeaderboardDTO leaderboard = gamificationService.getOrganizationLeaderboard(organizationId);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    // pour le gaming endpoint
    @GetMapping("/me")
    @Operation(summary = "Profil de gamification de l'utilisateur connecté (alias)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getMyGamificationProfile() {
        Long userId = currentUserService.getCurrentUserId();
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/my-badges")
    @Operation(summary = "Badges débloqués par l'utilisateur connecté")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GamificationProfileDTO>> getMyBadges() {
        Long userId = currentUserService.getCurrentUserId();
        GamificationProfileDTO profile = gamificationService.getGamificationProfile(userId);
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