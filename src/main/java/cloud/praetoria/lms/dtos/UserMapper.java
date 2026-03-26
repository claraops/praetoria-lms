package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.User;

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
            .fullName(user.getFullName())
            .role(user.getRole().getRoleName())
            .organizationName(user.getOrganization().getName())
            .isActive(user.getIsActive())
            .lastLogin(user.getLastLogin())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
    
}
