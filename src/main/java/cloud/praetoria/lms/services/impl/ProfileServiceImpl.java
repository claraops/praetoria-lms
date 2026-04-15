package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.PasswordUpdateRequest;
import cloud.praetoria.lms.dtos.ProfileUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.AuthenticationException;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMyProfile(Long userId) {
        log.debug("Récupération du profil de l'utilisateur: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(Long userId, ProfileUpdateRequest request) {
        log.info("Mise à jour du profil de l'utilisateur: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Cet email est déjà utilisé: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profil mis à jour pour l'utilisateur: {}", user.getEmail());

        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void updateMyPassword(Long userId, PasswordUpdateRequest request) {
        log.info("Changement de mot de passe pour l'utilisateur: {}", userId);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("Les mots de passe ne correspondent pas");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Tentative de changement de mot de passe avec ancien mot de passe incorrect pour: {}", user.getEmail());
            throw new AuthenticationException("Mot de passe actuel incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AuthenticationException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Mot de passe changé avec succès pour: {}", user.getEmail());
    }
}