package cloud.praetoria.lms.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.*;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.AuthService;
import cloud.praetoria.lms.services.PasswordResetService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param request les données d'inscription validées
     * @return les tokens et les informations de l'utilisateur (201 Created)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Inscription réussie. Vous pouvez vous connecter !"));
    }
    
    /**
     * Connecte un utilisateur existant.
     *
     * @param request les identifiants de connexion valides
     * @return les tokens et les informations de l'utilisateur
     */

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Connexion réussie"));
    }

 
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token rafraîchi avec succès"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        User currentUser = authService.getCurrentUser();  
        authService.logout(currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Déconnexion réussie."));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
        } catch (ResourceNotFoundException e) {
            log.info("Demande de réinitialisation pour email inexistant : {}", request.getEmail());
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Si cet email existe, vous recevrez un lien de réinitialisation."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Mot de passe réinitialisé avec succès. Vous pouvez maintenant vous connecter."));
    }

    
}