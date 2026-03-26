package cloud.praetoria.lms.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.LoginRequest;
import cloud.praetoria.lms.dtos.LoginResponse;
import cloud.praetoria.lms.dtos.RefreshTokenRequest;
import cloud.praetoria.lms.dtos.RegisterRequest;
import cloud.praetoria.lms.dtos.UserMapper;
import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.entities.RefreshToken;
import cloud.praetoria.lms.entities.Role;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.enums.RoleName;
import cloud.praetoria.lms.exceptions.AuthenticationException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.OrganizationRepository;
import cloud.praetoria.lms.repositories.RefreshTokenRepository;
import cloud.praetoria.lms.repositories.RoleRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Enregistrer un nouvel étudiant via clé d'inscription
     */
    @Transactional
    public void registerUser(RegisterRequest request) {
        log.info("Inscription d'un nouvel utilisateur: {}", request.getEmail());
        
        // Vérifier que les mots de passe correspondent
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("Les mots de passe ne correspondent pas");
        }
        
        // Vérifier la clé d'inscription
        Organization organization = organizationRepository
            .findByRegistrationKey(request.getRegistrationKey())
            .orElseThrow(() -> {
                log.warn("Clé d'inscription invalide: {}", request.getRegistrationKey());
                return new AuthenticationException("Clé d'inscription invalide");
            });
        
        // Vérifier que l'organisation est active et la licence valide
        if (!organization.isLicenseValid()) {
            log.warn("Organisation inactive ou licence expirée: {}", organization.getName());
            throw new AuthenticationException("Cette organisation n'est plus active");
        }
        
        // Vérifier le quota d'étudiants
        if (organization.getMaxStudents() != null) {
            long currentCount = organizationRepository.countActiveStudents(organization);
            if (currentCount >= organization.getMaxStudents()) {
                log.warn("Quota d'étudiants atteint pour: {}", organization.getName());
                throw new AuthenticationException("Le nombre maximum d'étudiants a été atteint pour cette organisation");
            }
        }
        
        // Vérifier que l'email n'est pas déjà utilisé
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email déjà utilisé: {}", request.getEmail());
            throw new AuthenticationException("Cet email est déjà utilisé");
        }
        
        Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle ROLE_STUDENT non trouvé"));
        
        User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(studentRole)
            .organization(organization)
            .isActive(true)
            .failedLoginAttempts(0)
            .build();
        
        userRepository.save(user);
        log.info("Utilisateur créé avec succès: {} dans l'organisation: {}", 
            request.getEmail(), organization.getName());
    }
    
    /**
     * Connexion utilisateur
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Tentative de connexion: {}", request.getEmail());
        
        try {
            User user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé ou inactif: {}", request.getEmail());
                    return new AuthenticationException("Email ou mot de passe invalide");
                });
            
            // Vérifier que l'organisation de l'étudiant est toujours active
            if (!user.getOrganization().isLicenseValid()) {
                log.warn("Organisation inactive pour l'utilisateur: {}", request.getEmail());
                throw new AuthenticationException("Votre organisation n'est plus active. Contactez votre établissement.");
            }
            
            if (!user.isAccountNonLocked()) {
                log.warn("Compte verrouillé: {}", request.getEmail());
                throw new AuthenticationException("Compte temporairement verrouillé");
            }
            
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            RefreshToken refreshToken = generateRefreshToken(user);
            
            log.info("Connexion réussie: {}", request.getEmail());
            
            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(UserMapper.toDTO(user))
                .build();
                
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentification échouée: {}", request.getEmail());
            
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                    log.warn("Compte verrouillé après 5 tentatives: {}", request.getEmail());
                }
                
                userRepository.save(user);
            });
            
            throw new AuthenticationException("Email ou mot de passe invalide");
        }
    }
    
    /**
     * Générer un access token à partir d'un refresh token
     */
    @Transactional
    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {
        log.info("Refresh d'un access token");
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new AuthenticationException("Refresh token invalide"));
        
        if (refreshToken.isExpired() || refreshToken.getIsRevoked()) {
            log.warn("Refresh token invalide ou expiré");
            throw new AuthenticationException("Refresh token expiré ou révoqué");
        }
        
        User user = refreshToken.getUser();
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        
        // Révoquer l'ancien et générer un nouveau (rotation)
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);
        RefreshToken newRefreshToken = generateRefreshToken(user);
        
        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken.getToken())
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
            .user(UserMapper.toDTO(user))
            .build();
    }
    
    /**
     * Déconnexion utilisateur
     */
    @Transactional
    public void logout(User user) {
        log.info("Déconnexion de l'utilisateur: {}", user.getEmail());
        
        refreshTokenRepository.revokeAllUserTokens(user);
    }
    
    /**
     * Générer un refresh token
     */
    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        List<RefreshToken> userTokens = refreshTokenRepository.findByUser(user);
        if (userTokens.size() >= 3) {
            userTokens.forEach(token -> refreshTokenRepository.delete(token));
        }
        
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(jwtTokenProvider.generateRefreshToken(user))
            .expiresAt(LocalDateTime.now().plusDays(7))
            .isRevoked(false)
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
}
