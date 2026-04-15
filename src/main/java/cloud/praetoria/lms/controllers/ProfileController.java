package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.PasswordUpdateRequest;
import cloud.praetoria.lms.dtos.ProfileUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.security.UserDetailsImpl;
import cloud.praetoria.lms.services.ProfileService;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Endpoints pour la gestion du profil de l'utilisateur connecté")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Consulter mon profil")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponse profile = profileService.getMyProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping
    @Operation(summary = "Mettre à jour mon profil")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {
        UserResponse updated = profileService.updateMyProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profil mis à jour avec succès"));
    }

    @PatchMapping("/password")
    @Operation(summary = "Changer mon mot de passe")
    public ResponseEntity<ApiResponse<Void>> updateMyPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PasswordUpdateRequest request) {
        profileService.updateMyPassword(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.successVoid("Mot de passe mis à jour avec succès"));
    }
}