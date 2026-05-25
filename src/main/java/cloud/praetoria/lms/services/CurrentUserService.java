package cloud.praetoria.lms.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.AuthenticationException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {

    private final UserRepository userRepository;

    /**
     * Récupère l'utilisateur authentifié depuis le contexte Spring Security
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Aucun utilisateur authentifié trouvé");
            throw new AuthenticationException("Utilisateur non authentifié");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + email));
    }

    /**
     * Récupère l'ID de l'utilisateur authentifié
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Vérifie si l'utilisateur authentifié a un rôle spécifique
     */
    public boolean hasRole(String roleName) {
        User user = getCurrentUser();
        return user.getRole().getRoleName().toString().equals(roleName);
    }
}
