package cloud.praetoria.lms.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Chargement de l'utilisateur: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("Utilisateur non trouvé: {}", email);
                return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
            });
        
        if (!user.getIsActive()) {
            log.warn("Utilisateur inactif: {}", email);
            throw new UsernameNotFoundException("Cet utilisateur est inactif");
        }
        
        if (!user.isAccountNonLocked()) {
            log.warn("Compte verrouillé: {}", email);
            throw new UsernameNotFoundException("Compte temporairement verrouillé");
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(new SimpleGrantedAuthority(user.getRole().getRoleName().toString()))
            .accountExpired(false)          
            .accountLocked(false)           
            .credentialsExpired(false)      
            .disabled(false)              
            .build();
    }
    
}
