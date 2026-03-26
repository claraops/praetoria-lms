package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPasswordResetRequest {
    
    @NotBlank(message = "Email est requis")
    @Email(message = "Email doit être valide")
    private String email;
    
}
