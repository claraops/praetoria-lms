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
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
        
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Cet utilisateur est inactif");
        }
        
        if (!user.isAccountNonLocked()) {
            throw new UsernameNotFoundException("Compte temporairement verrouillé");
        }   
        return UserDetailsImpl.build(user);
    }
    

    
    
    
}
