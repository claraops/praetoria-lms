package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.Promotion;
import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private RoleName role;
    private Long organizationId;
    private String organizationName;
    private Long promotionId;
    private String promotionName;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        
        return UserResponse.builder()
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
}