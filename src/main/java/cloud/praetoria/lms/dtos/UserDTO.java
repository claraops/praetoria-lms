package cloud.praetoria.lms.dtos;

import java.time.LocalDateTime;

import cloud.praetoria.lms.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String fullName;
    
    private RoleName role;
    
    private String organizationName;
    
    private Boolean isActive;
    
    private LocalDateTime lastLogin;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
}
