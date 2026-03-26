package cloud.praetoria.lms.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrganizationRequest {
    
    @NotBlank(message = "Nom de l'organisation requis")
    @Size(min = 2, max = 200)
    private String name;
    
    @Email(message = "Email de contact doit être valide")
    private String contactEmail;
    
    private Integer maxStudents;
    
    private LocalDateTime licenseExpiresAt;
    
}
