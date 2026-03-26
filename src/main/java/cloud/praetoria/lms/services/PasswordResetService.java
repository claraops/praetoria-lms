package cloud.praetoria.lms.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.ResetPasswordRequest;
import cloud.praetoria.lms.entities.PasswordResetToken;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.AuthenticationException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.PasswordResetTokenRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    /**
     * Créer un token de reset du mot de passe et envoyer l'email
     */
    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Demande de reset du mot de passe pour: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("Email non trouvé: {}", email);
                return new ResourceNotFoundException("Email non trouvé");
            });
        
        // Générer un token
        String tokenValue = UUID.randomUUID().toString();
        
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .token(tokenValue)
            .user(user)
            .expiresAt(LocalDateTime.now().plusHours(1))
            .isUsed(false)
            .build();
        
        passwordResetTokenRepository.save(resetToken);
        log.info("Token de reset créé pour: {}", email);
        
        // Envoyer l'email
        emailService.sendPasswordResetEmail(user, tokenValue);
    }
    
    /**
     * Valider et réinitialiser le mot de passe
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Réinitialisation du mot de passe avec token");
        
        // Vérifier que les mots de passe correspondent
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("Les mots de passe ne correspondent pas");
        }
        
        // Chercher le token valide
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findValidToken(request.getToken(), LocalDateTime.now())
            .orElseThrow(() -> {
                log.warn("Token de reset invalide ou expiré");
                return new AuthenticationException("Lien de réinitialisation invalide ou expiré");
            });
        
        User user = resetToken.getUser();
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Marquer le token comme utilisé
        resetToken.setIsUsed(true);
        passwordResetTokenRepository.save(resetToken);
        
        log.info("Mot de passe réinitialisé pour: {}", user.getEmail());
        
        // Envoyer un email de confirmation
        emailService.sendPasswordResetConfirmationEmail(user);
    }
    
    /**
     * Nettoyer les tokens expirés
     * À appeler régulièrement (via un scheduled task)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Nettoyage des tokens expirés");
        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
}
