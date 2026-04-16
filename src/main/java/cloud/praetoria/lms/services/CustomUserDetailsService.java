/*package cloud.praetoria.lms.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.security.UserDetailsImpl;

import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.repositories.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge un utilisateur par son email pour l'authentification Spring Security.
     *
     * @param email l'email de l'utilisateur
     * @return l'entité User (qui implémente UserDetails).
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email
     ****************
    @Override
    @Transactional(readOnly = true)
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        log.debug("Chargement de l'utilisateur par email : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec l'email : {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
                });

        log.debug("Utilisateur trouvé : {} avec le rôle : {}", user.getEmail(), user.getRole().name());
        return UserDetailsImpl.build(user);
    }
}  **/
