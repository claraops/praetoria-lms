package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.AssignBlocksRequest;
import cloud.praetoria.lms.dtos.RoleUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;
import cloud.praetoria.lms.dtos.UserUpdateRequest;
import cloud.praetoria.lms.entities.Block;
import cloud.praetoria.lms.entities.Promotion;
import cloud.praetoria.lms.entities.Role;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.enums.RoleName;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.BlockRepository;
import cloud.praetoria.lms.repositories.PromotionRepository;
import cloud.praetoria.lms.repositories.RoleRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BlockRepository blockRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsersByPromotionId(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée"));
        return userRepository.findByPromotion(promotion).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateRole(Long id, RoleUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Role newRole = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé"));
        user.setRole(newRole);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse toggleEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setIsActive(!user.getIsActive());
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse assignPromotion(Long userId, Long promotionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée"));
        user.setPromotion(promotion);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse removePromotion(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setPromotion(null);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse assignBlocks(Long userId, AssignBlocksRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Set<Block> blocks = request.getBlockIds().stream()
                .map(blockId -> blockRepository.findById(blockId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bloc non trouvé: " + blockId)))
                .collect(Collectors.toSet());
        //user.setBlocks(blocks);
        user.setBlocks(new ArrayList<>(blocks));
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}