package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole() != null ? user.getRole().getRoleName() : null)
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .promotionId(user.getPromotion() != null ? user.getPromotion().getId() : null)
                .promotionName(user.getPromotion() != null ? user.getPromotion().getName() : null)
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .isActive(userDTO.getIsActive())
                .lastLogin(userDTO.getLastLogin())
                .build();
    }
}