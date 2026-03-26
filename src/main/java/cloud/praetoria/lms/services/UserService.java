package cloud.praetoria.lms.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.UpdateUserRequest;
import cloud.praetoria.lms.dtos.UserDTO;
import cloud.praetoria.lms.dtos.UserMapper;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.AuthenticationException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Récupérer un utilisateur par ID
     */
    public UserDTO getUserById(Long id) {
        log.info("Récupération de l'utilisateur: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return UserMapper.toDTO(user);
    }
    
    /**
     * Récupérer un utilisateur par email
     */
    public UserDTO getUserByEmail(String email) {
        log.info("Récupération de l'utilisateur par email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return UserMapper.toDTO(user);
    }
    
    /**
     * Mettre à jour le profil utilisateur
     */
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        log.info("Mise à jour de l'utilisateur: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        
        userRepository.save(user);
        log.info("Utilisateur mis à jour: {}", id);
        
        return UserMapper.toDTO(user);
    }
    
    /**
     * Changer le mot de passe
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("Changement de mot de passe pour: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Ancien mot de passe incorrect pour: {}", id);
            throw new AuthenticationException("Ancien mot de passe incorrect");
        }
        
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AuthenticationException("Le nouveau mot de passe doit être différent de l'ancien");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Mot de passe changé pour: {}", id);
    }
    
    /**
     * Activer/Désactiver un utilisateur
     */
    @Transactional
    public void toggleUserActive(Long id, boolean isActive) {
        log.info("Changement d'état actif pour: {} -> {}", id, isActive);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        user.setIsActive(isActive);
        userRepository.save(user);
    }
    
}
