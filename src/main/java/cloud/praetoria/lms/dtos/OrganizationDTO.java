package cloud.praetoria.lms.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO  {
    
    private Long id;
    
    private String name;
    
    private String registrationKey;
    
    private String contactEmail;
    
    private Integer maxStudents;
    
    private Long currentStudentCount;
    
    private Boolean isActive;
    
    private LocalDateTime licenseExpiresAt;
    
    private LocalDateTime createdAt;
    
}
