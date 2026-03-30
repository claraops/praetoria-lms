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

   
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Inscription réussie. Vous pouvez vous connecter ! "));
    }

    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        User currentUser = getCurrentUser();
        authService.logout(currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Déconnexion réussie."));
    }

    
    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
        } catch (ResourceNotFoundException e) {
        		//ne revele peas si le mail existe ou non
            log.info("Demande de réinitialisation pour email inexistant : {}", request.getEmail());
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Si cet email existe, vous recevrez un lien de réinitialisation."));
    }

   
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Mot de passe réinitialisé avec succès. Vous pouvez maintenant vous connecter."));
    }

   
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new cloud.praetoria.lms.exceptions.AuthenticationException("Utilisateur non authentifié");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
}